package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.action.PausePieceAction;
import castle.comp3021.assignment.action.ResumePieceAction;
import castle.comp3021.assignment.action.TerminatePieceAction;
import castle.comp3021.assignment.action.UndoAction;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.Configuration;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.Player;
import castle.comp3021.assignment.protocol.exception.ActionException;
import castle.comp3021.assignment.textversion.JesonMor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTests {
    Game game;

    @BeforeEach
    public void setup() {
        var player1 = new ConsolePlayer("ConsolePlayer");
        var player2 = new ComputerPlayer("ConsolePlayer");
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(new Knight(player1), 0, 0);
        config.addInitialPiece(new Knight(player1), 1, 0);
        config.addInitialPiece(new Knight(player1), 2, 0);
        config.addInitialPiece(new Knight(player2), 0, 4);
        config.addInitialPiece(new Knight(player2), 1, 4);
        config.addInitialPiece(new Knight(player2), 2, 4);
        game = new JesonMor(config);
    }

    @AfterEach
    public void teardown() {
        game.getConfiguration().getPieceThreadMap().forEach((key, value) -> {
            key.terminate();
            value.interrupt();
        });
    }

    @Test
    @Timeout(1)
    public void testPauseAction() {
        // pause a computer player's piece should succeed
        assertDoesNotThrow(() -> new PausePieceAction(game, new String[]{"a5"}).perform());
        assertDoesNotThrow(() -> new PausePieceAction(game, new String[]{"b5"}).perform());
        assertDoesNotThrow(() -> new PausePieceAction(game, new String[]{"c5"}).perform());
        // pause an already-paused piece should succeed
        assertDoesNotThrow(() -> new PausePieceAction(game, new String[]{"c5"}).perform());
        // pause a human player's piece should fail
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{"a1"}).perform());
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{"a2"}).perform());
        // pause a non-exist piece should fail
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{"a8"}).perform());
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{"as12"}).perform());
        // pause nothing should fail
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{""}).perform());
        assertThrows(ActionException.class, () -> new PausePieceAction(game, new String[]{}).perform());
    }

    @Test
    @Timeout(1)
    public void testResumeAction() {
        // pause then resume should succeed
        assertDoesNotThrow(() -> new PausePieceAction(game, new String[]{"a5"}).perform());
        assertDoesNotThrow(() -> new ResumePieceAction(game, new String[]{"a5"}).perform());
        // resume a not-paused piece should succeed
        assertDoesNotThrow(() -> new ResumePieceAction(game, new String[]{"b5"}).perform());
        // resume a human player's piece should fail
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{"a1"}).perform());
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{"a2"}).perform());
        // resume a non-exist piece should fail
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{"a8"}).perform());
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{"as12"}).perform());
        // resume nothing should fail
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{""}).perform());
        assertThrows(ActionException.class, () -> new ResumePieceAction(game, new String[]{}).perform());
    }

    @Test
    @Timeout(1)
    public void testTerminateAction() throws InterruptedException {
        // stop a computer player's piece should succeed
        assertDoesNotThrow(() -> new TerminatePieceAction(game, new String[]{"a5"}).perform());
        assertDoesNotThrow(() -> new TerminatePieceAction(game, new String[]{"b5"}).perform());
        assertDoesNotThrow(() -> new TerminatePieceAction(game, new String[]{"c5"}).perform());
        // terminate an already-terminated piece should succeed
        assertDoesNotThrow(() -> new TerminatePieceAction(game, new String[]{"c5"}).perform());
        // all thread should be stopped
        Thread.sleep(100);
        game.getConfiguration().getAllThreads().forEach(thread -> assertFalse(thread.isAlive()));
        // terminate a human player's piece should fail
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{"a1"}).perform());
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{"a2"}).perform());
        // terminate a non-exist piece should fail
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{"a8"}).perform());
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{"as12"}).perform());
        // terminate nothing should fail
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{""}).perform());
        assertThrows(ActionException.class, () -> new TerminatePieceAction(game, new String[]{}).perform());
    }

    @Test
    @Timeout(1)
    public void testUndoAction() {
        // when undo throws UndoException, action should also throw
        assertThrows(ActionException.class, () -> new UndoAction(game, new String[]{}).perform());
    }
}
