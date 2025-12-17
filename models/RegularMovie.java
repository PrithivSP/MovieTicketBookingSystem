package models;

import models.enumerations.Genre;
import models.enumerations.Languages;
import models.enumerations.MovieCertificate;

public non-sealed class RegularMovie extends Movie {

    public RegularMovie(String movieId, String movieName, MovieCertificate movieEligibility,
                        String movieDescription, Genre movieGenre, Languages movieLanguage,
                        String movieDuration) {
        super(movieId, movieName, movieEligibility, movieDescription,
                movieGenre, movieLanguage, movieDuration, "2D");
    }
}
