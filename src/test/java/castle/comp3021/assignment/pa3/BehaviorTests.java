package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.mock.MockPlayer;
import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.protocol.Behavior;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;
import castle.comp3021.assignment.textversion.JesonMor;
import castle.comp3021.assignment.textversion.SmartPlayerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BehaviorTests {
    static class Game extends JesonMor {
        public Game(Configuration configuration) {
            super(configuration);
        }

        @Override
        public void refreshOutput() {
        }
    }

    @Test
    @Timeout(60)
    public void testIntegratedSmartStrategy() {
        var winningCount = 0;

        for (int j = 0; j < 5; j++) {
            var size = 15;
            var blackStrategy = Strategy.RANDOM;
            var blackBehavior = Behavior.RANDOM;
            var smartPlayer = SmartPlayerFactory.generateSmartPlayer(size);
            var smartPieces = SmartPlayerFactory.generateSmartPieces(size, smartPlayer);
            var blackPlayer = new ComputerPlayer("black", Color.BLACK, blackStrategy);
            var numMovesProtection = 20;
            var criticalRegionSize = 5;
            var criticalRegionCapacity = 2;
            Configuration configuration =
                    new Configuration(size, new Player[]{smartPlayer, blackPlayer}, numMovesProtection,
                            criticalRegionSize, criticalRegionCapacity);
            try {
                configuration.validateConfiguration();
            } catch (InvalidConfigurationError e) {
                throw e;
            }

            for (int i = 0; i < size; i++) {
                if (i % 2 == 0) {
                    var piece = new Knight(blackPlayer, blackBehavior);
                    configuration.addInitialPiece(piece, i, size - 1);
                } else {
                    var piece = new Archer(blackPlayer, blackBehavior);
                    configuration.addInitialPiece(piece, i, size - 1);
                }
            }
            for (int i = 0; i < size; i++) {
                configuration.addInitialPiece(smartPieces[i], i, 0);
            }
            var game = new Game(configuration);
            var winner = game.start();
            if (winner.equals(smartPlayer)) {
                winningCount++;
            }
        }
        assertTrue(winningCount >= 3);
    }

    @Test
    @Timeout(1)
    public void testRandomStrategy() {
        var player1 = new ComputerPlayer("random", Color.BLACK, Strategy.RANDOM);
        var player2 = new MockPlayer();
        var config = new Configuration(9, new Player[]{player1, player2});
        var knight1 = new Knight(player1, Behavior.RANDOM);
        var archer1 = new Archer(player1, Behavior.RANDOM);
        var knight2 = new Knight(player2);
        config.addInitialPiece(knight1, 2, 2);
        config.addInitialPiece(archer1, 5, 4);
        config.addInitialPiece(knight2, 7, 7);
        var game = new Game(config);

        // test knight piece random
        var moves = new HashSet<Move>();
        for (int i = 0; i < 200; i++) {
            var move = knight1.getCandidateMove(game, new Place(2, 2));
            moves.add(move);
        }
        assertEquals(8, moves.size());

        // test archer piece random
        moves.clear();
        for (int i = 0; i < 200; i++) {
            moves.add(archer1.getCandidateMove(game, new Place(5, 4)));
        }
        assertEquals(16, moves.size());

        // test player random
        moves.clear();
        var availableMoves = game.getAvailableMoves(player1);
        for (int i = 0; i < 200; i++) {
            moves.add(player1.nextMove(game, availableMoves));
        }
        assertEquals(availableMoves.length, moves.size());
    }

    @Test
    @Timeout(1)
    public void testGreedyBehavior() {
        var player1 = new ComputerPlayer("random", Color.BLACK, Strategy.SMART);
        var player2 = new MockPlayer();
        var config = new Configuration(9, new Player[]{player1, player2});
        var knight1 = new Knight(player1, Behavior.GREEDY);
        var archer1 = new Archer(player1, Behavior.RANDOM);
        var knight2 = new Knight(player2);
        config.addInitialPiece(knight1, 3, 2);
        config.addInitialPiece(archer1, 5, 4);
        config.addInitialPiece(knight2, 6, 7);
        var game = new Game(config);

        // test knight piece greedy
        for (int i = 0; i < 10; i++) {
            assertEquals(new Move(3, 2, 4, 4), knight1.getCandidateMove(game, new Place(3, 2)));
        }

        // test player random
//        for (int i = 0; i < 10; i++) {
//            var move = player1.nextMove(game, game.getAvailableMoves(player1));
//            assertEquals(new Move(3, 2, 4, 4), move);
//        }
    }

    @Test
    @Timeout(1)
    public void testBlockingBehavior() {
        var player1 = new ComputerPlayer("random", Color.BLACK, Strategy.SMART);
        var player2 = new MockPlayer();
        var config = new Configuration(9, new Player[]{player1, player2});
        var knight1 = new Knight(player1, Behavior.BLOCKING);
        var archer1 = new Archer(player1, Behavior.BLOCKING);
        var archer11 = new Archer(player1, Behavior.RANDOM);
        var knight2 = new Knight(player2);
        var archer2 = new Archer(player2);
        config.addInitialPiece(knight1, 3, 6);
        config.addInitialPiece(archer1, 5, 8);
        config.addInitialPiece(archer11, 0, 8);
        config.addInitialPiece(knight2, 6, 7);
        config.addInitialPiece(archer2, 1, 7);
        var game = new Game(config);

        for (int i = 0; i < 10; i++) {
            // test knight piece blocking
            assertEquals(new Move(3, 6, 5, 7), knight1.getCandidateMove(game, new Place(3, 6)));
            // test archer piece blocking
            assertEquals(new Move(5, 8, 5, 7), archer1.getCandidateMove(game, new Place(5, 8)));
            // test player blocking
//            assertEquals(new Place(5, 7), player1.nextMove(game, game.getAvailableMoves(player1)).getDestination());
        }
    }

    @Test
    @Timeout(1)
    public void testCapturingBehavior() {
        var player1 = new ComputerPlayer("random", Color.BLACK, Strategy.SMART);
        var player2 = new MockPlayer();
        var config = new Configuration(9, new Player[]{player1, player2});
        var knight1 = new Knight(player1, Behavior.CAPTURING);
        var archer1 = new Archer(player1, Behavior.CAPTURING);
        var archer11 = new Archer(player1, Behavior.RANDOM);
        var knight2 = new Knight(player2);
        var archer2 = new Archer(player2);
        config.addInitialPiece(knight1, 3, 6);
        config.addInitialPiece(archer1, 8, 7);
        config.addInitialPiece(archer11, 0, 8);
        config.addInitialPiece(knight2, 6, 7);
        config.addInitialPiece(archer2, 1, 7);
        var game = new Game(config);

        for (int i = 0; i < 10; i++) {
            // test knight piece blocking
            assertEquals(new Move(3, 6, 1, 7), knight1.getCandidateMove(game, new Place(3, 6)));
            // test archer piece blocking
            assertEquals(new Move(8, 7, 1, 7), archer1.getCandidateMove(game, new Place(8, 7)));
            // test player blocking
//            assertEquals(new Place(1, 7), player1.nextMove(game, game.getAvailableMoves(player1)).getDestination());
        }
    }
}
