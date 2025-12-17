package models;

import models.enumerations.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final String userId;
    private final String theaterId;
    private final String screenId;
    private final String showId;
    private final String showDateId;
    private final String showTimeId;
    private List<SeatSnapShot> bookingSeat;
    private List<SeatSnapShot> cancelSeat;
    private BookingStatus bookingStatus;
    private double bookingPrice;
    private final LocalDateTime bookingCreatedAt;
    private LocalDateTime bookingCancelledAt;

    public Booking(String bookingId, String userId, String theaterId, String screenId, String showId, String showDateId, String showTimeId, List<SeatSnapShot> bookingSeat, List<SeatSnapShot> cancelSeat, BookingStatus bookingStatus, double bookingPrice, LocalDateTime bookingCreatedAt, LocalDateTime bookingCancelledAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.theaterId = theaterId;
        this.screenId = screenId;
        this.showId = showId;
        this.showDateId = showDateId;
        this.showTimeId = showTimeId;
        this.bookingSeat = bookingSeat;
        this.cancelSeat = cancelSeat;
        this.bookingStatus = bookingStatus;
        this.bookingPrice = bookingPrice;
        this.bookingCreatedAt = bookingCreatedAt;
        this.bookingCancelledAt = bookingCancelledAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public String getScreenId() {
        return screenId;
    }

    public String getShowId() {
        return showId;
    }

    public String getShowDateId() {
        return showDateId;
    }

    public String getShowTimeId() {
        return showTimeId;
    }

    public List<SeatSnapShot> getBookingSeat() {
        return bookingSeat;
    }

    public List<SeatSnapShot> getCancelSeat() {
        return cancelSeat;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public double getBookingPrice() {
        return bookingPrice;
    }

    public LocalDateTime getBookingCreatedAt() {
        return bookingCreatedAt;
    }

    public LocalDateTime getBookingCancelledAt() {
        return bookingCancelledAt;
    }

    public void setCancelSeat(List<SeatSnapShot> cancelSeat) {
        this.cancelSeat = cancelSeat;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    public void setBookingPrice(double bookingPrice) {
        this.bookingPrice = bookingPrice;
    }
    public void setBookingSeat(List<SeatSnapShot> bookingSeat) {
        this.bookingSeat = bookingSeat;
    }
    public void setBookingCancelledAt(LocalDateTime bookingCancelledAt) {
        this.bookingCancelledAt = bookingCancelledAt;
    }
}
