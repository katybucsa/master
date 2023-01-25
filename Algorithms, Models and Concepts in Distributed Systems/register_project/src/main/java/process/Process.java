package process;

import communication.Receiver;
import communication.Sender;
import main.CommunicationProtocol.Message;
import main.CommunicationProtocol.Message.Type;
import main.CommunicationProtocol.NetworkMessage;
import main.CommunicationProtocol.ProcRegistration;
import main.CommunicationProtocol.ProcessId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static util.Constants.APP;
import static util.Constants.systemId;

public class Process implements Runnable {

    private final int port;
    private final String owner;
    private final int index;
    private final Map<String, AppSystem> systems;
    private final BlockingQueue<Message> networkMessages;
    private final Sender sender;
    private final Receiver receiver;
    private static final Logger LOGGER = LoggerFactory.getLogger(Process.class);

    public Process(int port, String owner, int index) {

        this.port = port;
        this.owner = owner;
        this.index = index;
        this.systems = new HashMap<>();
        this.networkMessages = new LinkedBlockingQueue<>();
        this.sender = new Sender();
        this.receiver = new Receiver(port, networkMessages);
    }

    private ProcRegistration buildProcRegistration() {

        return ProcRegistration
                .newBuilder()
                .setOwner(owner)
                .setIndex(index)
                .build();
    }


    private Message buildProcRegistrationMessage(ProcRegistration procRegistration) {

        return Message
                .newBuilder()
                .setType(Type.PROC_REGISTRATION)
                .setProcRegistration(procRegistration)
                .setToAbstractionId(APP)
                .setSystemId(systemId)
                .build();
    }

    private NetworkMessage buildNetworkMessage(Message message) {

        return NetworkMessage
                .newBuilder()
                .setSenderHost(Constants.clientHost)
                .setSenderListeningPort(port)
                .setMessage(message)
                .build();
    }

    private Message buildNetworkMessageWrapper(NetworkMessage networkMessage) {

        return Message
                .newBuilder()
                .setType(Type.NETWORK_MESSAGE)
                .setNetworkMessage(networkMessage)
                .build();
    }

    private void initializeCommunication() {

        ProcRegistration procRegistration = buildProcRegistration();

        Message procRegistrationMessage = buildProcRegistrationMessage(procRegistration);
        NetworkMessage networkMessage = buildNetworkMessage(procRegistrationMessage);

        Message networkMessageWrapper = buildNetworkMessageWrapper(networkMessage);
        sendMessage(networkMessageWrapper);
    }

    private void sendMessage(Message message) {

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(Constants.hubIp);
        } catch (UnknownHostException e) {
            LOGGER.error("Error connecting to hub!\n" + e.getMessage());
        }
        Socket socket = null;
        try {
            socket = new Socket(ip, Constants.hubPort);
        } catch (IOException e) {
            LOGGER.error("Error connecting to listening port!\n" + e.getMessage());
        }
        try {
            DataOutputStream dos = new DataOutputStream(Objects.requireNonNull(socket).getOutputStream());
            dos.writeInt(message.toByteArray().length);
            dos.write(message.toByteArray());
        } catch (IOException e) {
            LOGGER.error("Error writing message to DataOutputStream!\n" + e.getMessage());
        }
    }

    //    @Override
    public void run() {

        new Thread(sender).start();
        new Thread(receiver).start();
        initializeCommunication();
        while (true) {

            try {
                Message wrapperMessage = networkMessages.take();
                String systemId = wrapperMessage.getSystemId();
                Message receivedMessage = wrapperMessage.getNetworkMessage().getMessage();
                if (receivedMessage.getType().equals(Type.PROC_INITIALIZE_SYSTEM)) {
                    List<ProcessId> processIds = receivedMessage.getProcInitializeSystem().getProcessesList();
                    ProcessId currentProcess = processIds.stream()
                            .filter(x -> x.getHost().equals(Constants.clientHost) && x.getPort() == port)
                            .collect(Collectors.toList()).get(0);
                    AppSystem appSystem = new AppSystem(systemId, processIds, sender, currentProcess);
                    systems.put(systemId, appSystem);
                    new Thread(appSystem).start();
                } else if (receivedMessage.getType().equals(Type.PROC_DESTROY_SYSTEM)) {
                    systems.remove(systemId);
                } else {
                    systems.get(systemId).trigger(wrapperMessage);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
