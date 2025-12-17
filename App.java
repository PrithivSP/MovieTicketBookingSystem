import exceptions.ExitApp;
import exceptions.ExitToUserMenu;
import models.*;
import models.enumerations.*;
import repository.DataStore;
import services.*;
import services.interfaces.*;
import utils.ConsoleUtils;
import utils.InputValidator;
import wrappers.ValidateSeatResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class App {
    private User currentUser = null;
    private Movie selectedMovie;
    private Theater selectedTheater;
    private Screen selectedScreen;
    private Show selectedShow;
    private ShowDate selectedDate;
    private ShowTime selectedTime;
    private final BufferedReader bufferedReader;

    // ====== Services ======
    private final UserServiceI userService = new UserService();
    private final MovieServiceI movieService = new MovieService();
    private final TheaterServiceI theaterService = new TheaterService();
    private final ShowServiceI showService = new ShowService();
    private final BookingServiceI bookingService = new BookingService();

    public App() {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }


    public void main() {
        addSampleData();
        NavigationFlowState state = NavigationFlowState.AUTH_MENU;
        while (state != NavigationFlowState.EXIT) {
            try {
                state = handleFlowState(state);
            } catch (ExitToUserMenu e) {
                state = NavigationFlowState.USER_MENU;
            } catch (ExitApp e) {
                state = NavigationFlowState.EXIT;
            }
        }
        System.out.println("Thanks for choosing us");
    }

    // state dispatcher
    private NavigationFlowState handleFlowState(NavigationFlowState state) {
        return switch (state) {
            case AUTH_MENU -> showAuthMenu();
            case LOGIN -> loginFlow();
            case SIGNUP -> signUpFlow();

            case USER_MENU -> showUserMenuFlow();

            case BROWSE_MOVIES -> browseMoviesFlow();
            case SEARCH_MOVIE -> searchMovieFlow();
            case BROWSE_THEATER -> browseTheaterNearby();

            case SELECT_THEATER -> selectTheaterFlow();
            case SELECT_SCREEN -> selectScreenFlow();
            case SELECT_DATE -> selectDateFlow();
            case SELECT_TIME -> selectTimeFlow();
            case SELECT_SEATS -> selectSeatsFlow();

            case CHANGE_LOCATION -> changeLocationFlow();
            case USER_HISTORY -> userHistoryFlow();
            case CANCEL_BOOKING -> cancelBookingFlow();
            case UPDATE_PROFILE -> updateProfileFlow();

            case EXIT -> NavigationFlowState.EXIT;
        };
    }

    // flow
    private NavigationFlowState showUserMenuFlow() {
        ConsoleUtils.printHeader("User Menu");
        System.out.println("1. Browse Movies");
        System.out.println("2. Browse Movie by name");
        System.out.println("3. Browse near by theaters");
        System.out.println("4. Change location");
        System.out.println("5. Show Booking History");
        System.out.println("6. Cancel a Booking");
        System.out.println("7. Update Profile");
        System.out.println("0. Logout");
        try {

            int userMenuChoice = ConsoleUtils.readInt("Enter your choice: ");

            return switch (userMenuChoice) {
                case 1 -> NavigationFlowState.BROWSE_MOVIES;
                case 2 -> NavigationFlowState.SEARCH_MOVIE;
                case 3 -> NavigationFlowState.BROWSE_THEATER;
                case 4 -> NavigationFlowState.CHANGE_LOCATION;
                case 5 -> NavigationFlowState.USER_HISTORY;
                case 6 -> NavigationFlowState.CANCEL_BOOKING;
                case 7 -> NavigationFlowState.UPDATE_PROFILE;
                case 0 -> NavigationFlowState.AUTH_MENU;
                default -> NavigationFlowState.USER_MENU;
            };
        } catch (ExitToUserMenu e) {
            System.out.println(e.getMessage());
        }
        return NavigationFlowState.USER_MENU;
    }

    //navigator
    private NavigationFlowState showAuthMenu() {
        System.out.println("Authentication");
        System.out.println("1. Login");
        System.out.println("2. Sign up");
        System.out.println("0. Exit");
        int choice = ConsoleUtils.readAuthChoice("Enter your choice: ");
        return switch (choice) {
            case 1 -> NavigationFlowState.LOGIN;
            case 2 -> NavigationFlowState.SIGNUP;
            case 0 -> NavigationFlowState.EXIT;
            default -> NavigationFlowState.AUTH_MENU;
        };
    }
    private NavigationFlowState loginFlow() {
        int result = loginUser();
        if (result == 1) return NavigationFlowState.USER_MENU;
        if (result == 0) return NavigationFlowState.AUTH_MENU;
        return NavigationFlowState.LOGIN;
    }
    private NavigationFlowState signUpFlow() {
        createUser();
        return NavigationFlowState.AUTH_MENU;
    }
    private NavigationFlowState browseMoviesFlow() {
        Movie movie = chooseMovie();
        if(movie == null) {
            return NavigationFlowState.USER_MENU;
        }
        this.selectedMovie = movie;
        return NavigationFlowState.SELECT_THEATER;
    }
    private NavigationFlowState searchMovieFlow() {
        searchMovieByName();
        if (selectedMovie != null) {
            return NavigationFlowState.SELECT_THEATER;
        }
        return NavigationFlowState.USER_MENU;
    }
    private NavigationFlowState browseTheaterNearby() {

        List<Theater> theatersNearby = findTheatersNearby();

        if (theatersNearby.isEmpty()) {
            System.out.println("No theaters found in your area.");
            return NavigationFlowState.USER_MENU;
        }

        Theater theater = chooseTheater(theatersNearby, null);
        if (theater == null) {
            return NavigationFlowState.USER_MENU;
        }

        Movie movie = chooseMovieFromTheater(theater);
        if (movie == null) {
            return NavigationFlowState.USER_MENU;
        }

        this.selectedTheater = theater;
        this.selectedMovie = movie;
        return NavigationFlowState.SELECT_SCREEN;
    }
    private NavigationFlowState changeLocationFlow() {
        changeUserLocation();
        return NavigationFlowState.USER_MENU;
    }
    private NavigationFlowState userHistoryFlow() {
        showUserBookingHistory();
        return NavigationFlowState.USER_MENU;
    }
    private NavigationFlowState cancelBookingFlow() {
        cancelBooking();
        return NavigationFlowState.USER_MENU;
    }
    private NavigationFlowState updateProfileFlow() {
        updateUserProfile();
        return NavigationFlowState.USER_MENU;
    }
    private NavigationFlowState selectTheaterFlow() {

        List<Theater> theaters =
                findTheaterPlayingMovie(selectedMovie.getMovieId());

        if (theaters.isEmpty()) {
            System.out.println("No theaters showing this movie.");
            return NavigationFlowState.USER_MENU;
        }

        Theater theater = chooseTheater(theaters, selectedMovie);
        if (theater == null) {
            return NavigationFlowState.USER_MENU;
        }

        this.selectedTheater = theater;
        return NavigationFlowState.SELECT_SCREEN;
    }
    private NavigationFlowState selectScreenFlow() {

        Screen screen = chooseScreen(selectedTheater, selectedMovie.getMovieId());
        if (screen == null) {
            return NavigationFlowState.SELECT_THEATER;
        }

        this.selectedScreen = screen;
        this.selectedShow = findShow(screen, selectedMovie.getMovieId());

        if (selectedShow == null) {
            System.out.println("Show not found.");
            return NavigationFlowState.SELECT_THEATER;
        }

        return NavigationFlowState.SELECT_DATE;
    }
    private NavigationFlowState selectDateFlow() {

        ShowDate date = chooseShowDate(selectedShow);
        if (date == null) {
            return NavigationFlowState.SELECT_SCREEN;
        }

        this.selectedDate = date;
        return NavigationFlowState.SELECT_TIME;
    }
    private NavigationFlowState selectTimeFlow() {

        ShowTime time = chooseShowTime(selectedDate);
        if (time == null) {
            return NavigationFlowState.SELECT_DATE;
        }

        this.selectedTime = time;
        return NavigationFlowState.SELECT_SEATS;
    }
    private NavigationFlowState selectSeatsFlow() {

        Booking booking = selectSeatsAndBooks(
                selectedTheater,
                selectedScreen,
                selectedShow,
                selectedDate,
                selectedTime
        );

        if (booking == null) {
            return NavigationFlowState.SELECT_TIME;
        }

        System.out.println("Booking successful!");
        resetBookingContext();

        return NavigationFlowState.USER_MENU;
    }

    void resetBookingContext() {
        selectedMovie = null;
        selectedTheater = null;
        selectedScreen = null;
        selectedShow = null;
        selectedDate = null;
        selectedTime = null;
    }

    //    Authentication
    //1 -> success, -1 -> failure, 0 -> back
    private int loginUser() {
        ConsoleUtils.printHeader("Login");
        System.out.println("1. Login with Phone Number");
        System.out.println("2. Login with Email id");
        System.out.println("0. Back");

        int loginChoice = ConsoleUtils.readAuthChoice("Enter your option: ");
        switch (loginChoice) {
            case 1 -> {
                // phone login
                String phoneNumber;
                while (true) {
                    phoneNumber = ConsoleUtils.readLineInAuth("Enter your phone number (10 digits) or 0 to go back: ");
                    if (phoneNumber.equals("0")) return 0;

                    if (InputValidator.isValidPhone(phoneNumber)) {
                        System.out.println("Invalid phone format. Must be 10 digits.");
                        continue;
                    }
                    break;
                }

                if (!userService.isPhoneExists(phoneNumber)) {
                    System.out.println("No user found with this phone number");
                    return -1;
                }

                String userPassword = ConsoleUtils.readLineInAuth("Enter your password: ");

                User user = userService.authenticateByPhone(phoneNumber, userPassword);

                if (user != null) {
                    this.currentUser = user;
                    return 1;
                } else {
                    System.out.println("Wrong password.");
                    return -1;
                }
            }

            case 2 -> {
                // email login
                String userEmail;
                while (true) {
                    userEmail = ConsoleUtils.readLineInAuth("Enter your email id or 0 to go back: ");
                    if (userEmail.equals("0")) return 0;

                    if (InputValidator.isValidEmail(userEmail)) {
                        System.out.println("Invalid email format. Try again.");
                        continue;
                    }
                    break;
                }

                if (!userService.isEmailExists(userEmail)) {
                    System.out.println("No user found with this email. Try again or sign up.");
                    return -1;
                }

                String userPassword = ConsoleUtils.readLineInAuth("Enter your password: ");

                User user = userService.authenticateByEmail(userEmail, userPassword);

                if (user == null) {
                    System.out.println("Incorrect password. Try again.");
                    return -1;
                }
                this.currentUser = user;
                return 1;
            }

            case 0 -> {
                return 0;
            }

            default -> {
                System.out.println("You have entered a wrong input. Please try again!");
                return -1;
            }
        }
    }
    private void createUser() {
        ConsoleUtils.printHeader("User Sign up");

        String userName = ConsoleUtils.readLineInAuth("Enter your name: ");

        String userPhoneNumber;
        while (true) {
            userPhoneNumber = ConsoleUtils.readLineInAuth("Enter your Phone number (10 digit): ");
            if (InputValidator.isValidPhone(userPhoneNumber)) {
                System.out.println("Invalid phone number! Must contain exactly 10 digits.");
                continue;
            }

            if (userService.isPhoneExists(userPhoneNumber)) {
                System.out.println("Phone number is already taken");
                continue;
            }
            break;
        }

        int userAge = ConsoleUtils.readAuthChoice("Enter your age: ");

        String userEmail;
        while (true) {
            userEmail = ConsoleUtils.readLineInAuth("Enter your email id: ");
            if (InputValidator.isValidEmail(userEmail)) {
                System.out.println("Invalid email format! Try again.");
                continue;
            }

            if (userService.isEmailExists(userEmail)) {
                System.out.println("This email already taken");
                continue;
            }
            break;
        }

        String userLocation = ConsoleUtils.readLine("Enter your Location: ");

        String userPassword;
        String reUserPassword;
        while (true) {
            userPassword = ConsoleUtils.readLineInAuth("Enter your password: ");

            if (InputValidator.isValidPassword(userPassword)) {
                System.out.println("The password should be minimum 8 characters!");
                continue;
            }

            reUserPassword = ConsoleUtils.readLine("Re-enter your password: ");
            if (userPassword.equals(reUserPassword)) {
                break;
            } else {
                System.out.println("Passwords do not match! Try again.");
            }
        }

        // call service
        userService.createUser(userName, userPhoneNumber, userAge, userEmail, userLocation, userPassword);

        System.out.println("Sign up is successful. Please sign in");
    }
    private void changeUserLocation() {
        System.out.println("Your current location: " + currentUser.getUserLocation());
        String newLocation = ConsoleUtils.readLine("Enter your new location: ");
        currentUser.setUserLocation(newLocation);
        userService.updateUserLocation(currentUser.getUserId(), newLocation);
        System.out.println("Your location has been changed to \"" + newLocation + "\"");
    }
    private void searchMovieByName() {

        ConsoleUtils.printHeader("Search Movie");

        String movieName = ConsoleUtils.readLine("Enter movie name (or 0 to go back): ").trim();

        if (movieName.equals("0")) {
            return; // back to USER_MENU
        }

        if (movieName.isEmpty()) {
            System.out.println("Movie name cannot be empty.");
            return;
        }

        List<Movie> matchedMovies = movieService.searchMovieByName(movieName);

        if (matchedMovies == null || matchedMovies.isEmpty()) {
            System.out.println("No movies found for \"" + movieName + "\"");
            return;
        }

        listMovies(matchedMovies);

        int choice = ConsoleUtils.readInt("Choose a movie number (0 to go back): ");

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > matchedMovies.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Movie movie = matchedMovies.get(choice - 1);

        String proceed = ConsoleUtils.readLine(
                "Proceed with booking \"" + movie.getMovieName() + "\"? (y/n): "
        );

        if (!proceed.equalsIgnoreCase("y")) {
            return;
        }

        this.selectedMovie = movie;
    }

    private List<Theater> findTheaterPlayingMovie(String movieId) {
        String userCity = (currentUser == null || currentUser.getUserLocation() == null) ? "" : currentUser.getUserLocation();
        return theaterService.getTheatersPlayingMovie(movieId, userCity);
    }
    private List<Theater> findTheatersNearby() {
        String userCity = (currentUser == null || currentUser.getUserLocation() == null) ? "" : currentUser.getUserLocation();
        return theaterService.getTheatersInCity(userCity);
    }
    private List<Screen> findScreenWithMovie(Theater theater, String movieId) {
        return showService.findScreensWithMovie(theater, movieId);
    }
    private Show findShow(Screen screen, String movieId) {
        for (Show show : screen.getShows()) {
            if (show.getMovieId() != null && show.getMovieId().equals(movieId)) {
                return show;
            }
        }
        return null;
    }

    //choosing movie,theater, screen, date, time
    private Movie chooseMovieFromTheater(Theater theater) {
        System.out.println("\nMovies showing at " + theater.getTheaterName() + ":");
        List<Movie> availableMovies = new ArrayList<>();
        int index = 1;

        for (String movieId : theater.getMovies()) {
            Movie movie = movieService.getMovieById(movieId);
            if (movie != null) {
                availableMovies.add(movie);
                System.out.printf("%d) %s%n", index++, movie.getDisplayLine());
            }
        }
        if (availableMovies.isEmpty()) {
            System.out.println("No movies available.");
            return null;
        }

        int movieChoice = ConsoleUtils.readInt("Choose movie number (0 to go back or -1 to home): ");
        if (movieChoice == 0) return null;
        if (movieChoice < 1 || movieChoice > availableMovies.size()) {
            System.out.println("Invalid Choice.");
            return null;
        }

        return availableMovies.get(movieChoice - 1);
    }
    private Movie chooseMovie() {
        List<Movie> movieList = listMovies(movieService.getAllMovies());
        if (movieList.isEmpty()) {
            System.out.println("No movies in the database");
            return null;
        }
        int movieChoice = ConsoleUtils.readInt("Enter movie number (0 to go back or -1 to home): ");
        if (movieChoice == 0) return null;
        if (movieChoice < 0 || movieChoice > movieList.size()) {
            System.out.println("Invalid input.");
            return null;
        }
        Movie movie = movieList.get(movieChoice - 1);
        if (movie == null) {
            System.out.println("Movie not found");
            return null;
        }

        showMovieDetails(movie);

        String proceedChoice = ConsoleUtils.readLine("Book a ticket (y/n): ");

        if (!proceedChoice.equalsIgnoreCase("y")) {
            return null;
        }

        return movie;
    }
    private Theater chooseTheater(List<Theater> theaters, Movie movie) {
        if (movie != null) {
            System.out.println("\nTheaters showing \"" + movie.getMovieName() + "\":");
        } else {
            System.out.println("Available Theaters: ");
        }

        while (true) {
            for (int i = 0; i < theaters.size(); i++) {
                Theater theater = theaters.get(i);
                System.out.printf("%d) %s | %s | %s%n", i + 1, theater.getTheaterName(), theater.getTheaterAddress(), theater.getTheaterCity());
            }
            int theaterChoice = ConsoleUtils.readInt("Choose theater number (0 to go back or -1 to home): ");

            if (theaterChoice == 0) return null;

            if (theaterChoice < 1 || theaterChoice > theaters.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            return theaters.get(theaterChoice - 1);
        }
    }
    private Screen chooseScreen(Theater theater, String movieId) {
        List<Screen> screensWithMovie = findScreenWithMovie(theater, movieId);
        if (screensWithMovie.isEmpty()) {
            System.out.println("No screens in selected theater have this movie");
            return null;
        }
        System.out.println("\nScreens Playing this movie: ");
        while (true) {
            for (int i = 0; i < screensWithMovie.size(); i++) {
                Screen screen = screensWithMovie.get(i);
                System.out.printf("%d) %s%n", i + 1, screen.getScreenName());
            }
            int screenChoice = ConsoleUtils.readInt("Choose screen number (0 to go back or -1 to home): ");
            if (screenChoice == 0) return null;

            if (screenChoice < 1 || screenChoice > screensWithMovie.size()) {
                System.out.println("Invalid choice.");
                continue;
            }
            return screensWithMovie.get(screenChoice - 1);
        }
    }
    private ShowDate chooseShowDate(Show chosenShow) {
        List<ShowDate> availableDates = showService.getAvailableDates(chosenShow);
        if (availableDates.isEmpty()) {
            System.out.println("No show dates available.");
            return null;
        }
        while (true) {
            System.out.println("\n Available Dates: ");
            for (int i = 0; i < availableDates.size(); i++) {
                System.out.printf("%d) %s \n", i + 1, availableDates.get(i).getShowDateDate());
            }
            int dateChoice = ConsoleUtils.readInt("Choose date number (0 to go back or -1 to home): ");
            if (dateChoice == 0) return null;
            if (dateChoice < 1 || dateChoice > availableDates.size()) {
                System.out.println("Invalid choice. Try again");
                continue;
            }
            return availableDates.get(dateChoice - 1);
        }
    }
    private ShowTime chooseShowTime(ShowDate chosenDate) {
        List<ShowTime> showTimes = showService.getAvailableShowTimes(chosenDate);

        while (true) {
            for (int i = 0; i < showTimes.size(); i++) {
                ShowTime showTime = showTimes.get(i);
                System.out.printf("%d) %s  | Premium: %.2f  Classic: %.2f  Economy: %.2f\n", i + 1, showTime.getStartTime(), showTime.getPriceOfPremiumSeat(), showTime.getPriceOfClassicSeat(), showTime.getPriceOfEconomySeat());

            }
            int showTimeChoice = ConsoleUtils.readInt("Choose show time number(0 to go back or -1 to home): ");

            if (showTimeChoice == 0) return null;

            if (showTimeChoice < 1 || showTimeChoice > showTimes.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            return showTimes.get(showTimeChoice - 1);
        }
    }

    // showing and seat selection
    private Booking selectSeatsAndBooks(Theater chosenTheater, Screen chosenScreen, Show chosenShow, ShowDate chosenDate, ShowTime chosenShowTime) {
        if (chosenTheater == null || chosenScreen == null || chosenShow == null || chosenDate == null || chosenShowTime == null) {
            System.out.println("Required data missing for booking.");
            return null;
        }

        //display Seats
        displaySeatsAndBooks(chosenScreen, chosenShowTime);

        System.out.printf("\nPrices -> Premium: %.2f  Classic: %.2f  Economy: %.2f%n", chosenShowTime.getPriceOfPremiumSeat(), chosenShowTime.getPriceOfClassicSeat(), chosenShowTime.getPriceOfEconomySeat());

        Set<String> requestedSeats;
        while (true) {
            String seatLine = ConsoleUtils.readLine("\nEnter seat labels to book (comma separated, e.g. A1,A2) or 0 to cancel: ");
            if (seatLine.equals("0")) {
                System.out.println("Booking cancelled");
                return null; // back to time selection
            }

            requestedSeats = parseSeatInput(seatLine);
            if (requestedSeats.isEmpty()) {
                System.out.println("No valid seats entered. Please enter at least one valid seat (like A1,B3).");
                continue;
            }

            ValidateSeatResult validateSeatResult = bookingService.validateSeats(requestedSeats, chosenScreen, chosenShowTime);

            if (validateSeatResult.isValid()) {
                System.out.println(validateSeatResult.getMessage());
                continue;
            }
            break;
        }

        // create snapshots list and total via BookingService
        List<SeatSnapShot> seatSnapShots = bookingService.createSeatSnapshots(requestedSeats, chosenScreen, chosenShowTime);
        double total = bookingService.calculateTotalPrice(requestedSeats, chosenScreen, chosenShowTime);

        // display booking summary
        displayBookingSummary(seatSnapShots, total);

        //confirm  booking
        String conf = ConsoleUtils.readLine("Confirm booking? (y/n): ");

        if (!conf.equalsIgnoreCase("y")) {
            System.out.println("Booking cancelled.");
            return null;
        }

        // Booking via service
        Booking booking = bookingService.createBooking(currentUser.getUserId(), chosenTheater.getTheaterId(), chosenScreen.getScreenId(), chosenShow.getShowId(), chosenDate.getShowDateId(), chosenShowTime.getShowTimeId(), requestedSeats);

        if (booking == null) {
            System.out.println("Something went wrong while creating booking.");
            return null;
        }

        System.out.println("Booking successful! You can view booking in booking history");
        return booking;
    }
    private void displaySeatsAndBooks(Screen chosenScreen, ShowTime chosenShowTime) {
        System.out.println("\nSeat map (X = booked, O = available)");
        System.out.println("Legend: (P)=Premium (C)=Classic (E)=Economy");

        TreeMap<String, List<String>> rows = new TreeMap<>();
        List<String> labels = new ArrayList<>(chosenScreen.getSeatTypeMap().keySet());
        labels.sort(Comparator.naturalOrder());

        for (String label : labels) {
            String row = label.replaceAll("\\d+$", "");
            rows.computeIfAbsent(row, _ -> new ArrayList<>()).add(label);
        }

        for (Map.Entry<String, List<String>> entry : rows.entrySet()) {
            System.out.println(entry.getKey() + " ");
            for (String label : entry.getValue()) {

                SeatTypes seatType = chosenScreen.getSeatTypeMap().get(label);

                boolean isBooked = (chosenShowTime.getSeats().get(label) != null);
                String typeChar = seatType == SeatTypes.PREMIUM ? "P" : (seatType == SeatTypes.CLASSIC ? "C" : "E");
                String mark = isBooked ? "X" : "O";
                System.out.printf("%s(%s)%s  ", label, typeChar, mark);
            }
            System.out.println();
        }
    }

    // user history
    private void showUserBookingHistory() {
        System.out.println("---------- Booking History ----------\n\n");
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }
        List<Booking> bookings = bookingService.getUserBookings(currentUser.getUserId());
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No booking done. Please book a ticket to view history");
            return;
        }
        for (Booking booking : bookings) {
            printBooking(booking);
        }
    }

    //cancel
    private void cancelBooking() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        List<Booking> cancellable = bookingService.getCancellableBookings(currentUser.getUserId());
        if (cancellable == null || cancellable.isEmpty()) {
            System.out.println("No ticket have been booked to cancel");
            return;
        }

        System.out.println("\nCancellable bookings:\n");
        for (int i = 0; i < cancellable.size(); i++) {
            System.out.printf("%d) ", i + 1);
            printBooking(cancellable.get(i));
        }

        int bookingChoice;
        Booking chosenBooking;
        while (true) {
            bookingChoice = ConsoleUtils.readInt("Select your booking (0 to go back): ");
            if (bookingChoice == 0) {
                return;
            }
            if (bookingChoice < 0 || bookingChoice > cancellable.size()) {
                System.out.println("Invalid choice.");
                continue;
            }
            chosenBooking = cancellable.get(bookingChoice - 1);
            while (true) {
                String seatLine = ConsoleUtils.readLine("Enter your (comma separated, e.g. A1,A2) or 0 to cancel: ");
                if (seatLine.equalsIgnoreCase("0")) {
                    return;
                }
                Set<String> requestedSeats = parseSeatInput(seatLine);

                ValidateSeatResult validateSeatResult = bookingService.validateSeatsForCancellation(chosenBooking, requestedSeats);
                if (validateSeatResult.isValid()) {
                    System.out.println(validateSeatResult.getMessage());
                    continue;
                }
                while (true) {
                    String confirmChoice = ConsoleUtils.readLine("Confirm cancellation (y/n): ");
                    if (confirmChoice.equalsIgnoreCase("y") || confirmChoice.equalsIgnoreCase("yes")) {
                        break;
                    } else if (confirmChoice.equalsIgnoreCase("n") || confirmChoice.equalsIgnoreCase("no")) {
                        System.out.println("Booking canceled.");
                        return;
                    } else {
                        System.out.println("Invalid choice. Please enter a valid choice.");
                    }
                }
                Booking updated = bookingService.cancelSeats(chosenBooking.getBookingId(), requestedSeats);
                if (updated != null) {
                    printBooking(updated);
                }
                return;
            }
        }
    }

    //update user
    private void updateUserProfile() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        while (true) {
            ConsoleUtils.printHeader("Update Profile");
            System.out.println("Current details:");
            System.out.println("1) Name     : " + currentUser.getUserName());
            System.out.println("2) Phone    : " + currentUser.getUserPhoneNumber());
            System.out.println("3) Email    : " + currentUser.getUserEmail());
            System.out.println("4) Location : " + currentUser.getUserLocation());
            System.out.println("5) Change Password");
            System.out.println("0) Back");

            int choice = ConsoleUtils.readInt("Choose what to update: ");

            switch (choice) {
                case 1 -> { // Update name
                    String newName = ConsoleUtils.readLine("Enter new name (or 0 to cancel): ");
                    if (newName.equals("0")) break;
                    if (newName.isEmpty()) {
                        System.out.println("Name cannot be empty.");
                        break;
                    }
                    if (userService.updateUserName(currentUser.getUserId(), newName)) {
                        currentUser.setUserName(newName);
                        System.out.println("Name updated successfully.");
                    } else {
                        System.out.println("Failed to update name.");
                    }
                }

                case 2 -> { // Update phone
                    while (true) {
                        String newPhone = ConsoleUtils.readLine("Enter new phone (10 digits) or 0 to cancel: ");
                        if (newPhone.equals("0")) break;

                        if (InputValidator.isValidPhone(newPhone)) {
                            System.out.println("Invalid phone number. Must contain exactly 10 digits.");
                            continue;
                        }

                        boolean success = userService.updateUserPhone(currentUser.getUserId(), newPhone);
                        if (!success) {
                            System.out.println("Phone number already registered. Please use another number.");
                            continue;
                        }

                        currentUser.setUserPhoneNumber(newPhone);
                        System.out.println("Phone number updated successfully.");
                        break;
                    }
                }

                case 3 -> { // Update email
                    while (true) {
                        String newEmail = ConsoleUtils.readLine("Enter new email (or 0 to cancel): ");
                        if (newEmail.equals("0")) break;

                        if (InputValidator.isValidEmail(newEmail)) {
                            System.out.println("Invalid email format. Try again.");
                            continue;
                        }

                        boolean success = userService.updateUserEmail(currentUser.getUserId(), newEmail);
                        if (!success) {
                            System.out.println("Email already registered. Please use another email.");
                            continue;
                        }

                        currentUser.setUserEmail(newEmail);
                        System.out.println("Email updated successfully.");
                        break;
                    }
                }

                case 4 -> // Update location
                        changeUserLocation();

                case 5 -> { // Change password
                    while (true) {
                        String oldPassword = ConsoleUtils.readLine("Enter your current password (or 0 to cancel): ");
                        if (oldPassword.equals("0")) break;

                        if (!oldPassword.equals(currentUser.getUserPasswordHash())) {
                            System.out.println("Incorrect old password. Try again.");
                            continue;
                        }

                        String newPassword = ConsoleUtils.readLine("Enter new password (min 8 characters): ");
                        if (InputValidator.isValidPassword(newPassword)) {
                            System.out.println("Password must be at least 8 characters long.");
                            continue;
                        }

                        String confirmPassword = ConsoleUtils.readLine("Re-enter new password: ");
                        if (!newPassword.equals(confirmPassword)) {
                            System.out.println("Passwords do not match. Try again.");
                            continue;
                        }

                        boolean updated = userService.updatePassword(currentUser.getUserId(), oldPassword, newPassword);
                        if (updated) {
                            currentUser.setUserPasswordHash(newPassword);
                            System.out.println("Password updated successfully.");
                        } else {
                            System.out.println("Failed to update password.");
                        }
                        break;
                    }
                }

                case 0 -> {
                    return; // back to user menu
                }

                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    //helper
    private List<Movie> listMovies(List<Movie> movieList) {
        if (movieList == null || movieList.isEmpty()) {
            System.out.println("No movies available right now.");
            return new ArrayList<>();
        }
        System.out.println("\nAvailable Movies:");
        System.out.println("-------------------------------------");
        for (int i = 0; i < movieList.size(); i++) {
            Movie m = movieList.get(i);
            System.out.printf("%d) %s | %s | %s | %s %n", i + 1, m.getMovieName(), m.getMovieGenre(), m.getMovieDuration(), m.getMovieLanguage());
        }
        System.out.println("-------------------------------------");
        return movieList;
    }
    private void showMovieDetails(Movie movie) {
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Movie name       : " + movie.getMovieName());
        System.out.println("Certificate      : " + movie.getMovieEligibility());
        System.out.println("Description      : " + movie.getMovieDescription());
        System.out.println("Genre            : " + movie.getMovieGenre());
        System.out.println("Language         : " + movie.getMovieLanguage());
        System.out.println("Duration         : " + movie.getMovieDuration());
        System.out.println("Movie Type       : " + movie.getMovieType());

        // Extra info only for 3D movies
        if (movie instanceof ThreeDMovie) {
            ThreeDMovie threeDMovie = (ThreeDMovie) movie;
            System.out.println("3D Glass Info    : " + threeDMovie.getGlassInfo());
        }

        System.out.println("-------------------------------------------\n\n");
    }
    private void displayBookingSummary(List<SeatSnapShot> seatSnapShots, double total) {
        System.out.println("\nBooking Summary");
        for (SeatSnapShot seatSnapShot : seatSnapShots) {
            System.out.printf(" %s | %s | %.2f%n", seatSnapShot.getSeatLabel(), seatSnapShot.getSeatType(), seatSnapShot.getSeatPrice());
        }

        System.out.printf("\nTotal: %.2f\n", total);
    }
    private Set<String> parseSeatInput(String seatLine) {
        String[] seatArray = seatLine.split(",");
        Set<String> requestedSeats = new HashSet<>();
        for (String seat : seatArray) {
            String trimmed = seat.trim().toUpperCase();
            if (!trimmed.isEmpty()) {
                if (InputValidator.isValidSeatLabel(trimmed)) {
                    requestedSeats.add(trimmed);
                } else {
                    System.out.println("Warning: '" + trimmed + "' is not a valid seat label format.");
                }
            }
        }

        return requestedSeats;
    }
    private void printBooking(Booking booking) {
        Theater theater = theaterService.getTheaterById(booking.getTheaterId());
        if (theater != null) {
            System.out.println("Theater         : " + theater.getTheaterName());
        } else {
            System.out.println("Theater ID        : " + booking.getTheaterId() + " (not found)");
        }

        Show foundShow = null;
        Screen foundScreen = null;
        if (theater != null) {
            foundScreen = theaterService.getScreenById(theater.getTheaterId(), booking.getScreenId());
            if (foundScreen != null) {
                for (Show s : foundScreen.getShows()) {
                    if (s != null && s.getShowId().equalsIgnoreCase(booking.getShowId())) {
                        foundShow = s;
                        break;
                    }
                }
            }
        }

        if (foundScreen != null) {
            System.out.println("Screen            : " + foundScreen.getScreenName());
        } else {
            System.out.println("Screen            : (not found)");
        }

        if (foundShow != null) {
            String movieName = null;
            if (foundShow.getMovieId() != null) {
                Movie movie = movieService.getMovieById(foundShow.getMovieId());
                movieName = (movie != null ? movie.getMovieName() : null);
            }

            System.out.println("Movie             : " + (movieName != null ? movieName : "ID: " + foundShow.getMovieId()));
        } else {
            System.out.println("Show ID           : " + booking.getShowId() + " (not found)");
        }

        ShowDate showDate = null;
        ShowTime showTime = null;
        if (foundShow != null && foundShow.getDates() != null) {
            showDate = foundShow.getDates().get(booking.getShowDateId());
            if (showDate != null && showDate.getShowTimes() != null) {
                showTime = showDate.getShowTimes().get(booking.getShowTimeId());
            }
        }

        System.out.println("Show Date         : " + (showDate != null ? showDate.getShowDateDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : booking.getShowDateId() + " (not found)"));
        System.out.println("Show Time         : " + (showTime != null ? showTime.getStartTime().format(DateTimeFormatter.ofPattern("hh : mm a")) : booking.getShowTimeId() + " (not found)"));

        if (booking.getBookingSeat() != null && !booking.getBookingSeat().isEmpty()) {
            StringBuilder seats = new StringBuilder();
            for (SeatSnapShot seat : booking.getBookingSeat()) {
                seats.append(seat.getSeatLabel()).append(" ");
            }
            System.out.println("Booked seats      : " + seats.toString().trim());
        }

        if (booking.getCancelSeat() != null && !booking.getCancelSeat().isEmpty()) {
            StringBuilder seats = new StringBuilder();
            for (SeatSnapShot seat : booking.getCancelSeat()) {
                seats.append(seat.getSeatLabel()).append(" ");
            }
            System.out.println("Cancelled seats   : " + seats.toString().trim());
        }

        System.out.println("Booking Status    : " + (booking.getBookingStatus() != null ? booking.getBookingStatus() : "N/A"));
        if (booking.getBookingPrice() != 0.0) {
            System.out.printf("Total Price       : ₹%.2f%n", booking.getBookingPrice());
        }
        System.out.println("Booked At         : " + (booking.getBookingCreatedAt() != null ? booking.getBookingCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy | hh : mm a")) : "N/A"));

        if (booking.getBookingCancelledAt() != null) {
            System.out.println("Cancelled At      : " + booking.getBookingCancelledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy | hh : mm a")));
        }
        System.out.println("-----------------------------");
    }

    // seeding sample data
    private void addSampleData() {

        System.out.println("Loading sample data...");

        /* ================= USERS ================= */
        User u1 = new User(UUID.randomUUID().toString(), "Max", "9786337697",
                25, "max@zoho.com", "chennai", "max12345", new ArrayList<>());

        User u2 = new User(UUID.randomUUID().toString(), "Holly", "0987654321",
                18, "holly@zoho.com", "chennai", "holly1234", new ArrayList<>());

        DataStore.users.put(u1.getUserId(), u1);
        DataStore.users.put(u2.getUserId(), u2);

        /* ================= MOVIES ================= */
        Movie m1 = new ThreeDMovie(
                "M1", "Inception", MovieCertificate.S,
                "A thief enters dreams to steal secrets.",
                Genre.SCI_FI, Languages.ENGLISH, "2h 28m",
                50, true, true
        );

        Movie m2 = new RegularMovie(
                "M2", "Vikram", MovieCertificate.UA,
                "A special agent on a dangerous mission.",
                Genre.ACTION, Languages.TAMIL, "2h 55m"
        );

        DataStore.movies.put(m1.getMovieId(), m1);
        DataStore.movies.put(m2.getMovieId(), m2);

        /* ================= SEATS ================= */
        HashMap<String, SeatTypes> seatTypeMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            seatTypeMap.put("A" + i, SeatTypes.PREMIUM);
            seatTypeMap.put("B" + i, SeatTypes.CLASSIC);
            seatTypeMap.put("C" + i, SeatTypes.ECONOMY);
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        HashSet<String> theaterMovies = new HashSet<>();
        theaterMovies.add("M1");

        /* ================= COMMON SHOW DATA ================= */
        HashMap<String, ShowTime> day1Times = new HashMap<>();
        day1Times.put("ST1", new ShowTime("ST1", LocalTime.of(10, 0), 250, 200, 150, new HashMap<>()));
        day1Times.put("ST2", new ShowTime("ST2", LocalTime.of(18, 30), 300, 250, 180, new HashMap<>()));

        HashMap<String, ShowTime> day2Times = new HashMap<>();
        day2Times.put("ST1", new ShowTime("ST1", LocalTime.of(10, 0), 250, 200, 150, new HashMap<>()));
        day2Times.put("ST2", new ShowTime("ST2", LocalTime.of(18, 30), 300, 250, 180, new HashMap<>()));

        HashMap<String, ShowDate> showDates = new HashMap<>();
        showDates.put("D1", new ShowDate("D1", today, day1Times));
        showDates.put("D2", new ShowDate("D2", tomorrow, day2Times));

        /* ================= THEATERS ================= */

        // ---------- THEATER 1 ----------
        Show sh1 = new Show("SH1_S1", "M1", showDates);
        Show sh2 = new Show("SH1_S2", "M1", showDates);

        Screen t1s1 = new Screen("S1", "Screen 1", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh1)));
        Screen t1s2 = new Screen("S2", "Screen 2", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh2)));

        HashMap<String, Screen> t1Screens = new HashMap<>();
        t1Screens.put(t1s1.getScreenId(), t1s1);
        t1Screens.put(t1s2.getScreenId(), t1s2);

        Theater t1 = new Theater("T1", "PVR Cinemas", "Anna Nagar", "chennai",
                t1Screens, theaterMovies, new ArrayList<>());

        // ---------- THEATER 2 ----------
        Screen t2s1 = new Screen("S1", "Screen 1", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh1)));
        Screen t2s2 = new Screen("S2", "Screen 2", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh2)));

        HashMap<String, Screen> t2Screens = new HashMap<>();
        t2Screens.put(t2s1.getScreenId(), t2s1);
        t2Screens.put(t2s2.getScreenId(), t2s2);

        Theater t2 = new Theater("T2", "Rohini Cinemas", "Koyambedu", "chennai",
                t2Screens, theaterMovies, new ArrayList<>());

        // ---------- THEATER 3 ----------
        Screen t3s1 = new Screen("S1", "Screen 1", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh1)));
        Screen t3s2 = new Screen("S2", "Screen 2", 5, 5, 5, 5, seatTypeMap, new ArrayList<>(List.of(sh2)));

        HashMap<String, Screen> t3Screens = new HashMap<>();
        t3Screens.put(t3s1.getScreenId(), t3s1);
        t3Screens.put(t3s2.getScreenId(), t3s2);

        Theater t3 = new Theater("T3", "Thinnapa Cinemas", "Karur", "karur",
                t3Screens, theaterMovies, new ArrayList<>());

        DataStore.theaters.put(t1.getTheaterId(), t1);
        DataStore.theaters.put(t2.getTheaterId(), t2);
        DataStore.theaters.put(t3.getTheaterId(), t3);

        System.out.println("Sample Data loaded");
    }
}
