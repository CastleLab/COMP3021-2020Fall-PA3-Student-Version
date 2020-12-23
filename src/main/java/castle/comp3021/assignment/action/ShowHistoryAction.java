package castle.comp3021.assignment.action;

import castle.comp3021.assignment.protocol.Action;
import castle.comp3021.assignment.protocol.Game;

public class ShowHistoryAction extends Action {
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
