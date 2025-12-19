package tvguide.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import tvguide.model.Channel;

import java.io.IOException;
import java.util.List;

@WebServlet("/channels")
public class ChannelsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        pageStart(req, resp, "Каналы");

        List<Channel> list = service().listChannels();
        var out = resp.getWriter();

        out.println("<h1>Каналы</h1>");

        out.println("<table class='table'>");
        out.println("<thead><tr><th>ID</th><th>Название</th><th>Действия</th></tr></thead>");
        out.println("<tbody>");

        if (list.isEmpty()) {
            out.println("<tr><td colspan='3'>(пусто)</td></tr>");
        } else {
            for (Channel c : list) {
                out.printf("<tr><td>%d</td><td>%s</td><td>%n", c.getId(), esc(c.getName()));

                // Удалить
                out.println("<form style='display:inline' method='post'>");
                out.println("<input type='hidden' name='action' value='delete'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>%n", c.getId());
                out.println("<button class='btn' type='submit' onclick='return confirm(\"Удалить?\")'>Удалить</button>");
                out.println("</form>");

                out.println("<div style='height:8px'></div>");


                out.println("<form method='post'>");
                out.println("<input type='hidden' name='action' value='rename'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>%n", c.getId());
                out.println("<div class='form-row'>");
                out.println("<input name='name' placeholder='Новое имя' required/>");
                out.println("<button class='btn' type='submit'>Переименовать</button>");
                out.println("</div>");
                out.println("</form>");

                out.println("</td></tr>");
            }
        }

        out.println("</tbody></table>");

        out.println("<h2>Добавить канал</h2>");
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
                service().addChannel(req.getParameter("name"));

            } else if ("rename".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service().updateChannel(id, req.getParameter("name"));

            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service().deleteChannel(id);
            }

            resp.sendRedirect(req.getContextPath() + "/channels");

        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().println("Ошибка: " + e.getMessage());
        }
    }
}
