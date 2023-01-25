package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import java.util.ArrayList;
import java.util.List;

import static util.Constants.EPFD;

public class EventualLeaderDetector extends Abstraction {

    private final List<ProcessId> suspected;
    private ProcessId leader;
    private final List<ProcessId> processes;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventualLeaderDetector.class);

    public EventualLeaderDetector(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
        this.suspected = new ArrayList<>();
        this.leader = null;
        this.processes = appSystem.getProcesses();
    }

    @Override
    public void registerChildren() {

        Abstraction epfd = new EventuallyPerfectFailureDetector(abstractionId + "." + EPFD, appSystem);
        appSystem.registerAbstraction(epfd);
    }

    @Override
    public void handle(Message message) {

        LOGGER.info("Handle message with type: " + message.getType());
        switch (message.getType()) {
            case EPFD_SUSPECT -> {
                EpfdSuspect epfdSuspect = message.getEpfdSuspect();
                suspected.add(epfdSuspect.getProcess());
                checkLeader();
            }
            case EPFD_RESTORE -> {
                EpfdRestore epfdRestore = message.getEpfdRestore();
                suspected.remove(epfdRestore.getProcess());
                checkLeader();
            }
        }
    }

    private ProcessId maxRank() {

        ProcessId maxRankProc = null;
        for (ProcessId p : processes) {
            if (!suspected.contains(p))
                maxRankProc = p;
        }
        return maxRankProc;
    }

    private void checkLeader() {

        ProcessId maxRankProc = maxRank();
        if (!leader.equals(maxRankProc)) {
            leader = maxRankProc;

            EldTrust eldTrust = EldTrust.newBuilder()
                    .setProcess(leader)
                    .build();

            Message wrapperMessage = Message.newBuilder()
                    .setType(Message.Type.ELD_TRUST)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getParentAbstractionId(abstractionId))
                    .setEldTrust(eldTrust)
                    .build();

            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  eldTrust processId" + eldTrust.getProcess().getOwner() + " " + eldTrust.getProcess().getRank());
//        LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);
        }
    }
}
