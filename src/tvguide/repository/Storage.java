package tvguide.repository;

import tvguide.model.AppState;
import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Storage {
    private final Path dataDir;
    private final Path statePath;
    private final Path tmpPath;


    private AppState state;

    public Storage(Path dataDir) {
        this.dataDir = dataDir;
        this.statePath = dataDir.resolve("state.bin");
        this.tmpPath = dataDir.resolve("state.tmp");
        this.state = loadOrCreateFromDisk();
    }


    public Map<Long, Channel> channels() { return state.getChannels(); }
    public Map<Long, Genre> genres()     { return state.getGenres(); }
    public Map<Long, ProgramItem> programs() { return state.getPrograms(); }


    public long consumeId() {
        long id = state.getNextId();
        state.setNextId(id + 1);
        return id;
    }


    public void save() {
        try {
            Files.createDirectories(dataDir);
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tmpPath)))) {
                oos.writeObject(state);
                oos.flush();
                try (FileChannel ch = FileChannel.open(tmpPath)) {
                    ch.force(true);
                }
            }
            try {
                Files.move(tmpPath, statePath, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception ex) {
                Files.move(tmpPath, statePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.err.println("Failed to save state: " + e);
        }
    }


    private AppState loadOrCreateFromDisk() {
        try {
            Files.createDirectories(dataDir);
            if (Files.exists(statePath)) {
                try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(statePath)))) {
                    Object o = ois.readObject();
                    return (AppState) o;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load state: " + e);
        }
        return new AppState();
    }
}
