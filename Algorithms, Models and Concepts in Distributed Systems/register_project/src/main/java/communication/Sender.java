package communication;

import main.CommunicationProtocol.Message;
import main.CommunicationProtocol.ProcessId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender implements Runnable {

    private final BlockingQueue<Pair<ProcessId, Message>> toSendMessages;
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);


    public Sender() {

        toSendMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {

        while (true) {
            try {
                Pair<ProcessId, Message> pair = toSendMessages.take();
                LOGGER.info("Sending message to process " + pair.getKey().getHost() + " " + pair.getKey().getPort() + " with abstraction " + pair.getValue().getToAbstractionId());
                sendMessage(pair.getKey(), pair.getValue());
            } catch (InterruptedException e) {
                LOGGER.error("Error extracting message from queue! " + e.getMessage());
            }
        }
    }

    private void sendMessage(ProcessId destination, Message message) {

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(destination.getHost());
        } catch (UnknownHostException e) {
            LOGGER.error("Error connecting to host " + destination.getHost() + "!\n" + e.getMessage());
        }
        Socket socket = null;
        try {
            socket = new Socket(ip, destination.getPort());
        } catch (IOException e) {
            LOGGER.error("Error connecting to listening port " + destination.getPort() + "!\n" + e.getMessage());
        }
        try {
            DataOutputStream dos = new DataOutputStream(Objects.requireNonNull(socket).getOutputStream());
            dos.writeInt(message.toByteArray().length);
            dos.write(message.toByteArray());
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Error writing message to DataOutputStream!\n" + e.getMessage());
        }
    }

    public void addMessageToQueue(ProcessId destination, Message message) {

        toSendMessages.add(new Pair<>(destination, message));
    }
}
