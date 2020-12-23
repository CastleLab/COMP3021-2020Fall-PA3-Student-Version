package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.piece.Knight;

import java.util.*;

public class MoveByBehavior {
    private final Behavior behavior;
    private final Game game;
    private final Move[] availableMoves;

    public MoveByBehavior(Game game, Move[] availableMoves, Behavior behavior) {
        this.game = game;
        this.availableMoves = availableMoves;
        this.behavior = behavior;
    }

    public Move getNextMove() {
        int nextIndex;
        if (this.behavior == Behavior.RANDOM) {
            nextIndex = new Random().nextInt(availableMoves.length);
        } else {
            List<Integer> weights = new ArrayList<>(this.availableMoves.length);

            for (Move availableMove : this.availableMoves) {
                weights.add(assignWeight(this.game, availableMove, this.behavior));
            }

            nextIndex = weights.indexOf(Collections.max(weights));
        }
        return availableMoves[nextIndex];
    }

    private int assignWeight(Game game, Move move, Behavior behavior) {
        switch (behavior) {
            case RANDOM -> {
                return 1;
            }
            case GREEDY -> {
                //if a piece gets closer to central place
                var sourceToCenter = distanceFromCentral(move.getSource(), game.getCentralPlace());
                var targetToCenter = distanceFromCentral(move.getDestination(), game.getCentralPlace());

                if (targetToCenter < sourceToCenter) {
                    return (sourceToCenter - targetToCenter) * 10 + 1;
                }
            }
            case CAPTURING -> {
                // if kill an enemy
                if (game.getPiece(move.getDestination()) != null) {
                    if (Objects.requireNonNull(game.getPiece(move.getDestination())).getPlayer() != null) {
                        if (Objects.requireNonNull(game.getPiece(move.getDestination())).getPlayer() != game.getCurrentPlayer()) {
                            return 100;
                        }
                    }
                }
            }
            case BLOCKING -> {
                // If the destination hits the boarder line, not preferable.
                if (move.getDestination().x() == 0 || move.getDestination().x() == game.configuration.size - 1 ||
                        move.getDestination().y() == 0 || move.getDestination().y() == game.configuration.size - 1) {
                    return 1;
                }
                // If block a knight, preferable
                var left = new Place(move.getDestination().x() - 1, move.getDestination().y());
                var right = new Place(move.getDestination().x() + 1, move.getDestination().y());
                var up = new Place(move.getDestination().x(), move.getDestination().y() + 1);
                var down = new Place(move.getDestination().x(), move.getDestination().y() - 1);
                ArrayList<Place> stream = new ArrayList<>(Arrays.asList(left, right, up, down));

                stream.removeIf(curPlace -> !(isBlocked(this.game, curPlace)));
                // the more pieces this move blocks, the more it is preferable
                return 100 * stream.size() + 1;
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
        return 0;
    }

    private boolean isBlocked(Game game, Place place) {
        if (game.getPiece(place) == null) {
            return false;
        }
        return game.getPiece(place) instanceof Knight &&
                Objects.requireNonNull(game.getPiece(place)).getPlayer() != game.currentPlayer;
    }

    private int distanceFromCentral(Place targetPlace, Place centralPlace) {
        return Math.abs(targetPlace.x() - centralPlace.x()) + Math.abs(targetPlace.y() - centralPlace.y());
    }
}

