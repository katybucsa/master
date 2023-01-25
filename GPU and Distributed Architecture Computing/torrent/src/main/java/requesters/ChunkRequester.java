package requesters;

import com.google.protobuf.ByteString;
import common.Storage;
import communication.Sender;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static communication.Torr2.*;
import static communication.Torr2.Message.Type.CHUNK_REQUEST;
import static communication.Torr2.Message.Type.CHUNK_RESPONSE;
import static communication.Torr2.Status.*;
import static util.Constants.clientHost;

public class ChunkRequester implements Callable<List<NodeReplicationStatus>> {

    private final int id;
    private final List<NodeId> nodeIds;
    private final ByteString fileHash;
    private final NodeId thisNode;
    private final Storage storage;
    private final Sender sender;

    public ChunkRequester(int id, List<NodeId> nodeIds, ByteString fileHash, NodeId thisNode, Storage storage, Sender sender) {

        this.id = id;
        this.nodeIds = nodeIds;
        this.fileHash = fileHash;
        this.thisNode = thisNode;
        this.storage = storage;
        this.sender = sender;
    }

    @Override
    public List<NodeReplicationStatus> call() {

        List<NodeReplicationStatus> nodeReplicationStatuses = new ArrayList<>();
        boolean obtained = false;
        int start = id % nodeIds.size();
        MessageDigest md = null;
        int size = nodeIds.size();
        byte[] digest;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        for (int j = start; j < size; j++) {
            if (nodeIds.get(j).getHost().equals(clientHost) && nodeIds.get(j).getPort() == thisNode.getPort() && nodeIds.get(j).getIndex() == thisNode.getIndex()) {
                if (j == nodeIds.size() - 1 && !obtained) {
                    j = 0;
                    size = start;
                }
                continue;
            }
            ChunkRequest chunkRequest = ChunkRequest.newBuilder()
                    .setFileHash(fileHash)
                    .setChunkIndex(id)
                    .build();
            Message message = Message.newBuilder()
                    .setType(CHUNK_REQUEST)
                    .setChunkRequest(chunkRequest)
                    .build();
            Message receivedMessage = sender.sendMessage(nodeIds.get(j), message);
            NodeReplicationStatus nodeReplicationStatus;

            if (receivedMessage == null)
                nodeReplicationStatus = NodeReplicationStatus.newBuilder()
                        .setNode(nodeIds.get(j))
                        .setChunkIndex(id)
                        .setStatus(NETWORK_ERROR)
                        .setErrorMessage("Error connection to node {}" + nodeIds.get(j).getOwner() + "-" + nodeIds.get(j).getIndex())
                        .build();
            else if (!receivedMessage.getType().equals(CHUNK_RESPONSE))
                nodeReplicationStatus = NodeReplicationStatus.newBuilder()
                        .setNode(nodeIds.get(j))
                        .setChunkIndex(id)
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("The type of the received message is not CHUNK_RESPONSE!")
                        .build();
            else {
                ChunkResponse chunkResponse = receivedMessage.getChunkResponse();
                nodeReplicationStatus = NodeReplicationStatus.newBuilder()
                        .setNode(nodeIds.get(j))
                        .setChunkIndex(id)
                        .setStatus(chunkResponse.getStatus())
                        .setErrorMessage(chunkResponse.getErrorMessage())
                        .build();

                if (chunkResponse.getStatus().equals(SUCCESS)) {
                    md.update(chunkResponse.getData().toByteArray());
                    digest = md.digest();
                    storage.addChunkContent(fileHash, ByteString.copyFrom(digest), chunkResponse.getData());
                    md.reset();
                    j = size + 1;
                    obtained = true;
                }
                if (j == nodeIds.size() - 1 && !obtained) {
                    j = 0;
                    size = start;
                }
            }
            nodeReplicationStatuses.add(nodeReplicationStatus);
        }
        return nodeReplicationStatuses;
    }
}
