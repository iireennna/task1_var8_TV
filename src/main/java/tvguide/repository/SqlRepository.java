package tvguide.repository;

import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;
import tvguide.db.Db;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRepository {
    private final Db db;

    public SqlRepository(Db db) {
        this.db = db;
    }

   
    public Channel insertChannel(String name) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO channels(name) VALUES(?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                long id = rs.next() ? rs.getLong(1) : 0L;
                return new Channel(id, name.trim());
            }
        }
    }

    public void updateChannel(long id, String newName) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE channels SET name=? WHERE id=?")) {
            ps.setString(1, newName.trim());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteChannel(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM channels WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Channel> listChannels() throws Exception {
        List<Channel> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM channels ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Channel(rs.getLong(1), rs.getString(2)));
        }
        return out;
    }

    public boolean channelExistsByName(String name) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM channels WHERE LOWER(TRIM(name))=LOWER(TRIM(?))")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean channelUsed(long channelId) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM programs WHERE channel_id=? LIMIT 1")) {
            ps.setLong(1, channelId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public Channel getChannel(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM channels WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Channel(rs.getLong(1), rs.getString(2));
                return null;
            }
        }
    }


    public Genre insertGenre(String name) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO genres(name) VALUES(?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                long id = rs.next() ? rs.getLong(1) : 0L;
                return new Genre(id, name.trim());
            }
        }
    }

    public void updateGenre(long id, String newName) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE genres SET name=? WHERE id=?")) {
            ps.setString(1, newName.trim());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteGenre(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM genres WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Genre> listGenres() throws Exception {
        List<Genre> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM genres ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Genre(rs.getLong(1), rs.getString(2)));
        }
        return out;
    }

    public boolean genreExistsByName(String name) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM genres WHERE LOWER(TRIM(name))=LOWER(TRIM(?))")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean genreUsed(long genreId) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM programs WHERE genre_id=? LIMIT 1")) {
            ps.setLong(1, genreId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public Genre getGenre(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,name FROM genres WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Genre(rs.getLong(1), rs.getString(2));
                return null;
            }
        }
    }


    public ProgramItem insertProgram(String title, long channelId, long genreId, DayOfWeek day, LocalTime start) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO programs(title,channel_id,genre_id,day,start_time) VALUES(?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title.trim());
            ps.setLong(2, channelId);
            ps.setLong(3, genreId);
            ps.setInt(4, day.getValue());
            ps.setString(5, start.toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                long id = rs.next() ? rs.getLong(1) : 0L;
                return new ProgramItem(id, title.trim(), channelId, genreId, day, start);
            }
        }
    }

    public void updateProgram(ProgramItem p) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE programs SET title=?, channel_id=?, genre_id=?, day=?, start_time=? WHERE id=?")) {
            ps.setString(1, p.getTitle());
            ps.setLong(2, p.getChannelId());
            ps.setLong(3, p.getGenreId());
            ps.setInt(4, p.getDay().getValue());
            ps.setString(5, p.getStartTime().toString());
            ps.setLong(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void deleteProgram(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM programs WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public ProgramItem getProgram(long id) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,title,channel_id,genre_id,day,start_time FROM programs WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProgram(rs);
                }
                return null;
            }
        }
    }

    public List<ProgramItem> listAllPrograms() throws Exception {
        List<ProgramItem> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,title,channel_id,genre_id,day,start_time FROM programs ORDER BY day, start_time");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapProgram(rs));
        }
        return out;
    }

    public List<ProgramItem> listProgramsByDay(DayOfWeek day) throws Exception {
        List<ProgramItem> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id,title,channel_id,genre_id,day,start_time FROM programs WHERE day=? ORDER BY start_time")) {
            ps.setInt(1, day.getValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapProgram(rs));
            }
        }
        return out;
    }

    private static ProgramItem mapProgram(ResultSet rs) throws Exception {
        long id = rs.getLong("id");
        String title = rs.getString("title");
        long ch = rs.getLong("channel_id");
        long gn = rs.getLong("genre_id");
        DayOfWeek day = DayOfWeek.of(rs.getInt("day"));
        LocalTime time = LocalTime.parse(rs.getString("start_time"));
        return new ProgramItem(id, title, ch, gn, day, time);
    }
}
