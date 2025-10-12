package tvguide.service;

import tvguide.api.ChannelApi;
import tvguide.api.GenreApi;
import tvguide.api.ProgramApi;
import tvguide.model.*;
import tvguide.repository.Storage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements ChannelApi, GenreApi, ProgramApi {
    private final Storage storage;
    private AppState state;

    public Service(Storage storage) {
        this.storage = storage;
        this.state = storage.loadOrCreate();
    }

    private long nextId() { return state.nextId++; }

    private void persist() { storage.save(state); }

    // ChannelApi
    @Override
    public synchronized Channel addChannel(String name) {
        Objects.requireNonNull(name);
        ensureUniqueChannel(name);
        Channel c = new Channel(nextId(), name.trim());
        state.channels.put(c.getId(), c);
        persist();
        return c;
    }

    @Override
    public synchronized void deleteChannel(long id) {
        // prevent deletion if used by a program
        boolean used = state.programs.values().stream().anyMatch(p -> p.getChannelId() == id);
        if (used) throw new IllegalStateException("Канал используется в программах, удалите соответствующие передачи.");
        state.channels.remove(id);
        persist();
    }

    @Override
    public synchronized Channel updateChannel(long id, String newName) {
        Channel c = requireChannel(id);
        if (newName != null && !newName.isBlank()) {
            ensureUniqueChannel(newName);
            c.setName(newName.trim());
        }
        persist();
        return c;
    }

    @Override
    public synchronized List<Channel> listChannels() {
        return state.channels.values().stream()
                .sorted(Comparator.comparing(Channel::getName))
                .collect(Collectors.toList());
    }

    private void ensureUniqueChannel(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT);
        boolean exists = state.channels.values().stream()
                .anyMatch(c -> c.getName().trim().toLowerCase(Locale.ROOT).equals(n));
        if (exists) throw new IllegalArgumentException("Канал с таким именем уже существует: " + name);
    }
    private Channel requireChannel(long id) {
        Channel c = state.channels.get(id);
        if (c == null) throw new NoSuchElementException("Канал не найден, id=" + id);
        return c;
    }

    // GenreApi
    @Override
    public synchronized Genre addGenre(String name) {
        Objects.requireNonNull(name);
        ensureUniqueGenre(name);
        Genre g = new Genre(nextId(), name.trim());
        state.genres.put(g.getId(), g);
        persist();
        return g;
    }

    @Override
    public synchronized void deleteGenre(long id) {
        boolean used = state.programs.values().stream().anyMatch(p -> p.getGenreId() == id);
        if (used) throw new IllegalStateException("Жанр используется в программах, удалите соответствующие передачи.");
        state.genres.remove(id);
        persist();
    }

    @Override
    public synchronized Genre updateGenre(long id, String newName) {
        Genre g = requireGenre(id);
        if (newName != null && !newName.isBlank()) {
            ensureUniqueGenre(newName);
            g.setName(newName.trim());
        }
        persist();
        return g;
    }

    @Override
    public synchronized List<Genre> listGenres() {
        return state.genres.values().stream()
                .sorted(Comparator.comparing(Genre::getName))
                .collect(Collectors.toList());
    }

    private void ensureUniqueGenre(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT);
        boolean exists = state.genres.values().stream()
                .anyMatch(g -> g.getName().trim().toLowerCase(Locale.ROOT).equals(n));
        if (exists) throw new IllegalArgumentException("Жанр с таким именем уже существует: " + name);
    }
    private Genre requireGenre(long id) {
        Genre g = state.genres.get(id);
        if (g == null) throw new NoSuchElementException("Жанр не найден, id=" + id);
        return g;
    }


    @Override
    public synchronized ProgramItem addProgram(String title, long channelId, long genreId, DayOfWeek day, LocalTime start) {
        Objects.requireNonNull(title);
        requireChannel(channelId);
        requireGenre(genreId);
        Objects.requireNonNull(day);
        Objects.requireNonNull(start);
        ProgramItem p = new ProgramItem(nextId(), title.trim(), channelId, genreId, day, start);
        state.programs.put(p.getId(), p);
        persist();
        return p;
    }

    @Override
    public synchronized ProgramItem updateProgram(long id, String title, Long channelId, Long genreId, DayOfWeek day, LocalTime start) {
        ProgramItem p = requireProgram(id);
        if (title != null && !title.isBlank()) p.setTitle(title.trim());
        if (channelId != null) { requireChannel(channelId); p.setChannelId(channelId); }
        if (genreId != null)   { requireGenre(genreId);   p.setGenreId(genreId); }
        if (day != null)       p.setDay(day);
        if (start != null)     p.setStartTime(start);
        persist();
        return p;
    }

    @Override
    public synchronized void deleteProgram(long id) {
        state.programs.remove(id);
        persist();
    }

    @Override
    public synchronized List<ProgramItem> listAll() {
        return state.programs.values().stream()
                .sorted(Comparator.comparing(ProgramItem::getDay).thenComparing(ProgramItem::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<ProgramItem> listByDay(DayOfWeek day) {
        return state.programs.values().stream()
                .filter(p -> p.getDay() == day)
                .sorted(Comparator.comparing(ProgramItem::getStartTime))
                .collect(Collectors.toList());
    }

    private ProgramItem requireProgram(long id) {
        ProgramItem p = state.programs.get(id);
        if (p == null) throw new NoSuchElementException("Передача не найдена, id=" + id);
        return p;
    }
}
