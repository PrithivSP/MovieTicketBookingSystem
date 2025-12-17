package services;

import models.Movie;
import models.Screen;
import models.Theater;
import repository.DataStore;
import services.interfaces.TheaterServiceI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TheaterService implements TheaterServiceI {

    public List<Theater> getTheatersInCity(String city) {
        List<Theater> result = new ArrayList<>();
        for (Theater t : DataStore.theaters.values()) {
            if (t.getTheaterCity() != null &&
                    t.getTheaterCity().equalsIgnoreCase(city)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Theater> getTheatersPlayingMovie(String movieId, String city) {
        List<Theater> result = new ArrayList<>();
        for (Theater t : DataStore.theaters.values()) {
            if (t.getTheaterCity() != null &&
                    t.getTheaterCity().equalsIgnoreCase(city) &&
                    t.getMovies() != null &&
                    t.getMovies().contains(movieId)) {
                result.add(t);
            }
        }
        return result;
    }

    public Theater getTheaterById(String theaterId) {
        return DataStore.theaters.get(theaterId);
    }

//    public List<Screen> getScreensInTheater(String theaterId) {
//        Theater t = DataStore.theaters.get(theaterId);
//        if (t == null || t.getScreens() == null) return new ArrayList<>();
//        return new ArrayList<>(t.getScreens().values());
//    }

    public Screen getScreenById(String theaterId, String screenId) {
        Theater t = DataStore.theaters.get(theaterId);
        if (t == null) return null;
        Map<String, Screen> screens = t.getScreens();
        if (screens == null) return null;
        return screens.get(screenId);
    }

//    public List<Movie> getMoviesInTheater(String theaterId) {
//        Theater t = DataStore.theaters.get(theaterId);
//        List<Movie> result = new ArrayList<>();
//        if (t == null || t.getMovies() == null) return result;
//
//        for (String movieId : t.getMovies()) {
//            Movie m = DataStore.movies.get(movieId);
//            if (m != null) result.add(m);
//        }
//        return result;
//    }

}
