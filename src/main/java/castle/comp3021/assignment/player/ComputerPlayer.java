package castle.comp3021.assignment.player;

import castle.comp3021.assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

/**
 * A computer player that makes a move randomly.
 */
public class ComputerPlayer extends Player {
    public ComputerPlayer(String name, Color color, Strategy strategy) {
        super(name, color, strategy);
    }

    public ComputerPlayer(String name, Color color) {
        super(name, color);
    }

    public ComputerPlayer(String name) {
        this(name, Color.BLUE);
    }

    /**
     * Select a move as next move given {@link Game} object game and an array of {@link Move} availableMoves
     * - If {@link this#strategy} is {@link Strategy#RANDOM}: randomly select one move
     * - If {@link this#strategy} is {@link Strategy#SMART}: come up with a smart strategy to select next move
     * <p>
     * Hint:
     * - the smart strategy should beat random strategy <strong>on average</strong>.
     * e.g., smart strategy should win more than half times over total number of times (win 6 times among 10)
     * - the smart strategy should not be hard coded.
     * </p>
     *
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return a next move
     */
    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        //TODO
        return new MoveByStrategy(game, availableMoves, strategy).getNextMove();
    }
}

