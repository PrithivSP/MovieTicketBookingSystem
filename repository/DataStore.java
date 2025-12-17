package repository;

import models.Booking;
import models.Movie;
import models.Theater;
import models.User;

import java.util.HashMap;

public class DataStore {
    public static HashMap<String, User> users = new HashMap<>();
    public static HashMap<String, Theater> theaters = new HashMap<>();
    public static HashMap<String, Booking> bookings = new HashMap<>();
    public static HashMap<String, Movie> movies = new HashMap<>();

    private DataStore() {

    }
}