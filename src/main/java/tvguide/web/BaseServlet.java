package tvguide.web;

import jakarta.servlet.http.HttpServlet;
import tvguide.service.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;

public abstract class BaseServlet extends HttpServlet {
    protected Service service() {
        return (Service) getServletContext().getAttribute("service");
    }
    protected static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
    protected static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
    protected void pageStart(jakarta.servlet.http.HttpServletRequest req,
                             jakarta.servlet.http.HttpServletResponse resp,
                             String title) throws java.io.IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String ctx = req.getContextPath();
        var out = resp.getWriter();

        out.println("<!doctype html><html lang='ru'><head>");
        out.println("<meta charset='UTF-8'/>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
        out.println("<title>" + esc(title) + "</title>");
        out.println("<link rel='stylesheet' href='" + ctx + "/assets/style.css'/>");
        out.println("</head><body><div class='container'>");

        out.println("<div class='header'>");
        out.println("<div class='brand'>TVGuide</div>");
        out.println("<div class='nav'>"
                + "<a href='" + ctx + "/index.html'>Главная</a>"
                + "<a href='" + ctx + "/channels'>Каналы</a>"
                + "<a href='" + ctx + "/genres'>Жанры</a>"
                + "<a href='" + ctx + "/programs'>Передачи</a>"
                + "</div>");
        out.println("</div>");

        out.println("<div class='card'>");
    }
    protected static String ruDay(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "Понедельник";
            case TUESDAY -> "Вторник";
            case WEDNESDAY -> "Среда";
            case THURSDAY -> "Четверг";
            case FRIDAY -> "Пятница";
            case SATURDAY -> "Суббота";
            case SUNDAY -> "Воскресенье";
        };
    }


    protected void pageEnd(jakarta.servlet.http.HttpServletResponse resp) throws java.io.IOException {
        var out = resp.getWriter();
        out.println("</div></div></body></html>");
    }

}
