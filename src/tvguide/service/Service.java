package tvguide.service;

import tvguide.api.ChannelApi;
import tvguide.api.GenreApi;
import tvguide.api.ProgramApi;
import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;
import tvguide.repository.SqlRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements ChannelApi, GenreApi, ProgramApi {
    private final SqlRepository repo;

    public Service(SqlRepository repo) {
        this.repo = repo;
    }

    // проверки на уникальность и существование
    private Channel requireChannel(long id) throws Exception {
        Channel c = repo.getChannel(id);
        if (c == null) throw new NoSuchElementException("Канал не найден, id=" + id);
        return c;
    }
    private Genre requireGenre(long id) throws Exception {
        Genre g = repo.getGenre(id);
        if (g == null) throw new NoSuchElementException("Жанр не найден, id=" + id);
        return g;
    }
    private ProgramItem requireProgram(long id) throws Exception {
        ProgramItem p = repo.getProgram(id);
        if (p == null) throw new NoSuchElementException("Передача не найдена, id=" + id);
        return p;
    }

    private void ensureUniqueChannel(String name) throws Exception {
        if (repo.channelExistsByName(name))
            throw new IllegalArgumentException("Канал с таким именем уже существует: " + name);
    }
    private void ensureUniqueGenre(String name) throws Exception {
        if (repo.genreExistsByName(name))
            throw new IllegalArgumentException("Жанр с таким именем уже существует: " + name);
    }


    @Override
    public Channel addChannel(String name) {
        try {
            Objects.requireNonNull(name);
            ensureUniqueChannel(name);
            return repo.insertChannel(name);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteChannel(long id) {
        try {
            if (repo.channelUsed(id))
                throw new IllegalStateException("Канал используется в программах; удалите соответствующие передачи.");
            repo.deleteChannel(id);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel updateChannel(long id, String newName) {
        try {
            requireChannel(id);
            if (newName != null && !newName.isBlank()) {
                ensureUniqueChannel(newName);
                repo.updateChannel(id, newName);
            }
            return repo.getChannel(id);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Channel> listChannels() {
        try {
            return repo.listChannels();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Genre addGenre(String name) {
        try {
            Objects.requireNonNull(name);
            ensureUniqueGenre(name);
            return repo.insertGenre(name);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteGenre(long id) {
        try {
            if (repo.genreUsed(id))
                throw new IllegalStateException("Жанр используется в программах; удалите соответствующие передачи.");
            repo.deleteGenre(id);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Genre updateGenre(long id, String newName) {
        try {
            requireGenre(id);
            if (newName != null && !newName.isBlank()) {
                ensureUniqueGenre(newName);
                repo.updateGenre(id, newName);
            }
            return repo.getGenre(id);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Genre> listGenres() {
        try {
            return repo.listGenres();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ProgramItem addProgram(String title, long channelId, long genreId, DayOfWeek day, LocalTime start) {
        try {
            Objects.requireNonNull(title);
            requireChannel(channelId);
            requireGenre(genreId);
            Objects.requireNonNull(day);
            Objects.requireNonNull(start);
            return repo.insertProgram(title, channelId, genreId, day, start);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProgramItem updateProgram(long id, String title, Long channelId, Long genreId, DayOfWeek day, LocalTime start) {
        try {
            ProgramItem p = requireProgram(id);
            if (title != null && !title.isBlank()) p.setTitle(title.trim());
            if (channelId != null) { requireChannel(channelId); p.setChannelId(channelId); }
            if (genreId != null)   { requireGenre(genreId);   p.setGenreId(genreId); }
            if (day != null)       p.setDay(day);
            if (start != null)     p.setStartTime(start);
            repo.updateProgram(p);
            return repo.getProgram(id);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteProgram(long id) {
        try {
            repo.deleteProgram(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProgramItem> listAll() {
        try {
            return repo.listAllPrograms();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProgramItem> listByDay(DayOfWeek day) {
        try {
            return repo.listProgramsByDay(day);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
