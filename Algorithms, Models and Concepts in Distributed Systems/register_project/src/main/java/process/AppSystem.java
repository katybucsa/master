package process;

import algorithms.App;
import api.Abstraction;
import communication.Sender;
import main.CommunicationProtocol.Message;
import main.CommunicationProtocol.ProcessId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static util.Constants.APP;

public class AppSystem implements Runnable {

    private final String systemId;
    private final BlockingQueue<Message> receivedMessages;
    private final Map<String, Abstraction> abstractions;
    private final List<ProcessId> processes;
    private final ProcessId currentProcess;
    private final Sender sender;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSystem.class);

    public AppSystem(String systemId, List<ProcessId> processes, Sender sender, ProcessId currentProcess) {

        this.systemId = systemId;
        this.receivedMessages = new LinkedBlockingQueue<>();
        this.abstractions = new HashMap<>();
        this.processes = processes;
        this.sender = sender;
        this.currentProcess = currentProcess;
    }

    @Override
    public void run() {

        abstractions.put("app", new App("app", this));
        while (true) {
            try {
                Message message = receivedMessages.take();
                String abstractionId = message.getToAbstractionId();

                LOGGER.info("Extracted message with abstraction id: " + abstractionId + " and type " + message.getType());

                if (!abstractions.containsKey(abstractionId)) {
                    if (abstractionId.contains("nnar"))
                        registerNNAR(abstractionId);
                    if (abstractionId.contains("uc"))
                        registerUc(abstractionId);
                }
                Abstraction abstraction = abstractions.get(abstractionId);
                abstraction.handle(message);
            } catch (InterruptedException e) {
                LOGGER.error("Error extracting message from queue! " + e.getMessage());
            }
        }
    }

    public void registerAbstraction(Abstraction abstraction) {

        if (!abstractions.containsKey(abstraction.abstractionId)) {
            LOGGER.info("Add abstraction with id " + abstraction.abstractionId);
            abstractions.put(abstraction.abstractionId, abstraction);
        }
    }

    private void registerNNAR(String abstractionId) {

        App app = (App) abstractions.get(APP);
        String substr = abstractionId.substring(abstractionId.indexOf('[') + 1);
        String register = substr.substring(0, substr.indexOf(']'));
        app.registerNNAR(register);
    }

    private void registerUc(String abstractionId) {

        App app = (App) abstractions.get(APP);
        String substr = abstractionId.substring(abstractionId.indexOf('[') + 1);
        String topic = substr.substring(0, substr.indexOf(']'));
        String no = "";
        if (abstractionId.contains("ep")) {
            String noSubstr = abstractionId.substring(abstractionId.lastIndexOf('[') + 1);
            no = noSubstr.substring(0, noSubstr.indexOf(']'));
        }
        app.registerUc(topic, no);
    }

    public ProcessId getProcessByHostAndPort(String host, int port) {

        for (ProcessId processId : processes)
            if (processId.getHost().equals(host) && processId.getPort() == port) {
                return processId;
            }
        if (Constants.hubIp.equals(host) && Constants.hubPort == port)
            return Constants.hubProcessId;
        return null;
    }

    public void send(ProcessId destination, Message message) {

        sender.addMessageToQueue(destination, message);
    }

    public void trigger(Message message) {

        receivedMessages.add(message);
    }

    public String getSystemId() {
        return systemId;
    }

    public String getHost() {

        return currentProcess.getHost();
    }

    public int getPort() {

        return currentProcess.getPort();
    }

    public int getRank() {

        return currentProcess.getRank();
    }

    public ProcessId getCurrentProcess() {

        return currentProcess;
    }

    public List<ProcessId> getProcesses() {

        return processes;
    }
}
