package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.textversion.JesonMor;
import castle.comp3021.assignment.mock.MockPiece;
import castle.comp3021.assignment.mock.MockPlayer;
import castle.comp3021.assignment.util.PA1Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputerPlayerTests {
    private Configuration config;
    private MockPlayer player1;
    private ComputerPlayer player2;

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = new ComputerPlayer("RandomPlayer");
        this.config = new Configuration(3, new Player[]{player1, player2});
    }

    @Test
    @PA1Test
    public void testNextMove() {
        var piece1 = new MockPiece(player1);
        var piece2 = new MockPiece(player2);
        this.config.addInitialPiece(piece1, 0, 0);
        this.config.addInitialPiece(piece2, 2, 2);
        var game = new JesonMor(this.config);
        var moves = game.getAvailableMoves(player2);
        var move = player2.nextMove(game, moves);
        assertTrue(Arrays.asList(moves).contains(move));
    }
}
