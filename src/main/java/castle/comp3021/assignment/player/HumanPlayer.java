package castle.comp3021.assignment.player;

import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.Strategy;
import org.jetbrains.annotations.NotNull;

public abstract class HumanPlayer extends Player {
    public HumanPlayer(String name, Color color, Strategy strategy) {
        super(name, color, strategy);
    }

    public HumanPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public abstract @NotNull Move nextMove(Game game, Move[] availableMoves);
}
