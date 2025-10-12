package tvguide.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AppState implements Serializable {
    public Map<Long, Channel> channels = new HashMap<>();
    public Map<Long, Genre> genres = new HashMap<>();
    public Map<Long, ProgramItem> programs = new HashMap<>();
    public long nextId = 1L;
}
