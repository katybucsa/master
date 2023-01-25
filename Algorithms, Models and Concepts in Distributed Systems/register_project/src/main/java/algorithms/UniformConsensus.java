package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;
import util.Pair;

import static util.Constants.EC;
import static util.Constants.EP;

public class UniformConsensus extends Abstraction {

    private Value val;
    private boolean proposed;
    private boolean decided;
    private Pair<Integer, ProcessId> initialLeader;
    private Pair<Integer, ProcessId> leaderOnTimestamp;
    private static final Logger LOGGER = LoggerFactory.getLogger(UniformConsensus.class);


    public UniformConsensus(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
        this.val = Value.newBuilder().getDefaultInstanceForType();
        this.proposed = false;
        this.decided = false;
        this.initialLeader = new Pair<>(0, getMaxRankProc());
        this.leaderOnTimestamp = new Pair<>(0, null);
    }

    @Override
    public void registerChildren() {

        Abstraction ec = new EpochChange(getChildAbstractionId(EC, ""), appSystem);
        appSystem.registerAbstraction(ec);
    }

    public void registerEp(String no) {

        Abstraction ep = new EpochConsensus(getChildAbstractionId(EP, no), appSystem, Integer.parseInt(no), new Pair<>(0, Value.newBuilder().getDefaultInstanceForType()));
        appSystem.registerAbstraction(ep);
    }

    private void initializeNewEpInstance(int ets, Pair<Integer, Value> state) {

        Abstraction ep = new EpochConsensus(getChildAbstractionId(EP, "" + ets), appSystem, ets, state);
        appSystem.registerAbstraction(ep);
    }

    @Override
    public void handle(Message message) {

        switch (message.getType()) {
            case UC_PROPOSE -> this.val = message.getUcPropose().getValue();

            case EC_START_EPOCH -> ecStartEpoch(message.getEcStartEpoch());
            case EP_ABORTED -> {
                int ts = message.getEpAborted().getEts();
                if (ts != initialLeader.getKey()) {
                    LOGGER.info("ts != ets. Put message in queue");
                    return;
                }
                epAborted(message.getEpAborted());
            }
            case EP_DECIDE -> {
                int ts = message.getEpDecide().getEts();
                if (ts != initialLeader.getKey()) {
                    LOGGER.info("ts != ets. Put message in queue");
                    return;
                }
                epDecide(message.getEpDecide());
            }
        }
    }

    private void ecStartEpoch(EcStartEpoch ecStartEpoch) {

        this.leaderOnTimestamp = new Pair<>(ecStartEpoch.getNewTimestamp(), ecStartEpoch.getNewLeader());

        EpAbort epAbort = EpAbort.newBuilder().build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.EP_ABORT)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(EP, initialLeader.getKey().toString()))
                .setEpAbort(epAbort)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId());
        //        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void epAborted(EpAborted epAborted) {

        this.initialLeader = new Pair<>(leaderOnTimestamp.getKey(), leaderOnTimestamp.getValue());
        this.proposed = false;
        initializeNewEpInstance(initialLeader.getKey(), new Pair<>(epAborted.getValueTimestamp(), epAborted.getValue()));
        if (initialLeader.getValue().getRank() == appSystem.getCurrentProcess().getRank()
                && this.val.getDefined() && !proposed) {
            proposed = true;

            EpPropose epPropose = EpPropose.newBuilder()
                    .setValue(this.val)
                    .build();

            Message wrapperMessage = Message.newBuilder()
                    .setType(Message.Type.EP_PROPOSE)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getChildAbstractionId(EP, "" + epAborted.getEts()))
                    .setEpPropose(epPropose)
                    .build();

            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epfdRestore value " + epPropose.getValue());
            //        LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);
        }
    }

    private void epDecide(EpDecide epDecide) {

        if (!this.decided) {
            this.decided = true;
            UcDecide ucDecide = UcDecide.newBuilder()
                    .setValue(epDecide.getValue())
                    .build();

            Message wrapperMessage = Message.newBuilder()
                    .setType(Message.Type.UC_DECIDE)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getParentAbstractionId(abstractionId))
                    .setUcDecide(ucDecide)
                    .build();

            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  ucDecide value " + ucDecide.getValue().getV());
            //        LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);
        }
    }

    private ProcessId getMaxRankProc() {

        ProcessId max = appSystem.getProcesses().get(0);
        for (ProcessId p : appSystem.getProcesses()) {
            if (p.getRank() > max.getRank()) {
                max = p;
            }
        }
        return max;
    }
}
