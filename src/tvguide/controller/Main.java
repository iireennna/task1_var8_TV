package tvguide.controller;

import tvguide.api.ChannelApi;
import tvguide.api.GenreApi;
import tvguide.api.ProgramApi;
import tvguide.model.Channel;
import tvguide.model.Genre;
import tvguide.model.ProgramItem;
import tvguide.service.Service;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import tvguide.db.Db;
import tvguide.db.Migrations;
import tvguide.repository.SqlRepository;

public class Main {
    public static void main(String[] args) {
        try {

            Db db = new Db(Path.of("data"));
            try (var conn = db.getConnection()) {
                Migrations.ensureSchema(conn);
            }

            SqlRepository repo = new SqlRepository(db);
            Service service = new Service(repo);

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    System.out.println("\nВыход из приложения...")
            ));

            runConsole(service, service, service);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    private static void runConsole(ChannelApi channelApi, GenreApi genreApi, ProgramApi programApi) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    
                    Планировщик на неделю
                    1) Каналы
                    2) Жанры
                    3) Передачи
                    0) Выход
                    Выберите пункт: """);
            String s = sc.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> channelsMenu(sc, channelApi);
                    case "2" -> genresMenu(sc, genreApi);
                    case "3" -> programsMenu(sc, channelApi, genreApi, programApi);
                    case "0" -> { System.out.println("Пока!"); return; }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (Exception ex) {
                System.out.println("ОШИБКА: " + ex.getMessage());
            }
        }
    }

    private static void channelsMenu(Scanner sc, ChannelApi api) {
        while (true) {
            System.out.println("""
                    -- Каналы --
                    1) Список
                    2) Добавить
                    3) Переименовать
                    4) Удалить
                    0) Назад
                    Выберите пункт: """);
            String s = sc.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> {
                        List<Channel> list = api.listChannels();
                        list.forEach(c -> System.out.println(c.getId() + ") " + c.getName()));
                        if (list.isEmpty()) System.out.println("(пусто)");
                    }
                    case "2" -> {
                        System.out.print("Название канала: ");
                        String name = sc.nextLine();
                        Channel c = api.addChannel(name);
                        System.out.println("Добавлен: " + c);
                    }
                    case "3" -> {
                        long id = askId(sc, "ID канала");
                        System.out.print("Новое название: ");
                        String newName = sc.nextLine();
                        Channel c = api.updateChannel(id, newName);
                        System.out.println("Обновлён: " + c);
                    }
                    case "4" -> {
                        long id = askId(sc, "ID канала");
                        api.deleteChannel(id);
                        System.out.println("Удалён.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (Exception ex) {
                System.out.println("ОШИБКА: " + ex.getMessage());
            }
        }
    }

    private static void genresMenu(Scanner sc, GenreApi api) {
        while (true) {
            System.out.println("""
                    -- Жанры --
                    1) Список
                    2) Добавить
                    3) Переименовать
                    4) Удалить
                    0) Назад
                    Выберите пункт: """);
            String s = sc.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> {
                        List<Genre> list = api.listGenres();
                        list.forEach(g -> System.out.println(g.getId() + ") " + g.getName()));
                        if (list.isEmpty()) System.out.println("(пусто)");
                    }
                    case "2" -> {
                        System.out.print("Название жанра: ");
                        String name = sc.nextLine();
                        Genre g = api.addGenre(name);
                        System.out.println("Добавлен: " + g);
                    }
                    case "3" -> {
                        long id = askId(sc, "ID жанра");
                        System.out.print("Новое название: ");
                        String newName = sc.nextLine();
                        Genre g = api.updateGenre(id, newName);
                        System.out.println("Обновлён: " + g);
                    }
                    case "4" -> {
                        long id = askId(sc, "ID жанра");
                        api.deleteGenre(id);
                        System.out.println("Удалён.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (Exception ex) {
                System.out.println("ОШИБКА: " + ex.getMessage());
            }
        }
    }

    private static void programsMenu(Scanner sc, ChannelApi channelApi, GenreApi genreApi, ProgramApi api) {
        while (true) {
            System.out.println("""
                    -- Передачи (программа) --
                    1) Список всех
                    2) Список по дню
                    3) Добавить
                    4) Редактировать
                    5) Удалить
                    0) Назад
                    Выберите пункт: """);
            String s = sc.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> {
                        List<ProgramItem> list = api.listAll();
                        printPrograms(list);
                    }
                    case "2" -> {
                        DayOfWeek day = askDay(sc);
                        List<ProgramItem> list = api.listByDay(day);
                        printPrograms(list);
                    }
                    case "3" -> {
                        System.out.print("Название передачи: ");
                        String title = sc.nextLine();
                        long chId = askId(sc, "ID канала");
                        long gId  = askId(sc, "ID жанра");
                        DayOfWeek day = askDay(sc);
                        LocalTime time = askTime(sc, "Время начала (ЧЧ:ММ)");
                        ProgramItem p = api.addProgram(title, chId, gId, day, time);
                        System.out.println("Добавлена: " + p);
                    }
                    case "4" -> {
                        long id = askId(sc, "ID передачи");
                        System.out.print("Новое название (пусто — оставить): ");
                        String title = sc.nextLine();
                        String ch = askOptional(sc, "Новый ID канала (пусто — оставить)");
                        String gn = askOptional(sc, "Новый ID жанра (пусто — оставить)");
                        String d  = askOptional(sc, "Новый день (пн..вс или MONDAY..SUNDAY) (пусто — оставить)");
                        String t  = askOptional(sc, "Новое время ЧЧ:ММ (пусто — оставить)");
                        ProgramItem p = api.updateProgram(
                                id,
                                title.isBlank()? null: title,
                                ch.isBlank()? null: Long.parseLong(ch),
                                gn.isBlank()? null: Long.parseLong(gn),
                                d.isBlank()? null: parseDay(d),
                                t.isBlank()? null: LocalTime.parse(t)
                        );
                        System.out.println("Обновлена: " + p);
                    }
                    case "5" -> {
                        long id = askId(sc, "ID передачи");
                        api.deleteProgram(id);
                        System.out.println("Удалена.");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (Exception ex) {
                System.out.println("ОШИБКА: " + ex.getMessage());
            }
        }
    }

    private static void printPrograms(List<ProgramItem> list) {
        if (list.isEmpty()) { System.out.println("(пусто)"); return; }
        for (ProgramItem p: list) {
            System.out.printf("%d) %-24s  %-10s  %s  канал#%d  жанр#%d%n",
                    p.getId(), p.getTitle(), russianDay(p.getDay()), p.getStartTime(), p.getChannelId(), p.getGenreId());
        }
    }

    private static long askId(Scanner sc, String label) {
        System.out.print(label + ": ");
        return Long.parseLong(sc.nextLine().trim());
    }
    private static String askOptional(Scanner sc, String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }
    private static DayOfWeek askDay(Scanner sc) {
        System.out.print("День недели (пн..вс или MONDAY..SUNDAY): ");
        String s = sc.nextLine().trim();
        return parseDay(s);
    }

    private static DayOfWeek parseDay(String s) {
        String v = s.trim().toLowerCase(Locale.ROOT);
        switch (v) {
            case "1", "пн", "понедельник" -> { return DayOfWeek.MONDAY; }
            case "2", "вт", "вторник" -> { return DayOfWeek.TUESDAY; }
            case "3", "ср", "среда" -> { return DayOfWeek.WEDNESDAY; }
            case "4", "чт", "четверг" -> { return DayOfWeek.THURSDAY; }
            case "5", "пт", "пятница" -> { return DayOfWeek.FRIDAY; }
            case "6", "сб", "суббота" -> { return DayOfWeek.SATURDAY; }
            case "7", "вс", "воскресенье" -> { return DayOfWeek.SUNDAY; }
            default -> { return DayOfWeek.valueOf(s.toUpperCase(Locale.ROOT)); }
        }
    }

    private static String russianDay(DayOfWeek d) {
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

    private static LocalTime askTime(Scanner sc, String label) {
        System.out.print(label + ": ");
        return LocalTime.parse(sc.nextLine().trim());
    }
}
