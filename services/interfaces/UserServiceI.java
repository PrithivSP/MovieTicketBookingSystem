package services.interfaces;
import models.User;
import java.util.List;

public interface UserServiceI {

    User createUser(String name, String phone, int age, String email, String location, String password);

    User authenticateByPhone(String phone, String password);

    User authenticateByEmail(String email, String password);

    boolean updateUserName(String userId, String newName);

    boolean updateUserPhone(String userId, String newPhone);

    boolean updateUserEmail(String userId, String newEmail);

    boolean updateUserLocation(String userId, String newLocation);

    boolean updatePassword(String userId, String oldPassword, String newPassword);

//    public User getUserById(String userId) {
//        return DataStore.users.get(userId);
//    }

    boolean isPhoneExists(String phone);

    boolean isEmailExists(String email);

    List<User> getAllUsers();
}
