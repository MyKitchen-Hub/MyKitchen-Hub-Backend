package femcoders25.mykitchen_hub.cloudinary.exception;

public class InvalidImageFileException extends RuntimeException {

    public InvalidImageFileException(String message) {
        super(message);
    }

    public InvalidImageFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
