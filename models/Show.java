package models;

import java.util.HashMap;

public class Show {
    private final String showId;
    private final String movieId;
    private final HashMap<String, ShowDate> dates;

    public Show(String showId, String movieId, HashMap<String, ShowDate> dates) {
        this.showId = showId;
        this.movieId = movieId;
        this.dates = dates;
    }

    public String getShowId() {
        return showId;
    }

    public String getMovieId() {
        return movieId;
    }

    public HashMap<String, ShowDate> getDates() {
        return dates;
    }

}
