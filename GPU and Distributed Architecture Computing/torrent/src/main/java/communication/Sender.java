package communication;


import communication.Torr2.Message;
import communication.Torr2.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender {

//    private final BlockingQueue<Pair<NodeId, Message>> toSendMessages;
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);


    public Sender() {

//        this.toSendMessages = new LinkedBlockingQueue<>();
    }

//    @Override
//    public void run() {
//
//        while (true) {
//            try {
//                Pair<NodeId, Message> pair = toSendMessages.take();
//                LOGGER.info("Sending message to process " + pair.getKey().getHost() + " " + pair.getKey().getPort());
//                sendMessage(pair.getKey(), pair.getValue());
//            } catch (InterruptedException e) {
//                LOGGER.error("Error extracting message from queue! " + e.getMessage());
//            }
//        }
//    }

    public Message sendMessage(NodeId destination, Message message) {

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
        if (socket == null)
            return null;
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeInt(message.toByteArray().length);
            dos.write(message.toByteArray());

            int expectedLength = dis.readInt();
            byte[] bytes = new byte[expectedLength];
            dis.readFully(bytes, 0, expectedLength);
            socket.close();
            return Message.parseFrom(bytes);

        } catch (IOException e) {
            LOGGER.error("Error writing message to DataOutputStream!\n" + e.getMessage());
        }
        return null;
    }
}
