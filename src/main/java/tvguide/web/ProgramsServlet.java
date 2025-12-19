package tvguide.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/programs")
public class ProgramsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        pageStart(req, resp, "Передачи");

        List<ProgramItem> list = service().listAll();
        List<Channel> channels = service().listChannels();
        List<Genre> genres = service().listGenres();

        var out = resp.getWriter();

        out.println("<h1>Передачи на неделю</h1>");

        out.println("<table class='table'>");
        out.println("<thead><tr>"
                + "<th>ID</th><th>Название</th><th>Канал</th><th>Жанр</th><th>День</th><th>Время</th><th>Действия</th>"
                + "</tr></thead>");
        out.println("<tbody>");

        if (list.isEmpty()) {
            out.println("<tr><td colspan='7'>(пусто)</td></tr>");
        } else {
            for (ProgramItem p : list) {
                out.printf("<tr><td>%d</td><td>%s</td><td>%d</td><td>%d</td><td>%s</td><td>%s</td><td>",
                        p.getId(),
                        esc(p.getTitle()),
                        p.getChannelId(),
                        p.getGenreId(),
                        esc(ruDay(p.getDay())),
                        esc(String.valueOf(p.getStartTime()))
                );


                out.println("<form style='display:inline' method='post'>");
                out.println("<input type='hidden' name='action' value='delete'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>", p.getId());
                out.println("<button class='btn' type='submit' onclick='return confirm(\"Удалить?\")'>Удалить</button>");
                out.println("</form>");

                out.println("<div style='height:8px'></div>");


                out.println("<form method='post'>");
                out.println("<input type='hidden' name='action' value='update'/>");
                out.printf("<input type='hidden' name='id' value='%d'/>", p.getId());
                out.println("<div class='form-row'>");

                out.printf("<input name='title' placeholder='Название' value='%s'/>", esc(p.getTitle()));

                out.println("<select name='channelId'>");
                for (Channel c : channels) {
                    String sel = (c.getId() == p.getChannelId()) ? " selected" : "";
                    out.printf("<option value='%d'%s>%s</option>", c.getId(), sel, esc(c.getName()));
                }
                out.println("</select>");

                out.println("<select name='genreId'>");
                for (Genre g : genres) {
                    String sel = (g.getId() == p.getGenreId()) ? " selected" : "";
                    out.printf("<option value='%d'%s>%s</option>", g.getId(), sel, esc(g.getName()));
                }
                out.println("</select>");


                out.println("<select name='day'>");
                for (DayOfWeek d : DayOfWeek.values()) {
                    String sel = (d == p.getDay()) ? " selected" : "";
                    out.printf("<option value='%s'%s>%s</option>", d.name(), sel, esc(ruDay(d)));
                }
                out.println("</select>");

                out.printf("<input name='time' type='time' value='%s'/>", esc(String.valueOf(p.getStartTime())));
                out.println("<button class='btn' type='submit'>Сохранить</button>");

                out.println("</div>");
                out.println("</form>");

                out.println("</td></tr>");
            }
        }

        out.println("</tbody></table>");


        out.println("<h2>Добавить передачу</h2>");
        out.println("<form method='post'>");
        out.println("<input type='hidden' name='action' value='add'/>");
        out.println("<div class='form-row'>");

        out.println("<input name='title' placeholder='Название' required/>");

        out.println("<select name='channelId'>");
        for (Channel c : channels) {
            out.printf("<option value='%d'>%s</option>", c.getId(), esc(c.getName()));
        }
        out.println("</select>");

        out.println("<select name='genreId'>");
        for (Genre g : genres) {
            out.printf("<option value='%d'>%s</option>", g.getId(), esc(g.getName()));
        }
        out.println("</select>");


        out.println("<select name='day'>");
        for (DayOfWeek d : DayOfWeek.values()) {
            out.printf("<option value='%s'>%s</option>", d.name(), esc(ruDay(d)));
        }
        out.println("</select>");

        out.println("<input name='time' type='time' required/>");
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
                String title = req.getParameter("title");
                long chId = Long.parseLong(req.getParameter("channelId"));
                long gId = Long.parseLong(req.getParameter("genreId"));
                DayOfWeek day = DayOfWeek.valueOf(req.getParameter("day"));
                LocalTime time = LocalTime.parse(req.getParameter("time"));
                service().addProgram(title, chId, gId, day, time);

            } else if ("update".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                String title = req.getParameter("title");
                Long chId = Long.parseLong(req.getParameter("channelId"));
                Long gId = Long.parseLong(req.getParameter("genreId"));
                DayOfWeek day = DayOfWeek.valueOf(req.getParameter("day"));
                LocalTime time = LocalTime.parse(req.getParameter("time"));
                service().updateProgram(id, title, chId, gId, day, time);

            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service().deleteProgram(id);
            }

            resp.sendRedirect(req.getContextPath() + "/programs");

        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().println("Ошибка: " + e.getMessage());
        }
    }
}
