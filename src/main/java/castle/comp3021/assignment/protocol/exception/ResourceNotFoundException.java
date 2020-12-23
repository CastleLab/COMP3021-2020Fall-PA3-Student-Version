package castle.comp3021.assignment.protocol.exception;

import org.jetbrains.annotations.NonNls;

/**
 * Exception indicating that a required resource is not found on the filesystem.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(@NonNls final String message) {
        super(message);
    }
}
