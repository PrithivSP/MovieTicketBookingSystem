package services;

import models.User;
import repository.DataStore;
import services.interfaces.UserServiceI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService implements UserServiceI {

    public User createUser(String name,
                           String phone,
                           int age,
                           String email,
                           String location,
                           String password) {

        String userId = UUID.randomUUID().toString();

        User user = new User(
                userId,
                name,
                phone,
                age,
                email,
                location,
                password, new ArrayList<>()
        );

        DataStore.users.put(userId, user);
        return user;
    }

    public User authenticateByPhone(String phone, String password) {
        for (User u : DataStore.users.values()) {
            if (u.getUserPhoneNumber() != null &&
                    u.getUserPhoneNumber().equals(phone) &&
                    u.getUserPasswordHash() != null &&
                    u.getUserPasswordHash().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User authenticateByEmail(String email, String password) {
        for (User u : DataStore.users.values()) {
            if (u.getUserEmail() != null &&
                    u.getUserEmail().equalsIgnoreCase(email) &&
                    u.getUserPasswordHash() != null &&
                    u.getUserPasswordHash().equals(password)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public boolean updateUserName(String userId, String newName) {
        User user = DataStore.users.get(userId);
        if (user == null) return false;

        user.setUserName(newName);
        return true;
    }

    @Override
    public boolean updateUserPhone(String userId, String newPhone) {
        // Check if another user already has this phone number
        for (User existingUser : DataStore.users.values()) {
            if (existingUser.getUserPhoneNumber().equals(newPhone)) {
                return false; // phone number already taken
            }
        }

        User user = DataStore.users.get(userId);
        if (user == null) return false;

        user.setUserPhoneNumber(newPhone);
        return true;
    }

    @Override
    public boolean updateUserEmail(String userId, String newEmail) {
        // Check if another user already has this email
        for (User existingUser : DataStore.users.values()) {
            if (existingUser.getUserEmail().equalsIgnoreCase(newEmail)) {
                return false; // email already taken
            }
        }

        User user = DataStore.users.get(userId);
        if (user == null) return false;

        user.setUserEmail(newEmail);
        return true;
    }


    public boolean isPhoneExists(String phone) {
        for (User u : DataStore.users.values()) {
            if (u.getUserPhoneNumber() != null &&
                    u.getUserPhoneNumber().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        for (User u : DataStore.users.values()) {
            if (u.getUserEmail() != null &&
                    u.getUserEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    public boolean updateUserLocation(String userId, String newLocation) {
        User user = DataStore.users.get(userId);
        if (user == null) return false;
        user.setUserLocation(newLocation);
        return true;
    }

    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        return false;
    }

}
