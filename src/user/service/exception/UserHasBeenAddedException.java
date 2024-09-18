package user.service.exception;

public class UserHasBeenAddedException extends RuntimeException {
    public UserHasBeenAddedException() {
        super("Account with your name and email input is still active");
    }
}