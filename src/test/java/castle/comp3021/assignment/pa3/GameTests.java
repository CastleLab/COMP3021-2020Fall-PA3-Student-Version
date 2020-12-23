package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.textversion.JesonMor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    @Test
    @Timeout(value = 5)
    public void testGetAvailableMoves() {
        var player1 = new ComputerPlayer("player1");
        var player2 = new ComputerPlayer("player2");
        var config = new Configuration(5, new Player[]{player1, player2});
        // all pieces have no thread runnable body so it should all timeout and return null for best move
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player1);
            config.addInitialPiece(piece, i, 0);
        }
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player2);
            config.addInitialPiece(piece, i, config.getSize() - 1);
        }
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var moves = game.getAvailableMoves(player1);
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 1000);
        assertEquals(config.getSize(), moves.length);
    }

    @Test
    @Timeout(value = 5)
    public void testGetAvailableMovesTimeout() {
        var player1 = new ComputerPlayer("player1");
        var player2 = new ComputerPlayer("player2");
        var config = new Configuration(5, new Player[]{player1, player2});
        // all pieces have no thread runnable body so it should all timeout and return null for best move
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new PieceTests.MockKnight(player1);
            config.addInitialPiece(piece, i, 0);
        }
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new PieceTests.MockKnight(player2);
            config.addInitialPiece(piece, i, config.getSize() - 1);
        }
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var moves = game.getAvailableMoves(player1);
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 4500);
        assertEquals(0, moves.length);
    }

    @Test
    @Timeout(value = 5)
    public void testGameStopAllThreads() throws InterruptedException {
        var player1 = new ComputerPlayer("player1", Color.GREEN);
        var player2 = new ComputerPlayer("player2");
        var config = new Configuration(5, new Player[]{player1, player2});
        // all pieces have no thread runnable body so it should all timeout and return null for best move
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player1);
            config.addInitialPiece(piece, i, 0);
        }
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player2);
            config.addInitialPiece(piece, i, config.getSize() - 1);
        }
        var game = new JesonMor(config);
        game.start();
        Thread.sleep(100);
        for (var thread :
                config.getAllThreads()) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    @Timeout(5)
    public void testGetAvailableMovesHumanPlayer() {
        var player1 = new ConsolePlayer("player1", Color.GREEN);
        var player2 = new ComputerPlayer("player2");
        var config = new Configuration(5, new Player[]{player1, player2});
        // all pieces have no thread runnable body so it should all timeout and return null for best move
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player1);
            config.addInitialPiece(piece, i, 0);
        }
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player2);
            config.addInitialPiece(piece, i, config.getSize() - 1);
        }
        var game = new JesonMor(config);
        var moves = game.getAvailableMoves(player1);
        assertTrue(config.getSize() < moves.length);
        moves = game.getAvailableMoves(player2);
        assertEquals(config.getSize(), moves.length);
    }
}
