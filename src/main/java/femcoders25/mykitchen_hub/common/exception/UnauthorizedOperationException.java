package femcoders25.mykitchen_hub.common.exception;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String operation, String resource) {
        super(String.format("You are not authorized to %s this %s", operation, resource));
    }
}
