package common;

import com.google.protobuf.ByteString;
import communication.Torr2;
import util.Pair;

import java.util.*;

public class Storage {

    private final Map<ByteString, Torr2.FileInfo> fileInfoMap;
    private final Map<ByteString, ByteString> filesContents;
    private final Map<ByteString, Map<ByteString, ByteString>> chunksContents;

    public Storage() {

        this.fileInfoMap = new HashMap<>();
        this.filesContents = new HashMap<>();
        this.chunksContents = new HashMap<>();
    }

    public synchronized void addFileInfo(ByteString hash, Torr2.FileInfo fileInfo) {

        fileInfoMap.put(hash, fileInfo);
    }

    public Torr2.FileInfo getFileInfo(ByteString hash) {

        return fileInfoMap.get(hash);
    }

    public boolean fileInfoMapContainsKey(ByteString bytes) {

        return fileInfoMap.containsKey(bytes);
    }

    public Collection<Torr2.FileInfo> getFileInfoMapValues() {

        return fileInfoMap.values();
    }

    public synchronized void addChunkContent(ByteString fileHash, ByteString hash, ByteString content) {

        if (!chunksContents.containsKey(fileHash))
            chunksContents.put(fileHash, new HashMap<>());

        chunksContents.get(fileHash).put(hash, content);
    }

    public boolean chunksContentsContainsKey(ByteString key) {

        return chunksContents.containsKey(key);
    }

    public ByteString getChunkInfo(ByteString fileHash, ByteString key) {

        return chunksContents.get(fileHash).get(key);
    }

    public Map<ByteString, ByteString> getFileChunksInfo(ByteString fileHash) {

        return chunksContents.get(fileHash);
    }

    public synchronized void addFileContent(ByteString hash, ByteString content) {

        filesContents.put(hash, content);
    }

    public boolean filesContentsContainsKey(ByteString key) {

        return filesContents.containsKey(key);
    }

    public ByteString getFileContent(ByteString key) {

        return filesContents.get(key);
    }
}
