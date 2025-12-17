package models;

import java.time.LocalTime;
import java.util.HashMap;

public class ShowTime {
    private final String showTimeId;
    private final LocalTime startTime;
    private final double priceOfPremiumSeat;
    private final double priceOfClassicSeat;
    private final double priceOfEconomySeat;
    private final HashMap<String, String> seats;

    public String getShowTimeId() {
        return showTimeId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public double getPriceOfPremiumSeat() {
        return priceOfPremiumSeat;
    }

    public double getPriceOfClassicSeat() {
        return priceOfClassicSeat;
    }

    public double getPriceOfEconomySeat() {
        return priceOfEconomySeat;
    }

    public HashMap<String, String> getSeats() {
        return seats;
    }

    public ShowTime(String showTimeId, LocalTime startTime, double priceOfPremiumSeat, double priceOfClassicSeat, double priceOfEconomySeat, HashMap<String, String> seats) {
        this.showTimeId = showTimeId;
        this.startTime = startTime;
        this.priceOfPremiumSeat = priceOfPremiumSeat;
        this.priceOfClassicSeat = priceOfClassicSeat;
        this.priceOfEconomySeat = priceOfEconomySeat;
        this.seats = seats;
    }

}
