package tvguide.api;

import tvguide.model.Genre;
import java.util.List;

public interface GenreApi {
    Genre addGenre(String name);
    void deleteGenre(long id);
    Genre updateGenre(long id, String newName);
    List<Genre> listGenres();
}
