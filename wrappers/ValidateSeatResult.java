package wrappers;

public final class ValidateSeatResult{
    private final boolean valid;
    private final String message;

    public ValidateSeatResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return !valid;
    }
    public String getMessage() {
        return message;
    }
}
