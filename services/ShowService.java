package services;

import models.*;
import models.enumerations.SeatTypes;
import services.interfaces.ShowServiceI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowService implements ShowServiceI {

    public Show getShowsForMovie(String screenId, String movieId, Theater theater) {
        Screen screen = theater.getScreens().get(screenId);
        if (screen == null || screen.getShows() == null) return null;

        for (Show s : screen.getShows()) {
            if (s.getMovieId() != null && s.getMovieId().equals(movieId)) {
                return s;
            }
        }
        return null;
    }

    public List<ShowDate> getAvailableDates(Show show) {
        if (show.getDates() == null) return new ArrayList<>();
        List<ShowDate> dates = new ArrayList<>(show.getDates().values());
        dates.sort(Comparator.comparing(ShowDate::getShowDateDate));
        return dates;
    }

    public List<ShowTime> getAvailableShowTimes(ShowDate showDate) {
        if (showDate.getShowTimes() == null) return new ArrayList<>();
        List<ShowTime> times = new ArrayList<>(showDate.getShowTimes().values());
        times.sort(Comparator.comparing(ShowTime::getStartTime));
        return times;
    }

    public ShowTime getShowTimeById(Show show, String dateId, String timeId) {
        ShowDate date = show.getDates().get(dateId);
        if (date == null) return null;
        return date.getShowTimes().get(timeId);
    }

    public List<String> getAvailableSeats(Screen screen, ShowTime showTime) {
        List<String> result = new ArrayList<>();
        for (String label : screen.getSeatTypeMap().keySet()) {
            String booked = showTime.getSeats().get(label);
            if (booked == null) {
                result.add(label);
            }
        }
        return result;
    }

    public List<String> getSeatsByType(Screen screen, SeatTypes type) {
        List<String> result = new ArrayList<>();
        for (var entry : screen.getSeatTypeMap().entrySet()) {
            if (entry.getValue() == type) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public boolean isSeatAvailable(ShowTime showTime, String seatLabel) {
        return showTime.getSeats().get(seatLabel) == null;
    }

    public List<Screen> findScreensWithMovie(Theater theater, String movieId) {
        List<Screen> result = new ArrayList<>();
        for (Screen s : theater.getScreens().values()) {
            if (s.getShows() == null) continue;
            for (Show show : s.getShows()) {
                if (show.getMovieId() != null && show.getMovieId().equals(movieId)) {
                    result.add(s);
                    break;
                }
            }
        }
        return result;
    }
}
