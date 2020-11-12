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

    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        return new MakeMoveByStrategy(game, availableMoves, this.strategy).getNextMove();
    }
}

