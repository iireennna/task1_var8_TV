package tvguide.service;

import tvguide.api.ChannelApi;
import tvguide.api.GenreApi;
import tvguide.api.ProgramApi;
import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;
import tvguide.repository.Storage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements ChannelApi, GenreApi, ProgramApi {
    private final Storage storage;

    public Service(Storage storage) {
        this.storage = storage;
    }

    private long nextId() { return storage.consumeId(); }
    private void persist() { storage.save(); }

    private Map<Long, Channel> channelMap() { return storage.channels(); }
    private Map<Long, Genre> genreMap()     { return storage.genres(); }
    private Map<Long, ProgramItem> programMap() { return storage.programs(); }

    private Channel requireChannel(long id) {
        Channel c = channelMap().get(id);
        if (c == null) throw new NoSuchElementException("Канал не найден, id=" + id);
        return c;
    }
    private Genre requireGenre(long id) {
        Genre g = genreMap().get(id);
        if (g == null) throw new NoSuchElementException("Жанр не найден, id=" + id);
        return g;
    }
    private ProgramItem requireProgram(long id) {
        ProgramItem p = programMap().get(id);
        if (p == null) throw new NoSuchElementException("Передача не найдена, id=" + id);
        return p;
    }
    private void ensureUniqueChannel(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT);
        boolean exists = channelMap().values().stream()
                .anyMatch(c -> c.getName().trim().toLowerCase(Locale.ROOT).equals(n));
        if (exists) throw new IllegalArgumentException("Канал с таким именем уже существует: " + name);
    }
    private void ensureUniqueGenre(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT);
        boolean exists = genreMap().values().stream()
                .anyMatch(g -> g.getName().trim().toLowerCase(Locale.ROOT).equals(n));
        if (exists) throw new IllegalArgumentException("Жанр с таким именем уже существует: " + name);
    }


    @Override
    public Channel addChannel(String name) {
        Objects.requireNonNull(name);
        ensureUniqueChannel(name);
        Channel c = new Channel(nextId(), name.trim());
        channelMap().put(c.getId(), c);
        persist();
        return c;
    }

    @Override
    public void deleteChannel(long id) {
        boolean used = programMap().values().stream().anyMatch(p -> p.getChannelId() == id);
        if (used) throw new IllegalStateException("Канал используется в программах; удалите соответствующие передачи.");
        channelMap().remove(id);
        persist();
    }

    @Override
    public Channel updateChannel(long id, String newName) {
        Channel c = requireChannel(id);
        if (newName != null && !newName.isBlank()) {
            ensureUniqueChannel(newName);
            c.setName(newName.trim());
        }
        persist();
        return c;
    }

    @Override
    public List<Channel> listChannels() {
        return channelMap().values().stream()
                .sorted(Comparator.comparing(Channel::getName))
                .collect(Collectors.toList());
    }


    @Override
    public Genre addGenre(String name) {
        Objects.requireNonNull(name);
        ensureUniqueGenre(name);
        Genre g = new Genre(nextId(), name.trim());
        genreMap().put(g.getId(), g);
        persist();
        return g;
    }

    @Override
    public void deleteGenre(long id) {
        boolean used = programMap().values().stream().anyMatch(p -> p.getGenreId() == id);
        if (used) throw new IllegalStateException("Жанр используется в программах; удалите соответствующие передачи.");
        genreMap().remove(id);
        persist();
    }

    @Override
    public Genre updateGenre(long id, String newName) {
        Genre g = requireGenre(id);
        if (newName != null && !newName.isBlank()) {
            ensureUniqueGenre(newName);
            g.setName(newName.trim());
        }
        persist();
        return g;
    }

    @Override
    public List<Genre> listGenres() {
        return genreMap().values().stream()
                .sorted(Comparator.comparing(Genre::getName))
                .collect(Collectors.toList());
    }


    @Override
    public ProgramItem addProgram(String title, long channelId, long genreId, DayOfWeek day, LocalTime start) {
        Objects.requireNonNull(title);
        requireChannel(channelId);
        requireGenre(genreId);
        Objects.requireNonNull(day);
        Objects.requireNonNull(start);
        ProgramItem p = new ProgramItem(nextId(), title.trim(), channelId, genreId, day, start);
        programMap().put(p.getId(), p);
        persist();
        return p;
    }

    @Override
    public ProgramItem updateProgram(long id, String title, Long channelId, Long genreId, DayOfWeek day, LocalTime start) {
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
    public void deleteProgram(long id) {
        programMap().remove(id);
        persist();
    }

    @Override
    public List<ProgramItem> listAll() {
        return programMap().values().stream()
                .sorted(Comparator.comparing(ProgramItem::getDay).thenComparing(ProgramItem::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProgramItem> listByDay(DayOfWeek day) {
        return programMap().values().stream()
                .filter(p -> p.getDay() == day)
                .sorted(Comparator.comparing(ProgramItem::getStartTime))
                .collect(Collectors.toList());
    }
}
