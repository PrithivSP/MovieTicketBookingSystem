package models;

import models.enumerations.Genre;
import models.enumerations.Languages;
import models.enumerations.MovieCertificate;

public non-sealed class ThreeDMovie extends Movie {
    private final double glassDeposit;
    private final boolean motionSicknessRisk;
    private final boolean isConvertedFrom2D;

    public ThreeDMovie(String movieId, String movieName, MovieCertificate movieEligibility,
                       String movieDescription, Genre movieGenre, Languages movieLanguage,
                       String movieDuration, double glassDeposit,
                       boolean motionSicknessRisk, boolean isConvertedFrom2D) {

        super(movieId, movieName, movieEligibility, movieDescription,
                movieGenre, movieLanguage, movieDuration, "3D");

        this.glassDeposit = glassDeposit;
        this.motionSicknessRisk = motionSicknessRisk;
        this.isConvertedFrom2D = isConvertedFrom2D;
    }

    public String getGlassInfo() {
            String info = "Deposit ₹" + glassDeposit + ".";
            if (isConvertedFrom2D) {
                info += ", converted from 2D";
            }
            if (motionSicknessRisk) {
                info += " — may cause mild motion sickness for sensitive viewers";
            }
            return info;
    }

    public double getGlassDeposit() {
        return glassDeposit;
    }

    public boolean hasMotionSicknessRisk() {
        return motionSicknessRisk;
    }

    public boolean isConvertedFrom2D() {
        return isConvertedFrom2D;
    }

     //Polymorphism display line for listing movies
    @Override
    public String getDisplayLine() {
        return super.getDisplayLine() + " | \n\t" + getGlassInfo();
    }
}
