package castle.comp3021.assignment.action;

import castle.comp3021.assignment.protocol.Action;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.exception.UndoException;

/**
 * The action to show move history.
 */
public class ShowHistoryAction extends Action {
    /**
     * @param game the current {@link Game} object
     * @param args the arguments input by users in the console
     */
    public ShowHistoryAction(Game game, String[] args) {
        super(game, args);
    }

    @Override
    public void perform() {
        game.showHistoryMove();
    }

    @Override
    public String toString() {
        return "Action[history]";
    }
}
