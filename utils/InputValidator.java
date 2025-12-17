package utils;

public class InputValidator {

    public static boolean isValidPhone(String phone) {
        return phone == null || !phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {
        return email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPassword(String password) {
        return password == null || password.length() < 8;
    }

    public static boolean isValidSeatLabel(String label) {
        return label != null && label.matches("^[A-Z]+\\d+$");
    }
}
