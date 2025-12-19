package tvguide.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import tvguide.db.Db;
import tvguide.db.Migrations;
import tvguide.repository.SqlRepository;
import tvguide.service.Service;

import java.nio.file.Path;
import java.sql.Connection;

@WebListener
public class AppBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            String dataDirEnv = System.getenv("DATA_DIR");
            Path dataDir = (dataDirEnv == null || dataDirEnv.isBlank())
                    ? Path.of("data") : Path.of(dataDirEnv);

            Db db = new Db(dataDir);
            try (Connection conn = db.getConnection()) {
                Migrations.ensureSchema(conn);
            }

            SqlRepository repo = new SqlRepository(db);
            Service service = new Service(repo);

            sce.getServletContext().setAttribute("service", service);
            System.out.println("AppBootstrap: сервис инициализирован.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppBootstrap: остановка приложения.");
    }
}
