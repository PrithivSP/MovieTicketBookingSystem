package models;

import models.enumerations.SeatTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class Screen {
    private final String screenId;
    private final String screenName;
    private final int noOfPremiumSeats;
    private final int noOfClassicSeats;
    private final int noOfEconomySeats;
    private final int noOfSeatsPerRows;

    private final HashMap<String, SeatTypes> seatTypeMap;
    private final ArrayList<Show> shows;

    public String getScreenId() {
        return screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public int getNoOfPremiumSeats() {
        return noOfPremiumSeats;
    }

    public int getNoOfClassicSeats() {
        return noOfClassicSeats;
    }

    public int getNoOfEconomySeats() {
        return noOfEconomySeats;
    }

    public int getNoOfSeatsPerRows() {
        return noOfSeatsPerRows;
    }

    public HashMap<String, SeatTypes> getSeatTypeMap() {
        return seatTypeMap;
    }

    public ArrayList<Show> getShows() {
        return shows;
    }

    public Screen(String screenId, String screenName, int noOfPremiumSeats, int noOfClassicSeats, int noOfEconomySeats, int noOfSeatsPerRows, HashMap<String, SeatTypes> seatTypeMap, ArrayList<Show> shows) {
        this.screenId = screenId;
        this.screenName = screenName;
        this.noOfPremiumSeats = noOfPremiumSeats;
        this.noOfClassicSeats = noOfClassicSeats;
        this.noOfEconomySeats = noOfEconomySeats;
        this.noOfSeatsPerRows = noOfSeatsPerRows;
        this.seatTypeMap = seatTypeMap;
        this.shows = shows;
    }
}
