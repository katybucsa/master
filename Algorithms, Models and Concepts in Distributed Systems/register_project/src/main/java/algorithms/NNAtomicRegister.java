package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import main.CommunicationProtocol.Message.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static util.Constants.BEB;
import static util.Constants.PL;

class Tuple {

    private int ts;
    private int wr;
    private Value val;

    public Tuple(int ts, int wr, Value val) {

        this.ts = ts;
        this.wr = wr;
        this.val = val;
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getWr() {
        return wr;
    }

    public void setWr(int wr) {
        this.wr = wr;
    }

    public Value getVal() {
        return val;
    }

    public void setVal(Value val) {
        this.val = val;
    }
}

public class NNAtomicRegister extends Abstraction {

    private Tuple tuple;
    private int acks;
    private Value writeVal;
    private int rid;
    private final Map<String, Tuple> readList;
    private Value readVal;
    private boolean reading;
    private static final Logger LOGGER = LoggerFactory.getLogger(NNAtomicRegister.class);

    public NNAtomicRegister(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
        this.tuple = new Tuple(0, 0, Value.newBuilder().getDefaultInstanceForType());
        this.acks = 0;
        this.writeVal = null;
        this.rid = 0;
        this.readList = new HashMap<>();
        this.readVal = null;
        this.reading = false;
    }

    @Override
    public void registerChildren() {

        Abstraction beb = new BestEffortBroadcast(abstractionId + "." + BEB, appSystem);
        appSystem.registerAbstraction(beb);
        Abstraction pl = new PerfectLink(abstractionId + "." + PL, appSystem);
        appSystem.registerAbstraction(pl);
    }

    @Override
    public void handle(Message message) {

        switch (message.getType()) {
            case NNAR_READ -> read();
            case BEB_DELIVER -> {
                BebDeliver bebDeliver = message.getBebDeliver();
                switch (bebDeliver.getMessage().getType()) {
                    case NNAR_INTERNAL_READ -> bebDeliverRead(bebDeliver.getSender(), bebDeliver.getMessage().getNnarInternalRead().getReadId());
                    case NNAR_INTERNAL_WRITE -> {
                        NnarInternalWrite nnariw = bebDeliver.getMessage().getNnarInternalWrite();
                        bebDeliverWrite(bebDeliver.getSender(), nnariw.getReadId(), nnariw.getTimestamp(), nnariw.getWriterRank(), nnariw.getValue());
                    }
                }
            }
            case NNAR_WRITE -> write(message.getNnarWrite().getValue());
            case PL_DELIVER -> {
                PlDeliver plDeliver = message.getPlDeliver();
                switch (plDeliver.getMessage().getType()) {
                    case NNAR_INTERNAL_VALUE -> {
                        NnarInternalValue nnariv = plDeliver.getMessage().getNnarInternalValue();
                        if (nnariv.getReadId() > this.rid) {
                            appSystem.trigger(message);
                            break;
                        } else if (nnariv.getReadId() < this.rid)
                            break;
                        plDeliverValue(plDeliver.getSender(), nnariv);
                    }
                    case NNAR_INTERNAL_ACK -> {
                        NnarInternalAck nnaria = plDeliver.getMessage().getNnarInternalAck();
                        if (nnaria.getReadId() > this.rid) {
                            appSystem.trigger(message);
                            break;
                        } else if (nnaria.getReadId() < this.rid)
                            break;
                        plDeliverAck();
                    }
                }
            }
        }
    }

    private void read() {

        this.rid++;
        this.acks = 0;
        this.readList.clear();
        this.reading = true;

        NnarInternalRead nnarInternalRead = NnarInternalRead.newBuilder()
                .setReadId(rid)
                .build();
        Message message = Message.newBuilder()
                .setType(Type.NNAR_INTERNAL_READ)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setNnarInternalRead(nnarInternalRead)
                .build();
        BebBroadcast bebBroadcast = BebBroadcast.newBuilder()
                .setMessage(message)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Type.BEB_BROADCAST)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(BEB, ""))
                .setBebBroadcast(bebBroadcast)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + bebBroadcast.getMessage().getType() + " inner from " + bebBroadcast.getMessage().getFromAbstractionId() + " inner to " + bebBroadcast.getMessage().getToAbstractionId());
        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void write(Value value) {

        this.rid++;
        this.writeVal = value;
        this.acks = 0;
        this.readList.clear();

        NnarInternalRead nnarInternalRead = NnarInternalRead.newBuilder()
                .setReadId(rid)
                .build();

        Message message = Message.newBuilder()
                .setType(Type.NNAR_INTERNAL_READ)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setNnarInternalRead(nnarInternalRead)
                .build();

        BebBroadcast bebBroadcast = BebBroadcast.newBuilder()
                .setMessage(message)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Type.BEB_BROADCAST)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(BEB, ""))
                .setBebBroadcast(bebBroadcast)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + bebBroadcast.getMessage().getType() + " inner from " + bebBroadcast.getMessage().getFromAbstractionId() + " inner to " + bebBroadcast.getMessage().getToAbstractionId());
        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void bebDeliverRead(ProcessId destination, int readId) {

        NnarInternalValue nnarInternalValue = NnarInternalValue.newBuilder()
                .setReadId(readId)
                .setTimestamp(tuple.getTs())
                .setWriterRank(tuple.getWr())
                .setValue(tuple.getVal())
                .build();

        Message message = Message.newBuilder()
                .setType(Type.NNAR_INTERNAL_VALUE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setNnarInternalValue(nnarInternalValue)
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(destination)
                .setMessage(message)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + plSend.getMessage().getType() + " inner from " + plSend.getMessage().getFromAbstractionId() + " inner to " + plSend.getMessage().getToAbstractionId());
        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void bebDeliverWrite(ProcessId destination, int readId, int ts, int wr, Value val) {

        if ((ts > this.tuple.getTs() || (ts == this.tuple.getTs() && wr > this.tuple.getWr())) && val.getDefined()) {
            this.tuple = new Tuple(ts, wr, Value.newBuilder().setDefined(true).setV(val.getV()).build());
        }

        NnarInternalAck nnarInternalAck = NnarInternalAck.newBuilder()
                .setReadId(readId)
                .build();

        Message message = Message.newBuilder()
                .setType(Type.NNAR_INTERNAL_ACK)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setNnarInternalAck(nnarInternalAck)
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(destination)
                .setMessage(message)
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + plSend.getMessage().getType() + " inner from " + plSend.getMessage().getFromAbstractionId() + " inner to " + plSend.getMessage().getToAbstractionId());
        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void plDeliverValue(ProcessId sender, NnarInternalValue nnariv) {

        this.readList.put(sender.getOwner() + "-" + sender.getIndex(), new Tuple(nnariv.getTimestamp(), nnariv.getWriterRank(), nnariv.getValue()));

        if (this.readList.size() > appSystem.getProcesses().size() / 2) {
            Tuple highestTuple = highest();
            this.readVal = highestTuple.getVal();
            this.readList.clear();

            NnarInternalWrite nnariw;
            if (this.reading) {
                nnariw = NnarInternalWrite.newBuilder()
                        .setReadId(rid)
                        .setTimestamp(highestTuple.getTs())
                        .setWriterRank(highestTuple.getWr())
                        .setValue(this.readVal)
                        .build();
            } else {
                nnariw = NnarInternalWrite.newBuilder()
                        .setReadId(rid)
                        .setTimestamp(highestTuple.getTs() + 1)
                        .setWriterRank(this.appSystem.getRank())
                        .setValue(this.writeVal)
                        .build();
            }

            Message message = Message.newBuilder()
                    .setType(Type.NNAR_INTERNAL_WRITE)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(abstractionId)
                    .setNnarInternalWrite(nnariw)
                    .build();
            BebBroadcast bebBroadcast = BebBroadcast.newBuilder()
                    .setMessage(message)
                    .build();
            Message wrapperMessage = Message.newBuilder()
                    .setType(Type.BEB_BROADCAST)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getChildAbstractionId(BEB, ""))
                    .setBebBroadcast(bebBroadcast)
                    .build();

            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + bebBroadcast.getMessage().getType() + " inner from " + bebBroadcast.getMessage().getFromAbstractionId() + " inner to " + bebBroadcast.getMessage().getToAbstractionId());
            LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);
        }
    }

    private void plDeliverAck() {

        acks++;
        if (acks > appSystem.getProcesses().size() / 2) {
            acks = 0;
            Message message;
            if (reading) {
                reading = false;
                NnarReadReturn nnarReadReturn = NnarReadReturn.newBuilder()
                        .setValue(readVal)
                        .build();
                message = Message.newBuilder()
                        .setType(Type.NNAR_READ_RETURN)
                        .setFromAbstractionId(abstractionId)
                        .setToAbstractionId(getParentAbstractionId(abstractionId))
                        .setNnarReadReturn(nnarReadReturn)
                        .build();

                LOGGER.info(message.getType() + " from " + abstractionId + " to " + message.getToAbstractionId() + "; inner value " + nnarReadReturn.getValue());
                LOGGER.info("Message {}", message);
                appSystem.trigger(message);
            } else {
                message = Message.newBuilder()
                        .setType(Type.NNAR_WRITE_RETURN)
                        .setFromAbstractionId(abstractionId)
                        .setToAbstractionId(getParentAbstractionId(abstractionId))
                        .setNnarWriteReturn(NnarWriteReturn.newBuilder().build())
                        .build();

                LOGGER.info(message.getType() + " from " + abstractionId + " to " + message.getToAbstractionId());
                LOGGER.info("Message {}", message);
                appSystem.trigger(message);
            }
        }
    }

    private Tuple highest() {

        int highestTs = 0, highestRank = 0;
        Tuple highestTuple = new ArrayList<>(readList.values()).get(0);
        for (Tuple t : this.readList.values()) {
            if ((t.getTs() > highestTs ||
                    (t.getTs() == highestTs && t.getWr() > highestRank)) && t.getVal().getDefined()) {
                highestTs = t.getTs();
                highestRank = t.getWr();
                highestTuple = t;
            }
        }
        return highestTuple;
    }
}
