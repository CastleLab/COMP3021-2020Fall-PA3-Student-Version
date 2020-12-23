package castle.comp3021.assignment.textversion;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.player.ComputerPlayer;

import java.util.ArrayList;

public class SmartPlayerFactory {
    public static Player generateSmartPlayer(int boardSize) {
        return new ComputerPlayer("smart", Color.BLACK, Strategy.SMART);
    }

    public static Piece[] generateSmartPieces(int boardSize, Player player) {
        var ls = new ArrayList<Piece>();
        for (int i = 0; i < boardSize; i++) {
            if (i % 2 == 0) {
                ls.add(new Knight(player, Behavior.GREEDY));
            } else {
                ls.add(new Archer(player, Behavior.GREEDY));
            }
        }
        return ls.toArray(new Piece[]{});
    }
}
