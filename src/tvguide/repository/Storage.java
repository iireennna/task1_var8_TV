package tvguide.repository;

import tvguide.model.AppState;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Storage {
    private final Path dataDir;
    private final Path statePath;
    private final Path tmpPath;

    public Storage(Path dataDir) {
        this.dataDir = dataDir;
        this.statePath = dataDir.resolve("state.bin");
        this.tmpPath = dataDir.resolve("state.tmp");
    }

    public synchronized AppState loadOrCreate() {
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

    public synchronized void save(AppState state) {
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
}
