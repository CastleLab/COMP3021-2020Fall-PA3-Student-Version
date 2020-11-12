package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.protocol.Piece;
import castle.comp3021.assignment.protocol.Player;
import castle.comp3021.assignment.textversion.SmartPlayerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmartPlayerFactoryTests {
    @Test
    public void test() {
        int size = 9;
        Player player = SmartPlayerFactory.generateSmartPlayer(size);
        Piece[] pieces = SmartPlayerFactory.generateSmartPieces(size, player);

        assertTrue(player instanceof ComputerPlayer);
        assertEquals(size, pieces.length);
        for (int i = 0; i < size; i++) {
            var piece = pieces[i];
            assertEquals(player, piece.getPlayer());
            if (i % 2 == 0) {
                assertTrue(piece instanceof Knight);
            } else {
                assertTrue(piece instanceof Archer);
            }
        }
    }
}
