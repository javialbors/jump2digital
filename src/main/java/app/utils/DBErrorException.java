package app.utils;

public class DBErrorException extends Exception {
    public DBErrorException() {
        super("Database error");
    }

    public DBErrorException(String message) {
        super(message);
    }
}
