package app.utils;

public class DataErrorException extends Exception {
    private int status;
    private Object object;

    public DataErrorException(int status) {
        super(status == 404 ? "Data not found" : "");

        this.status = status;
    }

    public DataErrorException(int status, String message) {
        super(message);
        this.status = status;
    }

    public DataErrorException(int status, String message, Object object) {
        super(message);
        this.status = status;
        this.object = object;
    }

    public int getStatus() {
        return status;
    }

    public Object getObject() {
        return object;
    }
}
