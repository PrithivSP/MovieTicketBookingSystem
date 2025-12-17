package models;

import models.enumerations.SeatTypes;

public class SeatSnapShot {
    private final String seatLabel;
    private final SeatTypes seatType;
    private final double seatPrice;

    public SeatSnapShot(String seatLabel, SeatTypes seatType, double seatPrice) {
        this.seatLabel = seatLabel;
        this.seatType = seatType;
        this.seatPrice = seatPrice;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public SeatTypes getSeatType() {
        return seatType;
    }

    public double getSeatPrice() {
        return seatPrice;
    }
}
