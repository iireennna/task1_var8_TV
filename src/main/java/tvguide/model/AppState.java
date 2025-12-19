package tvguide.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AppState implements Serializable {
    private final Map<Long, Channel> channels = new HashMap<>();
    private final Map<Long, Genre> genres = new HashMap<>();
    private final Map<Long, ProgramItem> programs = new HashMap<>();
    private long nextId = 1L;

    public Map<Long, Channel> getChannels() { return channels; }
    public Map<Long, Genre> getGenres() { return genres; }
    public Map<Long, ProgramItem> getPrograms() { return programs; }

    public long getNextId() { return nextId; }
    public void setNextId(long nextId) { this.nextId = nextId; }
}
