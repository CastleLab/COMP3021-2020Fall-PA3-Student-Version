package castle.comp3021.assignment.action;

import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.Action;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.exception.ActionException;

public class TerminatePieceAction extends Action {
    public TerminatePieceAction(Game game, String[] args) {
        super(game, args);
    }

    /**
     * Perform the action
     * Hint: Considering the following situations:
     * - If piece does not exist at the given place:
     * error message: "piece does not exist at " + {@link this#place}
     * - If the piece does not belong to computer player:
     * error message: {@link this#place} + "does not belong to computer player, thus can not be stopped"
     * - If the action can be performed to {@link Piece} at the given {@link Place}
     * error message: null
     * using {@link Piece#terminate()} and {@link Thread#interrupt()}
     * - The piece thread can be get by
     * {@link castle.comp3021.assignment.protocol.Configuration#getPieceThread(Piece)}
     *
     * @return string of error message if the action fails to perform, or null if successes.
     */
    @Override
    public void perform() throws ActionException {
        //TODO
        if (this.args.length < 1) {
            throw new ActionException("No piece provided");
        }
        var place = ConsolePlayer.parsePlace(this.args[0]);
        if (place == null) {
            throw new ActionException("Invalid piece at place " + this.args[0]);
        }
        var piece = this.game.getPiece(place);
        if (piece == null) {
            throw new ActionException("piece does not exist at " + place.toString());
        }
        if (!(piece.getPlayer() instanceof ComputerPlayer)) {
            throw new ActionException("piece at " + place.toString() + " does not belong to computer player, thus can not be stopped");
        }
        var thread = game.getConfiguration().getPieceThread(piece);
        piece.terminate();
        thread.interrupt();
    }

    @Override
    public String toString() {
        return "Action[Terminate piece]";
    }
}