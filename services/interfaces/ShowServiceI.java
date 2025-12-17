package services.interfaces;

import models.*;
import models.enumerations.SeatTypes;

import java.util.List;

public interface ShowServiceI {
    Show getShowsForMovie(String screenId, String movieId, Theater theater);

    List<ShowDate> getAvailableDates(Show show);

    List<ShowTime> getAvailableShowTimes(ShowDate showDate);

    ShowTime getShowTimeById(Show show, String dateId, String timeId);

    List<String> getAvailableSeats(Screen screen, ShowTime showTime);

    List<String> getSeatsByType(Screen screen, SeatTypes type);

    boolean isSeatAvailable(ShowTime showTime, String seatLabel);

    List<Screen> findScreensWithMovie(Theater theater, String movieId);
}
