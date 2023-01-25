package algorithms;

import api.Abstraction;
import main.CommunicationProtocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import process.AppSystem;

import java.util.UUID;

public class PerfectLink extends Abstraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfectLink.class);

    public PerfectLink(String abstractionId, AppSystem appSystem) {

        super(abstractionId, appSystem);
    }

    public void registerChildren() {
    }

    @Override
    public void handle(Message message) {

        LOGGER.info("Handle message with type: " + message.getType());
        switch (message.getType()) {
            case NETWORK_MESSAGE -> this.plDeliver(message, getParentAbstractionId(abstractionId));
            case PL_SEND -> {
                PlSend plSend = message.getPlSend();
                this.plSend(plSend.getDestination(), message);
            }
        }
    }

    private void plDeliver(Message message, String toAbstractionId) {

        ProcessId sender = appSystem.getProcessByHostAndPort(message.getNetworkMessage().getSenderHost(), message.getNetworkMessage().getSenderListeningPort());
        PlDeliver plDeliver = PlDeliver.newBuilder()
                .setSender(sender)
                .setMessage(message.getNetworkMessage().getMessage())
                .build();
        Message wrapperMessage = Message.newBuilder()
                .setType(Message.Type.PL_DELIVER)
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(toAbstractionId)
                .setPlDeliver(plDeliver)
                .build();
        LOGGER.info(wrapperMessage.getType() + " from " + abstractionId + " to " + wrapperMessage.getToAbstractionId() + "; inner type " + plDeliver.getMessage().getType() + " inner from " + plDeliver.getMessage().getFromAbstractionId() + " inner to " + plDeliver.getMessage().getToAbstractionId());
        LOGGER.info("Message {}", wrapperMessage);
        this.appSystem.trigger(wrapperMessage);
    }

    private void plSend(ProcessId destination, Message message) {

        NetworkMessage networkMessage = NetworkMessage.newBuilder()
                .setSenderHost(appSystem.getHost())
                .setSenderListeningPort(appSystem.getPort())
                .setMessage(message.getPlSend().getMessage())
                .build();

        Message newMessage = Message.newBuilder()
                .setType(Message.Type.NETWORK_MESSAGE)
                .setMessageUuid(UUID.randomUUID().toString())
                .setFromAbstractionId(abstractionId)
                .setToAbstractionId(abstractionId)
                .setSystemId(appSystem.getSystemId())
                .setNetworkMessage(networkMessage)
                .build();
        LOGGER.info(newMessage.getType() + " from " + abstractionId + " to " + newMessage.getToAbstractionId() + "; inner type " + networkMessage.getMessage().getType() + " inner from " + networkMessage.getMessage().getFromAbstractionId() + " inner to " + networkMessage.getMessage().getToAbstractionId());
        LOGGER.info("Send message from " + abstractionId + "to " + newMessage.getToAbstractionId() + " through network");
        LOGGER.info("Message {}", newMessage);
        this.appSystem.send(destination, newMessage);
    }
}
