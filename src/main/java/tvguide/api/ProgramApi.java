package tvguide.api;

import tvguide.model.ProgramItem;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface ProgramApi {
    ProgramItem addProgram(String title, long channelId, long genreId, DayOfWeek day, LocalTime start);
    ProgramItem updateProgram(long id, String title, Long channelId, Long genreId, DayOfWeek day, LocalTime start);
    void deleteProgram(long id);
    List<ProgramItem> listAll();
    List<ProgramItem> listByDay(DayOfWeek day);
}
