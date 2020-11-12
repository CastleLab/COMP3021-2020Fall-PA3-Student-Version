package castle.comp3021.assignment.textversion;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.player.ComputerPlayer;

import java.util.ArrayList;

/**
 * There is a special test, which is meant to check if your designed strategy/behaviours are smart enough.
 * Therefore, this factory class has two static methods, which allow you to configure the smart computer player and
 * its pieces as you want.
 * <p>
 * The player and pieces returned by this two methods will be used in playing a game against random player.
 */
public class SmartPlayerFactory {
    /**
     * Return a player you want to use in smart strategy tests.
     *
     * @param boardSize size of board
     * @return the smart {@link ComputerPlayer}
     */
    public static Player generateSmartPlayer(int boardSize) {
        // TODO optional, if you want to change the smart player instantiation
        return new ComputerPlayer("smart", Color.BLACK, Strategy.SMART);
    }

    /**
     * Specify the specific behavior that each piece adopts.
     * The returned pieces should belong to the given player, and they will be used in testing your smart
     * strategy against random computer player.
     * <p>
     * The order of piece should be Knight, Archer, Knight, ...
     * Currently, each piece adopts {@link Behavior#GREEDY}, you can customize them
     * For example: some {@link Knight} adopt {@link Behavior#GREEDY} while others adopt {@link Behavior#CAPTURING}
     *
     * @param boardSize size of board
     * @param player    the smart {@link ComputerPlayer} return by {@link SmartPlayerFactory#generateSmartPlayer(int)}
     * @return an array with pieces specifies each piece's behavior (including behaviors in {@link Behavior})
     */
    public static Piece[] generateSmartPieces(int boardSize, Player player) {
        // TODO optional, if you want to change smart player's pieces instantiation
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
