package exceptions;

public class ExitToUserMenu extends RuntimeException {
    public ExitToUserMenu() {
        super("Exit by user");
    }
}
