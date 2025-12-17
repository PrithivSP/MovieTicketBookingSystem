package services.interfaces;

import models.Movie;
import models.User;
import java.util.List;

public interface MovieServiceI {
    List<Movie> getAllMovies();

    Movie getMovieById(String movieId);

    List<Movie> searchMovieByName(String name);

//    public List<Movie> getMoviesByGenre(Genre genre) {
//        List<Movie> result = new ArrayList<>();
//        for (Movie m : DataStore.movies.values()) {
//            if (m.getMovieGenre() == genre) {
//                result.add(m);
//            }
//        }
//        return result;
//    }

//    public List<Movie> getMoviesByLanguage(Languages language) {
//        List<Movie> result = new ArrayList<>();
//        for (Movie m : DataStore.movies.values()) {
//            if (m.getMovieLanguage() == language) {
//                result.add(m);
//            }
//        }
//        return result;
//    }

    boolean isUserEligible(User user, Movie movie);
}
