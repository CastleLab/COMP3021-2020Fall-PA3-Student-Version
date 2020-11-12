package castle.comp3021.assignment.protocol.exception;

import org.jetbrains.annotations.NonNls;

public class InvalidGameException extends RuntimeException {
    public InvalidGameException(@NonNls final String message) {
        super(message);
    }

    public InvalidGameException(Throwable cause) {
        super(cause);
    }
}