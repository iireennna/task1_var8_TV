package tvguide.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private final String url;
    private final String user;
    private final String password;

    public Db(Path dataDir) {

        String defaultUrl = "jdbc:sqlite:" + dataDir.resolve("app.db");
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        this.url = (envUrl == null || envUrl.isBlank()) ? defaultUrl : envUrl.trim();
        this.user = envUser == null ? "" : envUser;
        this.password = envPass == null ? "" : envPass;

        try {
            Files.createDirectories(dataDir);
        } catch (Exception ignored) {
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "SQLite JDBC driver не найден.",
                    e
            );
        }
        return DriverManager.getConnection(url);
    }
}

