package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;
import util.Pair;

import java.util.HashMap;
import java.util.Map;

import static util.Constants.BEB;
import static util.Constants.PL;

public class EpochConsensus extends Abstraction {

    private Pair<Integer, Value> pair;
    private Value tmpVal;
    private final Map<String, Pair<Integer, Value>> states;
    private int accepted;
    private final int ets;
    private boolean aborted;
    private static final Logger LOGGER = LoggerFactory.getLogger(EpochConsensus.class);

    public EpochConsensus(String abstractionId, AppSystem appSystem, int ets, Pair<Integer, Value> initialState) {

        super(abstractionId, appSystem);
        this.pair = initialState;
        this.tmpVal = Value.newBuilder().getDefaultInstanceForType();
        this.states = new HashMap<>();
        this.accepted = 0;
        this.aborted = false;
        this.ets = ets;
    }

    @Override
    public void registerChildren() {

        Abstraction pl = new PerfectLink(getChildAbstractionId(PL, ""), appSystem);
        appSystem.registerAbstraction(pl);
        Abstraction beb = new BestEffortBroadcast(getChildAbstractionId(BEB, ""), appSystem);
        appSystem.registerAbstraction(beb);
    }

    @Override
    public void handle(Message message) {

        if (this.aborted)
            return;
        switch (message.getType()) {
            case EP_PROPOSE -> {
                EpPropose epPropose = message.getEpPropose();
                epPropose(epPropose);
            }
            case BEB_DELIVER -> {
                BebDeliver bebDeliver = message.getBebDeliver();
                switch (bebDeliver.getMessage().getType()) {
                    case EP_INTERNAL_READ -> epInternalRead(bebDeliver.getSender());
                    case EP_INTERNAL_WRITE -> {
                        EpInternalWrite epInternalWrite = bebDeliver.getMessage().getEpInternalWrite();
                        epInternalWrite(bebDeliver.getSender(), epInternalWrite);
                    }
                    case EP_INTERNAL_DECIDED -> {
                        EpInternalDecided epInternalDecided = bebDeliver.getMessage().getEpInternalDecided();
                        epInternalDecided(epInternalDecided);
                    }
                }
            }
            case PL_DELIVER -> {
                PlDeliver plDeliver = message.getPlDeliver();
                switch (plDeliver.getMessage().getType()) {
                    case EP_INTERNAL_STATE -> {
                        EpInternalState epInternalState = plDeliver.getMessage().getEpInternalState();
                        epInternalState(plDeliver.getSender(), epInternalState);
                    }
                    case EP_INTERNAL_ACCEPT -> epInternalAccept();
                }
            }
            case EP_ABORT -> epAbort();
        }

    }

    private void epPropose(EpPropose epPropose) {

        this.tmpVal = epPropose.getValue();
        EpInternalRead epInternalRead = EpInternalRead.newBuilder()
                .build();
        Message message = Message.newBuilder()
                .setType(Message.Type.EP_INTERNAL_READ)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpInternalRead(epInternalRead)
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

        appSystem.trigger(wrapperMessage);
    }

    private void epInternalRead(ProcessId destination) {

        EpInternalState epInternalState = EpInternalState.newBuilder()
                .setValueTimestamp(pair.getKey())
                .setValue(pair.getValue())
                .build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EP_INTERNAL_STATE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpInternalState(epInternalState)
                .build();

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

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epInternalState timestamp" + epInternalState.getValueTimestamp() + " value " + epInternalState.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void epInternalWrite(ProcessId destination, EpInternalWrite epInternalWrite) {

        if (epInternalWrite.getValue().getDefined())
            this.pair = new Pair<>(ets, epInternalWrite.getValue());

        EpInternalAccept epInternalAccept = EpInternalAccept.newBuilder().build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EP_INTERNAL_ACCEPT)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpInternalAccept(epInternalAccept)
                .build();

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

    private void epInternalDecided(EpInternalDecided epInternalDecided) {

        EpDecide epDecide = EpDecide.newBuilder()
                .setEts(ets)
                .setValue(epInternalDecided.getValue())
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.EP_DECIDE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getParentAbstractionId(abstractionId))
                .setEpDecide(epDecide)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epDecide ets" + epDecide.getEts() + " value " + epDecide.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void epInternalState(ProcessId sender, EpInternalState epInternalState) {

        states.put(sender.getOwner() + "-" + sender.getRank(), new Pair<>(epInternalState.getValueTimestamp(), epInternalState.getValue()));

        if (states.size() > appSystem.getProcesses().size() / 2)
            return;

        Pair<Integer, Value> highestPair = highest();
        if (highestPair != null)
            tmpVal = highestPair.getValue();
        states.clear();

        EpInternalWrite epInternalWrite = EpInternalWrite.newBuilder()
                .setValue(tmpVal)
                .build();

        Message message = Message.newBuilder()
                .setType(Message.Type.EP_INTERNAL_WRITE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpInternalWrite(epInternalWrite)
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

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epInternalWrite value" + epInternalWrite.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void epInternalAccept() {

        this.accepted += 1;
        if (!(this.accepted > appSystem.getProcesses().size() / 2))
            return;
        this.accepted = 0;
        EpInternalDecided epInternalDecided = EpInternalDecided.newBuilder()
                .setValue(tmpVal)
                .build();
        Message message = Message.newBuilder()
                .setType(Message.Type.EP_INTERNAL_DECIDED)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setEpInternalDecided(epInternalDecided)
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

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  epInternalDecided value" + epInternalDecided.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void epAbort() {

        EpAborted epAborted = EpAborted.newBuilder()
                .setEts(ets)
                .setValueTimestamp(pair.getKey())
                .setValue(pair.getValue())
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.EP_ABORTED)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getParentAbstractionId(abstractionId))
                .setEpAborted(epAborted)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
        this.aborted = true;
    }

    private Pair<Integer, Value> highest() {

        Pair<Integer, Value> highestPair = null;
        for (Pair<Integer, Value> p : states.values()) {

            if ((highestPair == null || (p.getKey() > highestPair.getKey())) && p.getValue().getDefined())
                highestPair = p;
        }
        return highestPair;
    }
}
