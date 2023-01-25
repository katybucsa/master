package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static util.Constants.PL;

public class EventuallyPerfectFailureDetector extends Abstraction {

    private final List<ProcessId> processes;
    private final BlockingQueue<ProcessId> alive;
    private final List<ProcessId> suspected;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventuallyPerfectFailureDetector.class);

    public EventuallyPerfectFailureDetector(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
        this.processes = appSystem.getProcesses();
        this.alive = new LinkedBlockingQueue<>(processes);
        this.suspected = new ArrayList<>();
        Timer timer = new Timer();
        timer.schedule(new OnTimeoutTask(timer, 2000), 2000, 2000);
    }

    @Override
    public void registerChildren() {

        Abstraction pl = new PerfectLink(abstractionId + "." + PL, appSystem);
        appSystem.registerAbstraction(pl);
    }

    @Override
    public void handle(Message message) {

        LOGGER.info("Handle message with type: " + message.getType());
        if (!message.getType().equals(Message.Type.PL_DELIVER)) {
            LOGGER.info("Wrong message type!Expected PL_DELIVER\n");
            return;
        }
        PlDeliver plDeliver = message.getPlDeliver();
        switch (plDeliver.getMessage().getType()) {
            case EPFD_INTERNAL_HEARTBEAT_REQUEST -> sendHeartbeatReply(plDeliver.getSender());
            case EPFD_INTERNAL_HEARTBEAT_REPLY -> alive.add(plDeliver.getSender());
        }
    }

    private void sendHeartbeatReply(ProcessId destination) {

        EpfdInternalHeartbeatReply eihr = EpfdInternalHeartbeatReply.newBuilder().build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EPFD_INTERNAL_HEARTBEAT_REPLY)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpfdInternalHeartbeatReply(eihr)
                .build();

        plSend(message, destination);
    }

    private void sendHeartbeatRequest(ProcessId destination) {

        EpfdInternalHeartbeatRequest eihr = EpfdInternalHeartbeatRequest.newBuilder().build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EPFD_INTERNAL_HEARTBEAT_REQUEST)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpfdInternalHeartbeatRequest(eihr)
                .build();

        plSend(message, destination);
    }

    private void plSend(Message message, ProcessId destination) {


        PlSend plSend = PlSend.newBuilder()
                .setDestination(destination)
                .setMessage(message)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    class OnTimeoutTask extends TimerTask {

        private Timer timer;
        private int delay;

        public OnTimeoutTask(Timer timer, int delay) {
            this.timer = timer;
            this.delay = delay;
        }

        @Override
        public void run() {
            if (notEmptyIntersection()) {
                this.delay *= 2;
            }
            for (ProcessId p : processes) {
                if (!alive.contains(p) && !suspected.contains(p)) {
                    suspected.add(p);
                    suspect(p);
                } else if (alive.contains(p) && suspected.contains(p)) {
                    suspected.remove(p);
                    restore(p);
                }
                sendHeartbeatRequest(p);
            }
            alive.clear();
            timer.cancel();
            timer = new Timer();
            timer.schedule(this, delay, delay);
        }
    }

    private boolean notEmptyIntersection() {

        for (ProcessId p : processes) {
            if (suspected.contains(p))
                return true;
        }
        return false;
    }

    private void suspect(ProcessId suspected) {

        EpfdSuspect epfdSuspect = EpfdSuspect.newBuilder()
                .setProcess(suspected)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.EPFD_SUSPECT)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getParentAbstractionId(abstractionId))
                .setEpfdSuspect(epfdSuspect)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epfdSuspect process " + epfdSuspect.getProcess().getOwner() + " " + epfdSuspect.getProcess().getRank());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void restore(ProcessId toRestore) {

        EpfdRestore epfdRestore = EpfdRestore.newBuilder()
                .setProcess(toRestore)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.EPFD_RESTORE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getParentAbstractionId(abstractionId))
                .setEpfdRestore(epfdRestore)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epfdRestore process " + epfdRestore.getProcess().getOwner() + " " + epfdRestore.getProcess().getRank());
        //        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }
}
