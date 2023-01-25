package requesters;

import communication.Sender;
import communication.Torr2.*;

import java.util.concurrent.Callable;

import static communication.Torr2.Message.Type.LOCAL_SEARCH_REQUEST;
import static communication.Torr2.Message.Type.LOCAL_SEARCH_RESPONSE;
import static communication.Torr2.Status.NETWORK_ERROR;

public class LocalSearchRequester implements Callable<NodeSearchResult> {

    private final String regex;
    private final NodeId nodeId;
    private final Sender sender;

    public LocalSearchRequester(String regex, NodeId nodeId, Sender sender) {

        this.regex = regex;
        this.nodeId = nodeId;
        this.sender = sender;
    }

    @Override
    public NodeSearchResult call() {

        LocalSearchRequest localSearchRequest = LocalSearchRequest.newBuilder()
                .setRegex(regex)
                .build();

        Message message = Message.newBuilder()
                .setType(LOCAL_SEARCH_REQUEST)
                .setLocalSearchRequest(localSearchRequest)
                .build();

        Message receivedMessage = sender.sendMessage(nodeId, message);
        NodeSearchResult nodeSearchResult;
        if (receivedMessage == null)
            nodeSearchResult = NodeSearchResult.newBuilder()
                    .setNode(nodeId)
                    .setStatus(NETWORK_ERROR)
                    .setErrorMessage("Cannot connect to node!")
                    .build();
        else if (!receivedMessage.getType().equals(LOCAL_SEARCH_RESPONSE))
            nodeSearchResult = NodeSearchResult.newBuilder()
                    .setNode(nodeId)
                    .setStatus(Status.PROCESSING_ERROR)
                    .setErrorMessage("Error while processing request!")
                    .build();
        else {
            LocalSearchResponse localSearchResponse = receivedMessage.getLocalSearchResponse();

            nodeSearchResult = NodeSearchResult.newBuilder()
                    .setNode(nodeId)
                    .setStatus(localSearchResponse.getStatus())
                    .setErrorMessage(localSearchResponse.getErrorMessage())
                    .addAllFiles(localSearchResponse.getFileInfoList())
                    .build();
        }
        return nodeSearchResult;
    }
}
