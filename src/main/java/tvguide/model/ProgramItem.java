package tvguide.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class ProgramItem implements Serializable {
    private final long id;
    private String title;
    private long channelId;
    private long genreId;
    private DayOfWeek day;
    private LocalTime startTime;

    public ProgramItem(long id, String title, long channelId, long genreId, DayOfWeek day, LocalTime startTime) {
        this.id = id;
        this.title = title;
        this.channelId = channelId;
        this.genreId = genreId;
        this.day = day;
        this.startTime = startTime;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getChannelId() { return channelId; }
    public void setChannelId(long channelId) { this.channelId = channelId; }
    public long getGenreId() { return genreId; }
    public void setGenreId(long genreId) { this.genreId = genreId; }
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramItem that = (ProgramItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "ProgramItem{id=" + id + ", title='" + title + "', channelId=" + channelId +
                ", genreId=" + genreId + ", day=" + day + ", startTime=" + startTime + "}";
    }
}
