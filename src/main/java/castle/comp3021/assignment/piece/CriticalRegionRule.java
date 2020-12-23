package castle.comp3021.assignment.piece;

import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.Move;
import castle.comp3021.assignment.protocol.Place;

public class CriticalRegionRule implements Rule {
    //TODO
    private boolean isInCriticalRegion(Game game, Place place) {
        var criticalRegionSize = game.getConfiguration().getCriticalRegionSize();
        var central = game.getCentralPlace();
        return Math.abs(place.y() - central.y()) <= criticalRegionSize / 2;
    }

    @Override
    public boolean validate(Game game, Move move) {
        //TODO
        var sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            return false;
        }
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        if (!isInCriticalRegion(game, move.getDestination())) {
            // if the destination is outside critical region, it must not violate this rule
            return true;
        }
        if (isInCriticalRegion(game, move.getSource())) {
            // if the source is inside critical region, it must not violate this rule
            return true;
        }

        // the move must be going from outside to inside critical region
        var sourcePlayer = sourcePiece.getPlayer();
        // get the number of pieces
        int count = 0;
        for (int i = 0; i < game.getConfiguration().getSize(); i++) {
            for (int j = 0; j < game.getConfiguration().getSize(); j++) {
                var place = new Place(i, j);
                var piece = game.getPiece(place);
                if (piece == null) {
                    continue;
                }
                if (piece.getPlayer().equals(sourcePlayer) && isInCriticalRegion(game, place) && piece instanceof Knight) {
                    count++;
                }
            }
        }

        // there is already enough pieces inside critical region
        return count < game.getConfiguration().getCriticalRegionCapacity();
    }

    @Override
    public String getDescription() {
        return "critical region is full";
    }
}
