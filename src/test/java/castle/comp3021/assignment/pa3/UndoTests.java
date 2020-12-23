package castle.comp3021.assignment.pa3;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.HumanPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.UndoException;
import castle.comp3021.assignment.textversion.JesonMor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class UndoTests {
    UndoPlayer player1;
    ComputerPlayer player2;
    Game game;
    Configuration config;

    static class UndoPlayer extends HumanPlayer {
        @FunctionalInterface
        interface NextMoveLogic {
            @NotNull Move nextMove(Player self, Game game, Move[] availableMoves);
        }

        public NextMoveLogic nextMoveLogic = null;

        public UndoPlayer(Color color) {
            super("UndoPlayer", color);
        }

        @Override
        public @NotNull Move nextMove(Game game, Move[] availableMoves) {
            if (nextMoveLogic == null) {
                return availableMoves[0];
            } else {
                return this.nextMoveLogic.nextMove(this, game, availableMoves);
            }
        }
    }


    @BeforeEach
    public void setup() {
        player1 = new UndoPlayer(Color.GREEN);
        player2 = new ComputerPlayer("player2");
        config = new Configuration(5, new Player[]{player1, player2}, 12);
        // all pieces have no thread runnable body so it should all timeout and return null for best move
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player1);
            config.addInitialPiece(piece, i, 0);
        }
        for (int i = 0; i < config.getSize(); i++) {
            var piece = new Knight(player2);
            config.addInitialPiece(piece, i, config.getSize() - 1);
        }
        game = new JesonMor(config);
    }

    public static HumanPlayer getHumanPlayer(Player... players) {
        for (var player : players) {
            if (player instanceof HumanPlayer) {
                return (HumanPlayer) player;
            }
        }
        return null;
    }

    public static ComputerPlayer getComputerPlayer(Player... players) {
        for (var player : players) {
            if (player instanceof ComputerPlayer) {
                return (ComputerPlayer) player;
            }
        }
        return null;
    }

    public static boolean instanceOfTheSamePlayerKind(Player... players) {
        var human = getHumanPlayer(players);
        if (human == null) return true;
        else return getComputerPlayer(players) == null;
    }

    public static boolean isGameEqual(Game game1, Game game2) {
        // check num of Moves
        if (game1.getNumMoves() != game2.getNumMoves()) {
            return false;
        }

        // check player scores
        var human1 = getHumanPlayer(game1.getConfiguration().getPlayers());
        var human2 = getHumanPlayer(game2.getConfiguration().getPlayers());
        var computer1 = getComputerPlayer(game1.getConfiguration().getPlayers());
        var computer2 = getComputerPlayer(game2.getConfiguration().getPlayers());
        if (human1 == null) {
            return false;
        }
        if (human2 == null) {
            return false;
        }
        if (computer1 == null) {
            return false;
        }
        if (computer2 == null) {
            return false;
        }
        if (human1.getScore() != human2.getScore()) {
            return false;
        }
        if (computer1.getScore() != computer2.getScore()) {
            return false;
        }

        // check board
        if (game1.getConfiguration().getSize() != game2.getConfiguration().getSize()) {
            return false;
        }
        for (int i = 0; i < game1.getConfiguration().getSize(); i++) {
            for (int j = 0; j < game1.getConfiguration().getSize(); j++) {
                var piece1 = game1.getBoard()[i][j];
                var piece2 = game2.getBoard()[i][j];
                if (piece1 == null && piece2 == null) {
                    continue;
                } else if (piece1 == null) {
                    return false;
                } else if (piece2 == null) {
                    return false;
                } else if (instanceOfTheSamePlayerKind(piece1.getPlayer(), piece2.getPlayer())) {
                    if (!(piece1 instanceof Knight && piece2 instanceof Knight
                            || piece1 instanceof Archer && piece2 instanceof Archer)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    @Timeout(5)
    public void testGameUndo() {
        var count = new AtomicInteger(0);
        var stack = new Stack<Game>();
        player1.nextMoveLogic = (self, game1, availableMoves) -> {
            if (count.get() < 3 && stack.size() == 1) {
                assertDoesNotThrow(game1::undo);
                count.incrementAndGet();
                var snapshot = stack.pop();
                assertTrue(isGameEqual(snapshot, game1));
            }
            try {
                stack.push(game1.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return game1.getAvailableMoves(self)[new Random().nextInt(game1.getAvailableMoves(self).length)];
        };
        game.start();
    }

    @Test
    @Timeout(5)
    public void testGameUndoNoMove() {
        var stack = new Stack<Game>();
        player1.nextMoveLogic = (self, game1, availableMoves) -> {
            if (stack.size() == 0) {
                assertThrows(UndoException.class, game1::undo);
            }
            try {
                stack.push(game1.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return game1.getAvailableMoves(self)[new Random().nextInt(game1.getAvailableMoves(self).length)];
        };
        game.start();
    }

    @Test
    @Timeout(5)
    public void testGameUndoLimit() {
        var stack = new Stack<Game>();
        player1.nextMoveLogic = (self, game1, availableMoves) -> {
            if (stack.size() == 5) {
                assertDoesNotThrow(game1::undo);
                assertDoesNotThrow(game1::undo);
                assertDoesNotThrow(game1::undo);
                assertThrows(UndoException.class, game1::undo);
            }
            try {
                stack.push(game1.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return game1.getAvailableMoves(self)[new Random().nextInt(game1.getAvailableMoves(self).length)];
        };
        game.start();
    }
}
