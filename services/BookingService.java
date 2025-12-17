package services;

import models.*;
import models.enumerations.BookingStatus;
import models.enumerations.SeatTypes;
import repository.DataStore;
import services.interfaces.BookingServiceI;
import wrappers.ValidateSeatResult;

import java.time.LocalDateTime;
import java.util.*;

public class BookingService implements BookingServiceI {

    public Booking createBooking(String userId,
                                 String theaterId,
                                 String screenId,
                                 String showId,
                                 String dateId,
                                 String timeId,
                                 Set<String> seatLabels) {

        Theater theater = DataStore.theaters.get(theaterId);
        if (theater == null) return null;

        Screen screen = theater.getScreens().get(screenId);
        if (screen == null) return null;

        Show show = null;
        for (Show s : screen.getShows()) {
            if (s.getShowId().equals(showId)) {
                show = s;
                break;
            }
        }
        if (show == null) return null;

        ShowDate showDate = show.getDates().get(dateId);
        if (showDate == null) return null;

        ShowTime showTime = showDate.getShowTimes().get(timeId);
        if (showTime == null) return null;

        List<SeatSnapShot> snapshots = createSeatSnapshots(seatLabels, screen, showTime);
        double total = calculateTotalPrice(seatLabels, screen, showTime);

        String bookingId = UUID.randomUUID().toString();
        bookSeats(showTime, seatLabels, bookingId);

        Booking booking = new Booking(
                bookingId,
                userId,
                theaterId,
                screenId,
                showId,
                dateId,
                timeId,
                snapshots,
                new ArrayList<>(),
                BookingStatus.BOOKED,
                total,
                LocalDateTime.now(),
                null
        );

        DataStore.bookings.put(bookingId, booking);

        User user = DataStore.users.get(userId);
        if (user != null) {
            user.addUserBookingHistory(booking);
        }
        theater.getTheaterBookings().add(bookingId);
        return booking;
    }

    public ValidateSeatResult validateSeats(Set<String> seatLabels, Screen screen, ShowTime showTime) {
        if (seatLabels == null || seatLabels.isEmpty()) {
            return new ValidateSeatResult(false, "No seats selected.");
        }
        if (screen == null || screen.getSeatTypeMap() == null) {
            return new ValidateSeatResult(false, "Invalid screen data.");
        }
        if (showTime == null || showTime.getSeats() == null) {
            return new ValidateSeatResult(false, "Invalid show time data.");
        }

        List<String> invalidSeats = new ArrayList<>();
        List<String> alreadyBooked = new ArrayList<>();

        for (String label : seatLabels) {
            if (!screen.getSeatTypeMap().containsKey(label)) {
                invalidSeats.add(label);
            } else {
                String mapped = showTime.getSeats().get(label);
                if (mapped != null) {
                    alreadyBooked.add(label);
                }
            }
        }

        if (!invalidSeats.isEmpty()) {
            return new ValidateSeatResult(false, "Invalid seat labels: " + invalidSeats);
        }
        if (!alreadyBooked.isEmpty()) {
            return new ValidateSeatResult(false, "Seats already booked: " + alreadyBooked);
        }
        return new ValidateSeatResult(true, "Valid");
    }

    public double calculateTotalPrice(Set<String> seatLabels, Screen screen, ShowTime showTime) {
        double total = 0.0;
        for (String label : seatLabels) {
            SeatTypes type = screen.getSeatTypeMap().get(label);
            if (type == SeatTypes.PREMIUM) total += showTime.getPriceOfPremiumSeat();
            else if (type == SeatTypes.CLASSIC) total += showTime.getPriceOfClassicSeat();
            else total += showTime.getPriceOfEconomySeat();
        }
        return total;
    }

    public List<SeatSnapShot> createSeatSnapshots(Set<String> seatLabels, Screen screen, ShowTime showTime) {
        List<SeatSnapShot> snapshots = new ArrayList<>();
        for (String label : seatLabels) {
            SeatTypes type = screen.getSeatTypeMap().get(label);
            double price = (type == SeatTypes.PREMIUM) ? showTime.getPriceOfPremiumSeat()
                    : (type == SeatTypes.CLASSIC) ? showTime.getPriceOfClassicSeat()
                    : showTime.getPriceOfEconomySeat();
            snapshots.add(new SeatSnapShot(label, type, price));
        }
        return snapshots;
    }

    public void bookSeats(ShowTime showTime, Set<String> seatLabels, String bookingId) {
        for (String label : seatLabels) {
            showTime.getSeats().put(label, bookingId);
        }
    }

    public List<Booking> getUserBookings(String userId) {
        User user = DataStore.users.get(userId);
        if (user == null || user.getUserBookingHistory() == null) return new ArrayList<>();
        return user.getUserBookingHistory();
    }

    public List<Booking> getCancellableBookings(String userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : getUserBookings(userId)) {
            if (b.getBookingStatus() == BookingStatus.BOOKED) {
                result.add(b);
            }
        }
        return result;
    }

    public ValidateSeatResult validateSeatsForCancellation(Booking booking, Set<String> seatLabels) {
        if (booking == null || booking.getBookingSeat() == null || booking.getBookingSeat().isEmpty()) {
            return new ValidateSeatResult(false, "No seats recorded in this booking.");
        }
        if (seatLabels == null || seatLabels.isEmpty()) {
            return new ValidateSeatResult(false, "No seats selected.");
        }

        List<String> invalid = new ArrayList<>();
        for (String label : seatLabels) {
            boolean found = false;
            for (SeatSnapShot snap : booking.getBookingSeat()) {
                if (snap != null && label.equals(snap.getSeatLabel())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                invalid.add(label);
            }
        }

        if (!invalid.isEmpty()) {
            return new ValidateSeatResult(false, "Invalid seats: " + invalid);
        }
        return new ValidateSeatResult(true, "Valid");
    }
    public Booking cancelSeats(String bookingId, Set<String> seatLabels) {
        Booking booking = DataStore.bookings.get(bookingId);
        if (booking == null) return null;

        Theater theater = DataStore.theaters.get(booking.getTheaterId());
        if (theater == null || theater.getScreens() == null) return null;

        Screen screen = theater.getScreens().get(booking.getScreenId());
        if (screen == null || screen.getShows() == null) return null;

        Show show = null;
        for (Show s : screen.getShows()) {
            if (s != null && booking.getShowId().equalsIgnoreCase(s.getShowId())) {
                show = s;
                break;
            }
        }
        if (show == null || show.getDates() == null) return null;

        ShowDate showDate = show.getDates().get(booking.getShowDateId());
        if (showDate == null || showDate.getShowTimes() == null) return null;

        ShowTime showTime = showDate.getShowTimes().get(booking.getShowTimeId());
        if (showTime == null) return null;

        releaseSeats(showTime, seatLabels, bookingId);

        List<SeatSnapShot> currentSeats = booking.getBookingSeat();
        if (currentSeats == null) {
            currentSeats = new ArrayList<>();
            booking.setBookingSeat(currentSeats);
        }
        if (booking.getCancelSeat() == null) {
            booking.setCancelSeat(new ArrayList<>());
        }

        double newPrice = booking.getBookingPrice();
        Iterator<SeatSnapShot> it = currentSeats.iterator();
        while (it.hasNext()) {
            SeatSnapShot snap = it.next();
            if (snap == null) continue;
            if (seatLabels.contains(snap.getSeatLabel())) {
                newPrice -= snap.getSeatPrice();
                booking.getCancelSeat().add(snap);
                it.remove();
            }
        }

        booking.setBookingPrice(newPrice);
        booking.setBookingCancelledAt(LocalDateTime.now());

        if (currentSeats.isEmpty()) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            System.out.println("All seats cancelled. Booking marked as CANCELLED.");
        } else {
            System.out.println("Partial cancellation done.");
        }

        return booking;
    }

    public void releaseSeats(ShowTime showTime, Set<String> seatLabels, String bookingId) {
        if (showTime == null || showTime.getSeats() == null || seatLabels == null) return;
        for (String seatLabel : seatLabels) {
            String mapped = showTime.getSeats().get(seatLabel);
            if (mapped != null && mapped.equals(bookingId)) {
                showTime.getSeats().put(seatLabel, null);
            }
        }
    }
}
