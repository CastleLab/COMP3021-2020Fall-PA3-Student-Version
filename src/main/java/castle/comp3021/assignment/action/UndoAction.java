package castle.comp3021.assignment.action;

import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.protocol.Action;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.exception.ActionException;
import castle.comp3021.assignment.protocol.exception.UndoException;

/**
 * The action to undo moves.
 * <p>
 * Note that there is a limit of undo, users cannot undo for more than a limited number of moves.
 */
public class UndoAction extends Action {
    /**
     * @param game the current {@link Game} object
     * @param args the arguments input by users in the console
     */
    public UndoAction(Game game, String[] args) {
        super(game, args);
    }

    /**
     * Perform the undo action
     * Hint:
     * - Call {@link Game#undo()}
     * - If fails, handle {@link UndoException} and return the error message
     * Catch {@link UndoException} and return error message if undo action fails
     */
    @Override
    public void perform() throws ActionException {
        //TODO
    }

    @Override
    public String toString() {
        return "Action[undo]";
    }
}