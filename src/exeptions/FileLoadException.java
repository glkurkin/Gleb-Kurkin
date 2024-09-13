package exeptions;

public class FileLoadException extends RuntimeException {
    public FileLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
