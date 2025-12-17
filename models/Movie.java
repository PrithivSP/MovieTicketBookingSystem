package models;

import models.enumerations.Genre;
import models.enumerations.Languages;
import models.enumerations.MovieCertificate;

public sealed class Movie permits RegularMovie, ThreeDMovie {
    private final String movieId;
    private final String movieName;
    private final MovieCertificate movieEligibility;
    private final String movieDescription;
    private final Genre movieGenre;
    private final Languages movieLanguage;
    private final String movieDuration;
    private final String movieType;

    protected Movie(String movieId, String movieName, MovieCertificate movieEligibility,
                    String movieDescription, Genre movieGenre, Languages movieLanguage,
                    String movieDuration, String movieType) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.movieEligibility = movieEligibility;
        this.movieDescription = movieDescription;
        this.movieGenre = movieGenre;
        this.movieLanguage = movieLanguage;
        this.movieDuration = movieDuration;
        this.movieType = movieType;
    }

    public String getDisplayLine() {
        return String.format(" %s | %s | %s | %s",
                movieName,
                movieGenre == null ? "-" : movieGenre,
                movieLanguage == null ? "-" : movieLanguage,
                movieType == null ? "-" : movieType
        );
    }

    public String getMovieType() {
        return movieType;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public MovieCertificate getMovieEligibility() {
        return movieEligibility;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public Genre getMovieGenre() {
        return movieGenre;
    }

    public Languages getMovieLanguage() {
        return movieLanguage;
    }

    public String getMovieDuration() {
        return movieDuration;
    }
}
