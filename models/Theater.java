package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Theater {
    private final String theaterId;
    private final String theaterName;
    private final String theaterAddress;
    private final String theaterCity;
    private final HashMap<String, Screen> screens;
    private final HashSet<String> movies; //moviesId
    private final ArrayList<String> theaterBookings;

    public String getTheaterId() {
        return theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public String getTheaterAddress() {
        return theaterAddress;
    }

    public String getTheaterCity() {
        return theaterCity;
    }

    public HashMap<String, Screen> getScreens() {
        return screens;
    }

    public HashSet<String> getMovies() {
        return movies;
    }

    public ArrayList<String> getTheaterBookings() {
        return theaterBookings;
    }

    void addTheaterBooking(String bookingId) {
        this.theaterBookings.add(bookingId);
    }

    public Theater(String theaterId, String theaterName, String theaterAddress, String theaterCity, HashMap<String, Screen> screens, HashSet<String> movies, ArrayList<String> theaterBookings) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.theaterAddress = theaterAddress.toLowerCase();
        this.theaterCity = theaterCity.toLowerCase();
        this.screens = screens;
        this.movies = movies;
        this.theaterBookings = theaterBookings;
    }
}
