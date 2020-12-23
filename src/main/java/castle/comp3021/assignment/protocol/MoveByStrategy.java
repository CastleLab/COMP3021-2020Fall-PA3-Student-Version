package castle.comp3021.assignment.protocol;


import java.util.*;

public class MoveByStrategy {
    private final Strategy strategy;
    private final Game game;
    private final Move[] availableMoves;

    public MoveByStrategy(Game game, Move[] availableMoves, Strategy strategy) {
        this.game = game;
        this.availableMoves = availableMoves;
        this.strategy = strategy;
    }

    public Move getNextMove() {
        int nextIndex;
        nextIndex = new Random().nextInt(availableMoves.length);
//        if (this.strategy == Strategy.RANDOM){
//            nextIndex = new Random().nextInt(availableMoves.length);
//        }
//        else{
//            List<Integer> weights = new ArrayList<>(this.availableMoves.length);
//
//            for (Move availableMove : this.availableMoves) {
//                weights.add(assignWeight(this.game, availableMove, this.strategy));
//            }
//
//            nextIndex = weights.indexOf(Collections.max(weights));
//        }
        return availableMoves[nextIndex];
    }

    private int assignWeight(Game game, Move move, Strategy strategy) {
        switch (strategy) {
            case RANDOM -> {
                return 1;
            }
            case SMART -> {
                //if a piece gets closer to central place
                var sourceToCenter = distanceFromCentral(move.getSource(), game.getCentralPlace());
                var targetToCenter = distanceFromCentral(move.getDestination(), game.getCentralPlace());

                if (targetToCenter < sourceToCenter) {
                    return (sourceToCenter - targetToCenter) * 10 + 1;
                }
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
        return 0;
    }


    private int distanceFromCentral(Place targetPlace, Place centralPlace) {
        return Math.abs(targetPlace.x() - centralPlace.x()) + Math.abs(targetPlace.y() - centralPlace.y());
    }
}

