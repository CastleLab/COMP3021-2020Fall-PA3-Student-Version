package castle.comp3021.assignment.action;

import castle.comp3021.assignment.protocol.Action;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.exception.ActionException;
import castle.comp3021.assignment.protocol.exception.UndoException;

public class UndoAction extends Action {
    public UndoAction(Game game, String[] args) {
        super(game, args);
    }

    /**
     * Perform the undo action
     * Hint:
     * - Using {@link Game#undo()}
     * - If fails, handle {@link UndoException} and return the error message
     * - If successes, return null
     * Catch {@link UndoException} and return error message if undo action fails
     *
     * @return null if undo action successes, or return error message if fails.
     */
    @Override
    public void perform() throws ActionException {
        //TODO
        try {
            game.undo();
        } catch (UndoException e) {
            throw new ActionException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Action[undo]";
    }
}