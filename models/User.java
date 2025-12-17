package models;

import java.util.List;

public class User {
    private String userId;
    private String userName;
    private String userPhoneNumber;
    private int userAge;
    private String userEmail;
    private String userLocation;
    private String userPasswordHash;
    private List<Booking> userBookingHistory;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public int getUserAge() {
        return userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserPasswordHash() {
        return userPasswordHash;
    }

    public List<Booking> getUserBookingHistory() {
        return userBookingHistory;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public void addUserBookingHistory(Booking booking) {
        userBookingHistory.add(booking);
    }

    public User(String userId, String userName, String userPhoneNumber, int userAge, String userEmail, String userLocation, String userPasswordHash, List<Booking> userBookingHistory) {
        this.userId = userId;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userLocation = userLocation.toLowerCase();
        this.userPasswordHash = userPasswordHash;
        this.userBookingHistory = userBookingHistory;
    }
}
