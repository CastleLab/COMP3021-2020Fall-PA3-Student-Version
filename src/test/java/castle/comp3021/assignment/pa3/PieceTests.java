package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.mock.MockPlayer;
import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.protocol.Configuration;
import castle.comp3021.assignment.protocol.Move;
import castle.comp3021.assignment.protocol.Place;
import castle.comp3021.assignment.protocol.Player;
import castle.comp3021.assignment.textversion.JesonMor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTests {
    public static class MockKnight extends Knight {
        public MockKnight(Player player) {
            super(player);
        }

        @Override
        public void run() {
        }
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceGetCandidateMove() {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        var game = new JesonMor(config);
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        assertEquals(new Move(0, 0, 1, 2), move);
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceThread() {
        var player1 = new MockPlayer();
        var piece1 = new Knight(player1);
        var thread = new Thread(piece1);
        thread.start();
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceThreadInterrupt() {
        var player1 = new MockPlayer();
        var piece1 = new Knight(player1);
        var thread = new Thread(piece1);
        thread.start();
        thread.interrupt();
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceThreadPause() throws InterruptedException {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        var game = new JesonMor(config);
        piece1.pause();
        config.getPieceThread(piece1).interrupt();
        Thread.sleep(100);
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        assertNull(move);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testPieceThreadStop() throws InterruptedException {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        var game = new JesonMor(config);
        piece1.terminate();
        config.getPieceThread(piece1).interrupt();
        Thread.sleep(100);
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        assertNull(move);
        piece1.resume();
        move = piece1.getCandidateMove(game, new Place(0, 0));
        assertNull(move);
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceThreadPauseResume() throws InterruptedException {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        var game = new JesonMor(config);
        piece1.pause();
        config.getPieceThread(piece1).interrupt();
        Thread.sleep(100);
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        assertNull(move);
        piece1.resume();
        move = piece1.getCandidateMove(game, new Place(0, 0));
        assertEquals(new Move(0, 0, 1, 2), move);
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    public void testPieceGetCandidateMoveTimeout() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = new MockKnight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime > 900);
        assertTrue(endTime - startTime < 1100);
        assertNull(move);
    }

    @Test
    @Timeout(1)
    public void testPieceNoAvailableMove() {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var piece3 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        config.addInitialPiece(piece3, 0, 1);
        // no available move for piece1
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 500);
        assertNull(move);
    }

    @Test
    @Timeout(1)
    public void testPieceBestMoveWhenCriticalRegionFull1() {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece0 = new Knight(player1);
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var config = new Configuration(5, new Player[]{player1, player2}, 0, 3, 1);
        // the critical region for player1 is already full. There is already one knight of player1 in critical region.
        config.addInitialPiece(piece0, 0, 3);
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 1, 0);
        // no available move for piece1
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var move = piece1.getCandidateMove(game, new Place(0, 0));
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 500);
        assertNull(move);
    }

    @Test
    @Timeout(1)
    public void testPieceBestMoveWhenCriticalRegionFull2() {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece0 = new Knight(player1);
        var piece1 = new Knight(player2);
        var piece2 = new Archer(player1);
        var config = new Configuration(5, new Player[]{player1, player2}, 0, 3, 1);
        // the critical region for player1 is already full. There is already one knight of player1 in critical region.
        config.addInitialPiece(piece0, 0, 3);
        config.addInitialPiece(piece1, 0, 2);
        config.addInitialPiece(piece2, 2, 4);
        // no available move for piece1
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var move = piece0.getCandidateMove(game, new Place(0, 3));
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 500);
        assertEquals(new Move(0, 3, 2, 2), move);
    }

    @Test
    @Timeout(1)
    public void testPieceBestMoveWhenCriticalRegionFull3() {
        var player1 = new ComputerPlayer("");
        var player2 = new MockPlayer();
        var piece0 = new Knight(player1);
        var piece1 = new Knight(player1);
        var piece2 = new Archer(player2);
        var piece3 = new Knight(player1);
        var config = new Configuration(5, new Player[]{player1, player2}, 0, 3, 1);
        // the critical region for player1 is already full. There is already one knight of player1 in critical region.
        config.addInitialPiece(piece0, 1, 3);
        config.addInitialPiece(piece1, 0, 1);
        config.addInitialPiece(piece2, 1, 2);
        config.addInitialPiece(piece3, 3, 2);
        // no available move for piece1
        var game = new JesonMor(config);
        var startTime = System.currentTimeMillis();
        var move = piece0.getCandidateMove(game, new Place(1, 3));
        var endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 500);
        assertEquals(new Move(1, 3, 3, 4), move);
    }
}
