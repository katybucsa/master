package communication;

import main.CommunicationProtocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Receiver implements Runnable {

    private final int port;
    private final BlockingQueue<Message> networkMessages;
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(int port, BlockingQueue<Message> networkMessages) {

        this.port = port;
        this.networkMessages = networkMessages;
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true)
                receiveMessage(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(ServerSocket serverSocket) throws IOException {

        try {
            Socket socket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int expectedLength = dis.readInt();
            byte[] bytes = new byte[expectedLength];
            dis.readFully(bytes, 0, expectedLength);
//            if (expectedLength != actualLength) {
//                logger.error("Message length is not the one expected! Expected: " + expectedLength + ", Actual: ");
//            }
            Message message = Message.parseFrom(bytes);
            networkMessages.add(message);

        } catch (IOException e) {
            serverSocket.close();
            e.printStackTrace();
        }
    }
}
