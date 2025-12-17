package services.interfaces;

import models.*;
import wrappers.ValidateSeatResult;
import java.util.*;

public interface BookingServiceI {
    Booking createBooking(String userId,
                          String theaterId,
                          String screenId,
                          String showId,
                          String dateId,
                          String timeId,
                          Set<String> seatLabels);

    ValidateSeatResult validateSeats(Set<String> seatLabels, Screen screen, ShowTime showTime);

    double calculateTotalPrice(Set<String> seatLabels, Screen screen, ShowTime showTime);

    List<SeatSnapShot> createSeatSnapshots(Set<String> seatLabels, Screen screen, ShowTime showTime);

    void bookSeats(ShowTime showTime, Set<String> seatLabels, String bookingId);

    List<Booking> getUserBookings(String userId);

    List<Booking> getCancellableBookings(String userId);

    ValidateSeatResult validateSeatsForCancellation(Booking booking, Set<String> seatLabels);

    Booking cancelSeats(String bookingId, Set<String> seatLabels);

    void releaseSeats(ShowTime showTime, Set<String> seatLabels, String bookingId);
}
