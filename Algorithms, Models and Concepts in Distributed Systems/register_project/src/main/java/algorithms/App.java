package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;
import util.Constants;

import static util.Constants.*;

public class App extends Abstraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public App(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
    }

    @Override
    public void registerChildren() {

        Abstraction beb = new BestEffortBroadcast("app.beb", appSystem);
        appSystem.registerAbstraction(beb);
        Abstraction pl = new PerfectLink("app.pl", appSystem);
        appSystem.registerAbstraction(pl);
    }

    public void registerNNAR(String register) {

        Abstraction nnar = new NNAtomicRegister("app.nnar[" + register + "]", appSystem);
        appSystem.registerAbstraction(nnar);
    }

    public void registerUc(String topic, String epNo) {

        UniformConsensus uc = new UniformConsensus("app.uc[" + topic + "]", appSystem);
        if (!epNo.equals(""))
            uc.registerEp(epNo);
        appSystem.registerAbstraction(uc);
    }

    @Override
    public void handle(Message message) {

        LOGGER.info("Handle message with type: " + message.getType());
        switch (message.getType()) {
            case PL_DELIVER -> {
                PlDeliver plDeliver = message.getPlDeliver();
                switch (plDeliver.getMessage().getType()) {
                    case APP_BROADCAST -> this.bebBroadcast(plDeliver.getMessage(), message.getToAbstractionId());
                    case APP_WRITE -> this.nnarWrite(plDeliver.getMessage().getAppWrite());
                    case APP_READ -> this.nnarRead(plDeliver.getMessage().getAppRead());
                    case APP_PROPOSE -> this.appPropose(plDeliver.getMessage().getAppPropose());
                }
            }
            case BEB_DELIVER -> {
                BebDeliver bebDeliver = message.getBebDeliver();
                this.plSend(bebDeliver.getMessage(), bebDeliver.getSender());
            }
            case NNAR_WRITE_RETURN -> appWriteReturn(message.getFromAbstractionId());
            case NNAR_READ_RETURN -> appReadReturn(message.getFromAbstractionId(), message.getNnarReadReturn());
            case UC_DECIDE -> ucDecide(message.getUcDecide());
        }
    }

    private String extractRegister(String abstraction) {

        String substr = abstraction.substring(abstraction.indexOf('[') + 1);
        return substr.substring(0, substr.indexOf(']'));
    }

    private void bebBroadcast(Message toSend, String abstractionId) {
        AppValue appValue = AppValue.newBuilder()
                .setValue(toSend.getAppBroadcast().getValue())
                .build();
        Message appValueMessage = Message.newBuilder()
                .setType(Message.Type.APP_VALUE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setAppValue(appValue)
                .build();
        BebBroadcast bebBroadcast = BebBroadcast.newBuilder()
                .setMessage(appValueMessage)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.BEB_BROADCAST)
                .setFromAbstractionId(this.abstractionId)
                .setToAbstractionId(getChildAbstractionId(BEB, ""))
                .setBebBroadcast(bebBroadcast)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void plSend(Message message, ProcessId destination) {

        Message appValueMessage = Message.newBuilder()
                .setType(Message.Type.APP_VALUE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(HUB)
                .setAppValue(message.getAppValue())
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(Constants.hubProcessId)
                .setMessage(appValueMessage)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + plSend.getMessage().getType() + " inner from " + plSend.getMessage().getFromAbstractionId() + " inner to " + plSend.getMessage().getToAbstractionId());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void nnarWrite(AppWrite appWrite) {

        NnarWrite nnarWrite = NnarWrite.newBuilder()
                .setValue(appWrite.getValue())
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.NNAR_WRITE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(NNAR, appWrite.getRegister()))
                .setNnarWrite(nnarWrite)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner value " + nnarWrite.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void appWriteReturn(String abstraction) {

        AppWriteReturn appWriteReturn = AppWriteReturn.newBuilder()
                .setRegister(extractRegister(abstraction))
                .build();
        Message message = Message.newBuilder()
                .setType(Message.Type.APP_WRITE_RETURN)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(HUB)
                .setAppWriteReturn(appWriteReturn)
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(Constants.hubProcessId)
                .setMessage(message)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner register" + appWriteReturn.getRegister());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void nnarRead(AppRead appRead) {

        NnarRead nnarRead = NnarRead.newBuilder()
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.NNAR_READ)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(NNAR, appRead.getRegister()))
                .setNnarRead(nnarRead)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner register" + appRead.getRegister());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void appReadReturn(String abstraction, NnarReadReturn nnarReadReturn) {

        AppReadReturn appReadReturn = AppReadReturn.newBuilder()
                .setRegister(extractRegister(abstraction))
                .setValue(nnarReadReturn.getValue())
                .build();
        Message message = Message.newBuilder()
                .setType(Message.Type.APP_READ_RETURN)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(HUB)
                .setAppReadReturn(appReadReturn)
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(Constants.hubProcessId)
                .setMessage(message)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner register" + appReadReturn.getRegister() + " value " + appReadReturn.getValue().getV());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void appPropose(AppPropose appPropose) {

        UcPropose ucPropose = UcPropose.newBuilder()
                .setValue(appPropose.getValue())
                .build();

        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.UC_PROPOSE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(UC, appPropose.getTopic()))
                .setUcPropose(ucPropose)
                .build();

        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner register" + appPropose.getTopic());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }

    private void ucDecide(UcDecide ucDecide) {

        AppDecide appDecide = AppDecide.newBuilder()
                .setValue(ucDecide.getValue())
                .build();

        Message message = Message.newBuilder()
                .setType(Message.Type.APP_DECIDE)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(HUB)
                .setAppDecide(appDecide)
                .build();

        PlSend plSend = PlSend.newBuilder()
                .setDestination(Constants.hubProcessId)
                .setMessage(message)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_SEND)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(getChildAbstractionId(PL, ""))
                .setPlSend(plSend)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + ";  value" + appDecide.getValue());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }
}
