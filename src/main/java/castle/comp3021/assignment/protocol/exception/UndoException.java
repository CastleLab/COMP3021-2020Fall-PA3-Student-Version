package castle.comp3021.assignment.protocol.exception;

import org.jetbrains.annotations.NonNls;

public class UndoException extends Exception {
    public UndoException(@NonNls final String message) {
        super(message);
    }
}
