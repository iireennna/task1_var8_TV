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
        // дефолт на SQLite
        String defaultUrl = "jdbc:sqlite:" + dataDir.resolve("app.db");
        String envUrl  = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        this.url = (envUrl == null || envUrl.isBlank()) ? defaultUrl : envUrl.trim();
        this.user = envUser == null ? "" : envUser;
        this.password = envPass == null ? "" : envPass;

        try { Files.createDirectories(dataDir); } catch (Exception ignored) {}
    }

    public Connection getConnection() throws SQLException {
        if (url.startsWith("jdbc:sqlite")) {
            return DriverManager.getConnection(url);
        }
        // Внешняя БД (Postgres/MySQL и т.п.)
        if (!user.isBlank() || !password.isBlank()) {
            return DriverManager.getConnection(url, user, password);
        }
        return DriverManager.getConnection(url);
    }
}
