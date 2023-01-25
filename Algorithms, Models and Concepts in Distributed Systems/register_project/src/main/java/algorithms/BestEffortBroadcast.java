package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import static util.Constants.PL;

public class BestEffortBroadcast extends Abstraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestEffortBroadcast.class);


    public BestEffortBroadcast(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
    }

    @Override
    public void registerChildren() {

        Abstraction pl = new PerfectLink(abstractionId + "." + PL, appSystem);
        appSystem.registerAbstraction(pl);
    }

    @Override
    public void handle(Message message) {

        LOGGER.info("Handle message with type: " + message.getType());
        switch (message.getType()) {
            case BEB_BROADCAST -> {
                BebBroadcast bebBroadcast = message.getBebBroadcast();
                this.plSend(bebBroadcast.getMessage());
            }
            case PL_DELIVER -> {
                PlDeliver plDeliver = message.getPlDeliver();
                this.bebDeliver(plDeliver.getMessage(), plDeliver.getSender(), getParentAbstractionId(message.getToAbstractionId()));
            }
        }
    }

    private void plSend(Message message) {

        appSystem.getProcesses().forEach(processId -> {
            PlSend plSend = PlSend.newBuilder()
                    .setDestination(processId)
                    .setMessage(message)
                    .build();

            Message wrapperMessage = Message.newBuilder()
                    .setType(Message.Type.PL_SEND)
                    .setFromAbstractionId(abstractionId)
                    .setToAbstractionId(getChildAbstractionId(PL, ""))
                    .setPlSend(plSend)
                    .build();
            LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId());
//            LOGGER.info("Message {}", wrapperMessage);
            appSystem.trigger(wrapperMessage);
        });
    }

    private void bebDeliver(Message toSend, ProcessId sender, String toAbstractionId) {

        BebDeliver bebDeliver = BebDeliver.newBuilder()
                .setMessage(toSend)
                .setSender(sender)
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.BEB_DELIVER)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(toAbstractionId)
                .setBebDeliver(bebDeliver)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + bebDeliver.getMessage().getType() + " inner from " + bebDeliver.getMessage().getFromAbstractionId() + " inner to " + bebDeliver.getMessage().getToAbstractionId());
//        LOGGER.info("Message {}", wrapperMessage);
        appSystem.trigger(wrapperMessage);
    }
}
