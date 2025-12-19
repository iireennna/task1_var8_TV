package tvguide.model;

import java.io.Serializable;
import java.util.Objects;

public class Channel implements Serializable {
    private final long id;
    private String name;

    public Channel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return id == channel.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Channel{id=" + id + ", name='" + name + "'}"; }
}
