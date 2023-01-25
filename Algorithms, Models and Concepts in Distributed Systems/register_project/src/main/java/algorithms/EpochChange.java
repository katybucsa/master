package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import java.util.List;

import static util.Constants.*;

public class EpochChange extends Abstraction {

    private final List<ProcessId> processes;
    private ProcessId trusted;
    private int lastts;
    private int ts;
    private static final Logger LOGGER = LoggerFactory.getLogger(EpochChange.class);

    public EpochChange(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
        this.processes = appSystem.getProcesses();
        this.trusted = getMaxRankProc();
        this.lastts = 0;
        this.ts = appSystem.getRank();
    }

    @Override
    public void registerChildren() {

        Abstraction eld = new EventualLeaderDetector(getChildAbstractionId(ELD, ""), appSystem);
        appSystem.registerAbstraction(eld);
        Abstraction beb = new BestEffortBroadcast(getChildAbstractionId(BEB, ""), appSystem);
        appSystem.registerAbstraction(beb);
        Abstraction pl = new PerfectLink(getChildAbstractionId(PL, ""), appSystem);
        appSystem.registerAbstraction(pl);
    }

    @Override
    public void handle(Message message) {

        switch (message.getType()) {
            case ELD_TRUST -> {
                EldTrust eldTrust = message.getEldTrust();
                this.trusted = eldTrust.getProcess();
                if (trusted.getRank() == appSystem.getCurrentProcess().getRank()) {
                    ts += processes.size();
                    bebBroadcast();
                }
            }
            case BEB_DELIVER -> {
                BebDeliver bebDeliver = message.getBebDeliver();
                if (!bebDeliver.getMessage().getType().equals(Message.Type.EC_INTERNAL_NEW_EPOCH)) {

                    LOGGER.error("Wrong internal message type! Expected EC_INTERNAL_NEW_EPOCH");
                    return;
                }
                startEpochOrPlSend(bebDeliver);
            }
            case PL_DELIVER -> bebBroadcast();
        }

    }

    private void bebBroadcast() {
        if (trusted.getRank() != appSystem.getCurrentProcess().getRank())
            return;

        ts += processes.size();
        EcInternalNewEpoch ecInternalNewEpoch = EcInternalNewEpoch.newBuilder()
                .setTimestamp(ts)
                .build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EC_INTERNAL_NEW_EPOCH)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEcInternalNewEpoch(ecInternalNewEpoch)
                .build();

        BebBroadcast bebBroadcast = BebBroadcast.newBuilder()
                .setMessage(message)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.BEB_BROADCAST)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(BEB, ""))
                .setBebBroadcast(bebBroadcast)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epInternalNewEpoch timestamp" + ecInternalNewEpoch.getTimestamp());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void startEpochOrPlSend(BebDeliver bebDeliver) {

        EcInternalNewEpoch ecInternalNewEpoch = bebDeliver.getMessage().getEcInternalNewEpoch();
        if (bebDeliver.getSender().getRank() == trusted.getRank() && ecInternalNewEpoch.getTimestamp() > this.lastts) {
            this.lastts = ecInternalNewEpoch.getTimestamp();

            EcStartEpoch ecStartEpoch = EcStartEpoch.newBuilder()
                    .setNewTimestamp(this.lastts)
                    .setNewLeader(bebDeliver.getSender())
                    .build();

            Message wrapperMessage = Message.newBuilder()
                    .setType(Message.Type.EC_START_EPOCH)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getParentAbstractionId(abstractionId))
                    .setEcStartEpoch(ecStartEpoch)
                    .build();

            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  ecEpochStart newTs" + ecStartEpoch.getNewTimestamp() + " newL " + ecStartEpoch.getNewLeader());
//        LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);

        } else {
            EcInternalNack ecInternalNack = EcInternalNack.newBuilder()
                    .build();
            Message message = Message.newBuilder()
                    .setType(Message.Type.EC_INTERNAL_NACK)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(abstractionId)
                    .setEcInternalNack(ecInternalNack)
                    .build();
            PlSend plSend = PlSend.newBuilder()
                    .setDestination(bebDeliver.getSender())
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
    }

    private ProcessId getMaxRankProc() {

        ProcessId max = processes.get(0);
        for (ProcessId p : processes) {
            if (p.getRank() > max.getRank()) {
                max = p;
            }
        }
        return max;
    }
}
