package services.interfaces;

import models.Screen;
import models.Theater;
import java.util.List;

public interface TheaterServiceI {
    List<Theater> getTheatersInCity(String city);

    List<Theater> getTheatersPlayingMovie(String movieId, String city);

    Theater getTheaterById(String theaterId);

//    List<Screen> getScreensInTheater(String theaterId);

    Screen getScreenById(String theaterId, String screenId);
}
