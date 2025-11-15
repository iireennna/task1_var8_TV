package tvguide.db;

import java.sql.Connection;
import java.sql.Statement;

public class Migrations {
    public static void ensureSchema(Connection c) throws Exception {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS channels(
                  id    INTEGER PRIMARY KEY AUTOINCREMENT,
                  name  TEXT NOT NULL UNIQUE
                );
            """);
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS genres(
                  id    INTEGER PRIMARY KEY AUTOINCREMENT,
                  name  TEXT NOT NULL UNIQUE
                );
            """);
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS programs(
                  id         INTEGER PRIMARY KEY AUTOINCREMENT,
                  title      TEXT NOT NULL,
                  channel_id INTEGER NOT NULL REFERENCES channels(id) ON DELETE RESTRICT,
                  genre_id   INTEGER NOT NULL REFERENCES genres(id)   ON DELETE RESTRICT,
                  day        INTEGER NOT NULL,          -- 1..7 (DayOfWeek)
                  start_time TEXT    NOT NULL           -- "HH:MM"
                );
            """);
        }
    }
}
