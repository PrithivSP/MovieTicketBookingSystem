package exceptions;

public class ExitApp extends RuntimeException {
    public ExitApp() {
        super("Exiting app....");
    }
}
