package castle.comp3021.assignment.protocol.exception;

import org.jetbrains.annotations.NonNls;

public class ActionException extends Exception {
    public ActionException(@NonNls final String message) {
        super(message);
    }

}
