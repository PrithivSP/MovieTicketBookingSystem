package services;

import models.Movie;
import models.User;
import models.enumerations.Genre;
import models.enumerations.Languages;
import repository.DataStore;
import services.interfaces.MovieServiceI;

import java.util.ArrayList;
import java.util.List;

public class MovieService implements MovieServiceI {

    public List<Movie> getAllMovies() {
        return new ArrayList<>(DataStore.movies.values());
    }

    public Movie getMovieById(String movieId) {
        return DataStore.movies.get(movieId);
    }


    public List<Movie> searchMovieByName(String name) {
        List<Movie> movies = new ArrayList<>();
        for (Movie m : DataStore.movies.values()) {
            if (m.getMovieName().toLowerCase().contains(name.toLowerCase())) {
                movies.add(m);
            }
        }
        return movies;
    }

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

    public boolean isUserEligible(User user, Movie movie) {
        return user.getUserAge() >= movie.getMovieEligibility().getMinAge();
    }

}
