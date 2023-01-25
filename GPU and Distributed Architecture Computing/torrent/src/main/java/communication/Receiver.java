package communication;

import communication.Torr2.Message;
import node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver implements Runnable {

    private final Node node;
    private final int port;
    private ExecutorService executor = Executors.newCachedThreadPool();
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(Node node, int port) {

        this.node = node;
        this.port = port;
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
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            int expectedLength = dis.readInt();
            byte[] bytes = new byte[expectedLength];
            dis.readFully(bytes, 0, expectedLength);
//            if (expectedLength != actualLength) {
//                logger.error("Message length is not the one expected! Expected: " + expectedLength + ", Actual: ");
//            }
            Message message = Message.parseFrom(bytes);
            Message toSend = node.handleRequest(message);
            dos.writeInt(toSend.toByteArray().length);
            dos.write(toSend.toByteArray());

        } catch (IOException e) {
            serverSocket.close();
            e.printStackTrace();
        }
    }
}
