package tvguide.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import tvguide.model.Genre;

import java.io.IOException;
import java.util.List;

@WebServlet("/genres")
public class GenresServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        pageStart(req, resp, "Жанры");

        List<Genre> list = service().listGenres();
        var out = resp.getWriter();

        out.println("<h1>Жанры</h1>");

        out.println("<table class='table'>");
        out.println("<thead><tr><th>ID</th><th>Название</th><th>Действия</th></tr></thead>");
        out.println("<tbody>");

        if (list.isEmpty()) {
            out.println("<tr><td colspan='3'>(пусто)</td></tr>");
        } else {
            for (Genre g : list) {
                out.printf("<tr><td>%d</td><td>%s</td><td>%n", g.getId(), esc(g.getName()));


                out.println("<form style='display:inline' method='post'>");
                out.println("<input type='hidden' name='action' value='delete'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>%n", g.getId());
                out.println("<button class='btn' type='submit' onclick='return confirm(\"Удалить?\")'>Удалить</button>");
                out.println("</form>");

                out.println("<div style='height:8px'></div>");


                out.println("<form method='post'>");
                out.println("<input type='hidden' name='action' value='rename'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>%n", g.getId());
                out.println("<div class='form-row'>");
                out.println("<input name='name' placeholder='Новое имя' required/>");
                out.println("<button class='btn' type='submit'>Переименовать</button>");
                out.println("</div>");
                out.println("</form>");

                out.println("</td></tr>");
            }
        }

        out.println("</tbody></table>");

        out.println("<h2>Добавить жанр</h2>");
        out.println("<form method='post'>");
        out.println("<input type='hidden' name='action' value='add'/>");
        out.println("<div class='form-row'>");
        out.println("<input name='name' placeholder='Название' required/>");
        out.println("<button class='btn' type='submit'>Добавить</button>");
        out.println("</div>");
        out.println("</form>");

        out.printf("<p><a class='link' href='%s/index.html'>На главную</a></p>%n", req.getContextPath());

        pageEnd(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                service().addGenre(req.getParameter("name"));

            } else if ("rename".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service().updateGenre(id, req.getParameter("name"));

            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service().deleteGenre(id);
            }

            resp.sendRedirect(req.getContextPath() + "/genres");

        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().println("Ошибка: " + e.getMessage());
        }
    }
}
