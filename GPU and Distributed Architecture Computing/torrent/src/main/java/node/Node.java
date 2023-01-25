package node;

import com.google.protobuf.ByteString;
import common.Storage;
import communication.Receiver;
import communication.Sender;
import communication.Torr2.*;
import communication.Torr2.Message.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requesters.ChunkRequester;
import requesters.LocalSearchRequester;
import util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static communication.Torr2.Message.Type.*;
import static communication.Torr2.Status.*;
import static util.Constants.clientHost;
import static util.Constants.hubId;

public class Node implements Runnable {

    private final int port;
    private final String owner;
    private final int index;
    private final Storage storage;
    private final Sender sender;
    private final NodeId thisNode;
    private static final Logger LOGGER = LoggerFactory.getLogger(Node.class);

    public Node(int port, String owner, int index) {

        this.port = port;
        this.owner = owner;
        this.index = index;
        this.storage = new Storage();
        this.sender = new Sender();
        this.thisNode = NodeId.newBuilder().setOwner(owner).setHost(clientHost).setPort(port).setIndex(index).build();
    }

    private RegistrationRequest buildRegistrationRequest() {

        return RegistrationRequest
                .newBuilder()
                .setOwner(owner)
                .setIndex(index)
                .setPort(port)
                .build();
    }

    private Message buildRegistrationRequestMessage(RegistrationRequest registrationRequest) {

        return Message
                .newBuilder()
                .setType(Type.REGISTRATION_REQUEST)
                .setRegistrationRequest(registrationRequest)
                .build();
    }

    private void initializeCommunication() {

        RegistrationRequest registrationRequest = buildRegistrationRequest();
        Message message = buildRegistrationRequestMessage(registrationRequest);
        sendMessage(message);
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
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeInt(message.toByteArray().length);
            dos.write(message.toByteArray());

            int expectedLength = dis.readInt();
            byte[] bytes = new byte[expectedLength];
            dis.readFully(bytes, 0, expectedLength);
            socket.close();
            Message receivedMessage = Message.parseFrom(bytes);
            if (receivedMessage.getType().equals(Type.REGISTRATION_RESPONSE)) {
                RegistrationResponse registrationResponse = receivedMessage.getRegistrationResponse();
                if (registrationResponse.getStatus() != Status.SUCCESS)
                    LOGGER.error("Node not registered!\n");
                else
                    LOGGER.info("Node registered!\n");
            } else {
                LOGGER.error("Not received registration response!\n");
            }
        } catch (IOException e) {
            LOGGER.error("Error writing message to DataOutputStream!\n" + e.getMessage());
        }
    }

    public void run() {

        new Thread(new Receiver(this, port)).start();
        initializeCommunication();
    }


    public Message handleRequest(Message message) {

        switch (message.getType()) {
            case UPLOAD_REQUEST -> {
                return handleUploadRequest(message.getUploadRequest());
            }
            case REPLICATE_REQUEST -> {
                return handleReplicateRequest(message.getReplicateRequest());
            }
            case CHUNK_REQUEST -> {
                return handleChunkRequest(message.getChunkRequest());
            }
            case LOCAL_SEARCH_REQUEST -> {
                return handleLocalSearchRequest(message.getLocalSearchRequest().getRegex());
            }
            case SEARCH_REQUEST -> {
                return handleSearchRequest(message.getSearchRequest());
            }
            case DOWNLOAD_REQUEST -> {
                return handleDownloadRequest(message.getDownloadRequest());
            }
        }
        return null;
    }

    private Message handleUploadRequest(UploadRequest uploadRequest) {

        UploadResponse uploadResponse;
        try {
            if (!uploadRequest.getFilename().isEmpty()) {
                FileInfo fileInfo;
                MessageDigest md = null;
                byte[] digest;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md.update(uploadRequest.getData().toByteArray());
                byte[] fileDigest = md.digest();
                if (!storage.fileInfoMapContainsKey(ByteString.copyFrom(fileDigest))) {

                    md.reset();
                    byte[] bytes = uploadRequest.getData().toByteArray();
                    List<ChunkInfo> chunkInfos = new ArrayList<>();

                    for (int i = 0; i < bytes.length; i += 1024) {
                        int length = Math.min(bytes.length - i, 1024);
                        byte[] chunk = new byte[length];
                        System.arraycopy(bytes, i, chunk, 0, length);
                        md.update(chunk);
                        digest = md.digest();
                        ChunkInfo chunkInfo = ChunkInfo.newBuilder()
                                .setHash(ByteString.copyFrom(digest))
                                .setIndex((int) Math.ceil((double) i / 1024))
                                .setSize(length)
                                .build();
                        chunkInfos.add(chunkInfo);
                        storage.addChunkContent(ByteString.copyFrom(fileDigest), ByteString.copyFrom(digest), ByteString.copyFrom(chunk));
                        md.reset();
                    }

                    md.update(bytes);
                    digest = md.digest();
                    fileInfo = FileInfo.newBuilder()
                            .setHash(ByteString.copyFrom(digest))
                            .setSize(bytes.length)
                            .setFilename(uploadRequest.getFilename())
                            .addAllChunks(chunkInfos)
                            .build();
                    storage.addFileInfo(ByteString.copyFrom(digest), fileInfo);
                    storage.addFileContent(ByteString.copyFrom(digest), uploadRequest.getData());
                } else {
                    fileInfo = storage.getFileInfo(ByteString.copyFrom(fileDigest));
                }

                uploadResponse = UploadResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setFileInfo(fileInfo)
                        .build();
            } else {
                uploadResponse = UploadResponse.newBuilder()
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("Filename is empty!")
                        .build();
            }
        } catch (Exception e) {
            uploadResponse = UploadResponse.newBuilder()
                    .setStatus(PROCESSING_ERROR)
                    .setErrorMessage("Error while processing the request!")
                    .build();
        }

        Message message = Message.newBuilder()
                .setType(UPLOAD_RESPONSE)
                .setUploadResponse(uploadResponse)
                .build();

        LOGGER.info("Sending upload response to hub {}", message);
        return message;
    }

    private Message handleReplicateRequest(ReplicateRequest replicateRequest) {

        ReplicateResponse replicateResponse;
        if (replicateRequest.getFileInfo().getFilename().isEmpty())
            replicateResponse = ReplicateResponse.newBuilder()
                    .setStatus(MESSAGE_ERROR)
                    .setErrorMessage("Filename is empty!")
                    .build();
        else {
            List<NodeReplicationStatus> nodeReplicationStatuses = new ArrayList<>();
            if (storage.fileInfoMapContainsKey(replicateRequest.getFileInfo().getHash())) {
                FileInfo fileInfo = storage.getFileInfo(replicateRequest.getFileInfo().getHash());
                for (int i = 0; i < fileInfo.getChunksCount(); i++) {
                    NodeReplicationStatus nodeReplicationStatus = NodeReplicationStatus.newBuilder()
                            .setNode(thisNode)
                            .setChunkIndex(i)
                            .setStatus(SUCCESS)
                            .setErrorMessage("")
                            .build();
                    nodeReplicationStatuses.add(nodeReplicationStatus);
                }
            } else {
                SubnetResponse subnetResponse = sendSubnetRequest(replicateRequest.getSubnetId());

                List<NodeId> nodeIds = subnetResponse.getNodesList()
                        .stream()
                        .filter(ni -> !(ni.getHost().equals(clientHost) && ni.getPort() == port && ni.getIndex() == index))
                        .collect(Collectors.toList());

                nodeReplicationStatuses = sendChunkRequest(nodeIds, replicateRequest.getFileInfo());
                storage.addFileInfo(replicateRequest.getFileInfo().getHash(), replicateRequest.getFileInfo());
            }
            replicateResponse = ReplicateResponse.newBuilder()
                    .setStatus(SUCCESS)
                    .setErrorMessage("")
                    .addAllNodeStatusList(nodeReplicationStatuses)
                    .build();
        }
        Message message = Message.newBuilder()
                .setType(REPLICATE_RESPONSE)
                .setReplicateResponse(replicateResponse)
                .build();

        LOGGER.info("Send replicate response to hub: {}", message);
        return message;
    }

    private Message handleChunkRequest(ChunkRequest chunkRequest) {

        ChunkResponse chunkResponse;
        try {
            if (chunkRequest.getFileHash().size() != 16 || chunkRequest.getChunkIndex() < 0) {

                chunkResponse = ChunkResponse.newBuilder()
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("File hash length is less than 16 or the chunk index is less than 0!")
                        .build();
            } else if (!storage.fileInfoMapContainsKey(chunkRequest.getFileHash())) {

                chunkResponse = ChunkResponse.newBuilder()
                        .setStatus(UNABLE_TO_COMPLETE)
                        .setErrorMessage("I do not have this chunk!")
                        .build();
            } else {
                chunkResponse = ChunkResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setErrorMessage("")
                        .setData(storage.getChunkInfo(chunkRequest.getFileHash(), storage.getFileInfo(chunkRequest.getFileHash()).getChunksList().get(chunkRequest.getChunkIndex()).getHash()))
                        .build();
            }
        } catch (Exception e) {
            chunkResponse = ChunkResponse.newBuilder()
                    .setStatus(PROCESSING_ERROR)
                    .setErrorMessage("Error while processing the request!")
                    .build();
        }
        Message message = Message.newBuilder()
                .setType(CHUNK_RESPONSE)
                .setChunkResponse(chunkResponse)
                .build();

        LOGGER.info("Sending chunk response: {}", chunkResponse);
        return message;
    }

    private SubnetResponse sendSubnetRequest(int subnetId) {

        SubnetRequest subnetRequest = SubnetRequest.newBuilder()
                .setSubnetId(subnetId)
                .build();
        Message message = Message.newBuilder()
                .setType(SUBNET_REQUEST)
                .setSubnetRequest(subnetRequest)
                .build();
        return sender.sendMessage(hubId, message).getSubnetResponse();
    }

    private List<NodeReplicationStatus> sendChunkRequest(List<NodeId> nodeIds, FileInfo fileInfo) {

        List<NodeReplicationStatus> nodeReplicationStatuses = new ArrayList<>();
        List<Future<List<NodeReplicationStatus>>> nodeReplicationStatusesFutures = new ArrayList<>();
        MessageDigest md = null;
        byte[] digest;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ExecutorService executor = Executors.newFixedThreadPool(fileInfo.getChunksCount());
        for (int i = 0; i < fileInfo.getChunksCount(); i++) {

            ChunkRequester chunkRequester = new ChunkRequester(i, nodeIds, fileInfo.getHash(), thisNode, storage, sender);
            Future<List<NodeReplicationStatus>> nodeReplicationStatusFuture = executor.submit(chunkRequester);
            nodeReplicationStatusesFutures.add(nodeReplicationStatusFuture);
        }
        for (int i = 0; i < fileInfo.getChunksCount(); i++) {

            while (!nodeReplicationStatusesFutures.get(i).isDone()) ;
            List<NodeReplicationStatus> nodeReplicationStatusList = null;
            try {
                nodeReplicationStatusList = nodeReplicationStatusesFutures.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            nodeReplicationStatuses.addAll(Objects.requireNonNull(nodeReplicationStatusList));
        }

        Map<ByteString, Integer> chunksWithIndex = fileInfo.getChunksList().stream()
                .collect(Collectors.toMap(ChunkInfo::getHash, ChunkInfo::getIndex));
        ByteString fileContent = storage.getFileChunksInfo(fileInfo.getHash()).entrySet().stream()
                .sorted(Comparator.comparingInt(x -> chunksWithIndex.get(x.getKey())))
                .map(Map.Entry::getValue)
                .reduce(ByteString.copyFromUtf8(""), ByteString::concat);
        md.update(fileContent.toByteArray());
        digest = md.digest();
        storage.addFileContent(fileInfo.getHash(), fileContent);
        return nodeReplicationStatuses;
    }

    private Message handleLocalSearchRequest(String regex) {

        LocalSearchResponse localSearchResponse;
        try {
            try {
                Pattern.compile(regex);

                List<FileInfo> fileInfos = storage.getFileInfoMapValues()
                        .stream()
                        .filter(fi -> fi.getFilename().matches(regex))
                        .collect(Collectors.toList());

                localSearchResponse = LocalSearchResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setErrorMessage("")
                        .addAllFileInfo(fileInfos)
                        .build();
            } catch (PatternSyntaxException exception) {
                localSearchResponse = LocalSearchResponse.newBuilder()
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("Invalid regex!")
                        .build();
            }
        } catch (Exception e) {
            localSearchResponse = LocalSearchResponse.newBuilder()
                    .setStatus(PROCESSING_ERROR)
                    .setErrorMessage("Error while processing request!")
                    .build();
        }

        Message message = Message.newBuilder()
                .setType(LOCAL_SEARCH_RESPONSE)
                .setLocalSearchResponse(localSearchResponse)
                .build();

        LOGGER.info("Send Local Search Response: {}", localSearchResponse);
        return message;
    }

    private Message handleSearchRequest(SearchRequest searchRequest) {

        SearchResponse searchResponse;
        try {
            try {
                Pattern.compile(searchRequest.getRegex());
                List<NodeSearchResult> nodeSearchResults = sendLocalSearchRequest(searchRequest);

                List<FileInfo> fileInfos = storage.getFileInfoMapValues()
                        .stream()
                        .filter(fi -> fi.getFilename().matches(searchRequest.getRegex()))
                        .collect(Collectors.toList());

                LocalSearchResponse localSearchResponse = LocalSearchResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setErrorMessage("")
                        .addAllFileInfo(fileInfos)
                        .build();
                NodeSearchResult nodeSearchResult = NodeSearchResult.newBuilder()
                        .setNode(thisNode)
                        .setStatus(localSearchResponse.getStatus())
                        .setErrorMessage(localSearchResponse.getErrorMessage())
                        .addAllFiles(localSearchResponse.getFileInfoList())
                        .build();

                nodeSearchResults.add(nodeSearchResult);

                searchResponse = SearchResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setErrorMessage("")
                        .addAllResults(nodeSearchResults)
                        .build();

            } catch (PatternSyntaxException exception) {
                searchResponse = SearchResponse.newBuilder()
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("Invalid regex!")
                        .build();
            }
        } catch (Exception e) {
            searchResponse = SearchResponse.newBuilder()
                    .setStatus(PROCESSING_ERROR)
                    .setErrorMessage("Error while processing request!")
                    .build();
        }

        Message message = Message.newBuilder()
                .setType(SEARCH_RESPONSE)
                .setSearchResponse(searchResponse)
                .build();

        LOGGER.info("Send Search Response: {}", searchResponse);
        return message;
    }

    private List<NodeSearchResult> sendLocalSearchRequest(SearchRequest searchRequest) {

        SubnetResponse subnetResponse = sendSubnetRequest(searchRequest.getSubnetId());
        ExecutorService executor = Executors.newFixedThreadPool(subnetResponse.getNodesCount() - 1);
        List<Future<NodeSearchResult>> nodeSearchResultFutures = new ArrayList<>();

        List<NodeSearchResult> nodeSearchResults = new ArrayList<>();
        for (int i = 0; i < subnetResponse.getNodesCount(); i++) {
            if (subnetResponse.getNodes(i).getOwner().equals(owner) && subnetResponse.getNodes(i).getIndex() == index
                    && subnetResponse.getNodes(i).getPort() == port && subnetResponse.getNodes(i).getHost().equals(clientHost))
                continue;

            LocalSearchRequester localSearchRequestSender = new LocalSearchRequester(searchRequest.getRegex(), subnetResponse.getNodes(i), sender);
            Future<NodeSearchResult> nodeSearchResultFuture = executor.submit(localSearchRequestSender);
            nodeSearchResultFutures.add(nodeSearchResultFuture);
        }
        for (Future<NodeSearchResult> nrs : nodeSearchResultFutures) {

            while (!nrs.isDone()) ;

            NodeSearchResult nodeSearchResult = null;
            try {
                nodeSearchResult = nrs.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            nodeSearchResults.add(nodeSearchResult);
        }
        return nodeSearchResults;
    }

    private Message handleDownloadRequest(DownloadRequest downloadRequest) {

        DownloadResponse downloadResponse;
        try {
            if (downloadRequest.getFileHash().size() != 16) {
                downloadResponse = DownloadResponse.newBuilder()
                        .setStatus(MESSAGE_ERROR)
                        .setErrorMessage("File hash is not 16 bytes long!")
                        .build();
            } else if (storage.fileInfoMapContainsKey(downloadRequest.getFileHash()) && storage.filesContentsContainsKey(downloadRequest.getFileHash())) {

                downloadResponse = DownloadResponse.newBuilder()
                        .setStatus(SUCCESS)
                        .setErrorMessage("")
                        .setData(storage.getFileContent(downloadRequest.getFileHash()))
                        .build();

            } else {
                downloadResponse = DownloadResponse.newBuilder()
                        .setStatus(UNABLE_TO_COMPLETE)
                        .setErrorMessage("I do not have this file")
                        .build();
            }
        } catch (Exception e) {
            downloadResponse = DownloadResponse.newBuilder()
                    .setStatus(PROCESSING_ERROR)
                    .setErrorMessage("Error while processing the request!")
                    .build();
        }

        Message message = Message.newBuilder()
                .setType(DOWNLOAD_RESPONSE)
                .setDownloadResponse(downloadResponse)
                .build();

        LOGGER.info("Send Download Response: {}", downloadResponse);
        return message;
    }
}
