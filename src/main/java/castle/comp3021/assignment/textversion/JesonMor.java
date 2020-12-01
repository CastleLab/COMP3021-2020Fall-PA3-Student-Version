package castle.comp3021.assignment.textversion;

import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.HumanPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.Color;
import castle.comp3021.assignment.protocol.exception.UndoException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JesonMor extends Game {
    static class LimitedStack<T> extends Stack<T> {
        private final int capacity;

        public LimitedStack(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public T push(T item) {
            while (this.size() >= this.capacity) {
                this.remove(0);
            }
            return super.push(item);
        }
    }

    private Player winner;

    public JesonMor() {
        super();
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    private List<MoveRecord> moveRecords = new ArrayList<>();

    /**
     * Start the game
     * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until
     * a player wins.
     *
     * @return the winner
     */
    @Override
    public Player start() {
        // reset all things
        Player winner;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();


        while (true) {
            var player = this.configuration.getPlayers()[this.numMoves % this.configuration.getPlayers().length];
            this.currentPlayer = player;
            // let player make next move
            var availableMoves = this.getAvailableMoves(player);
            // there shouldn't be no available moves, if no available moves, the player with lower score wins
            if (availableMoves.length <= 0) {
                System.out.println("No available moves for the player " + player.getName());
                if (this.configuration.getPlayers()[0].getScore() < this.configuration.getPlayers()[1].getScore()) {
                    winner = this.configuration.getPlayers()[0];
                } else if (this.configuration.getPlayers()[0].getScore() > this.configuration.getPlayers()[1].getScore()) {
                    winner = this.configuration.getPlayers()[1];
                } else {
                    winner = player;
                }
            } else {
                var move = player.nextMove(this, availableMoves);
                var movedPiece = this.getPiece(move.getSource());
                // make move
                this.movePiece(move);
                this.numMoves++;
                System.out.println(player.getName() + " moved piece at " + move.getSource() + "to " + move.getDestination());
                this.updateScore(player, movedPiece, move);

                this.refreshOutput();

                // check if there is a winner and if there is, return the winner.
                // if there is no winner yet, continue the loop with label "round"
                winner = this.getWinner(player, movedPiece, move);
            }
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", player.getColor(), player.getName(), Color.DEFAULT);
                this.winner = winner;
                // stop all threads
                for (var entry :
                        this.configuration.getPieceThreadMap().entrySet()) {
                    entry.getKey().terminate();
                    entry.getValue().interrupt();
                }
                return winner;
            }
        }
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null;
     *
     * @param lastPlayer the last player who makes a move
     * @param lastMove   the last move made by lastPlayer
     * @param lastPiece  the last piece that is moved by the player
     * @return the winner if it exists, otherwise return null
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Player winner;

        // no winner within numMovesProtection moves
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }

        // first way to win: a piece leaves the central square, the piece should not be an Archer
        if ((lastPiece instanceof Knight) && lastMove.getSource().equals(this.configuration.getCentralPlace())
                && !lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
            winner = lastPlayer;
        } else {
            // second way to win: one player captures all the pieces of other players
            Player remainingPlayer = null;
            for (int i = 0; i < this.configuration.getSize(); i++) {
                for (int j = 0; j < this.configuration.getSize(); j++) {
                    var piece = this.getPiece(i, j);
                    if (piece == null) {
                        continue;
                    }
                    if (remainingPlayer == null) {
                        remainingPlayer = piece.getPlayer();
                    } else if (remainingPlayer != piece.getPlayer()) {
                        // there are still two players having pieces on board
                        return null;
                    }
                }
            }
            // if the previous for loop terminates, then there must be 1 player on board (it cannot be null).
            // then winner appears
            winner = remainingPlayer;
        }

        return winner;
    }

    /**
     * Update the score of a player according to the piece and corresponding move made by him just now.
     *
     * @param player the player who just makes a move
     * @param piece  the piece that is just moved
     * @param move   the move that is just made
     */
    public void updateScore(Player player, Piece piece, Move move) {
        var newScore = player.getScore();
        newScore += Math.abs(move.getSource().x() - move.getDestination().x());
        newScore += Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(newScore);
        System.out.println("score " + newScore);
    }


    /**
     * Make a move.
     *
     * @param move the move to make
     */
    public void movePiece(@NotNull Move move) {
        var sourcePiece = this.getPiece(move.getSource());
        assert sourcePiece != null;
        var destPiece = this.getPiece(move.getDestination());

        assert destPiece == null || !destPiece.getPlayer().equals(sourcePiece.getPlayer())
                : "cannot capture a piece belonging to the same player";

        // move the piece
        this.board[move.getDestination().x()][move.getDestination().y()] = sourcePiece;
        this.board[move.getSource().x()][move.getSource().y()] = null;

        moveRecords.add(new MoveRecord(getCurrentPlayer(), move));
    }

    /**
     * Get all available moves of one player.
     * The different between the original implementation is that in PA3
     * - for {@link HumanPlayer} (including {@link castle.comp3021.assignment.player.ConsolePlayer}:
     *          the returned available moves should be an array of ALL available moves
     * - for {@link ComputerPlayer}:
     *          before a candidate move is proposed, print "Computer is figuring out next move..."
     *          return an array containing candidate moves proposed by each piece thread of computer player. 
     *          Paused/terminated pieces will not propose candidate moves. 
     *          The number of moves in the array should be the same as the number of non-paused/non-terminated pieces. 
     *
     * @param player the player whose available moves to get
     * @return an array of available moves
     */
    public @NotNull Move[] getAvailableMoves(Player player) {
        //TODO
        return null;
    }

    /**
     * Undo a move of {@link HumanPlayer}
     * Undo is only supported when there is {@link HumanPlayer} and one {@link ComputerPlayer}
     * <p>
     *     Hint: you should consider several situations.
     *     - if players are not one {@link HumanPlayer} and one {@link ComputerPlayer}, throw an {@link UndoException}
     *       with error message "Undo is only supported when there is one human player and one computer player"
     *     - if there is no undo step can be done: throw an {@link UndoException}
     *       with error message "No further undo is allowed"
     *     - After undo, refresh the output
     *     - Print "Game state reverted"
     * </p>
     *
     * <Strong>NOTE</Strong>
     * You may add helper classes or variables to help this method, or within {@link this#start()}
     * in order to record some necessary information of each round (e.g., scores, moves)
     *
     * @throws UndoException which is defined under protocol/exception
     *
     */
    @Override
    public void undo() throws UndoException {
        //TODO
    }

    /**
     * Show history move records
     * If move record is empty, output "No move history."
     * Output format:
     * \n
     * Game History:
     * player: White; move:(1,1)->(2,3)
     * player: Black; move:(8,8)->(6,8)
     *
     * Hint:
     * - Take advantage of {@link MoveRecord#toString()} and {@link this#moveRecords}
     *
     */
    @Override
    public void showHistoryMove() {
        //TODO
    }

    @Override
    public String toString() {
        StringBuilder moveRecordString = new StringBuilder();
        for (var record : moveRecords) {
            moveRecordString.append(record.toString()).append("\n");
        }
        return String.format("### %s ###\n\n#Game setting\n%s\n\n#Move records\n%s\nEND",
                this.getClass().getSimpleName(),
                configuration.toString(),
                moveRecordString
        );
    }

    @Override
    public JesonMor clone() throws CloneNotSupportedException {
        var cloned = (JesonMor) super.clone();
        cloned.moveRecords = this.moveRecords.stream().map(moveRecord -> {
            try {
                return moveRecord.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        return cloned;
    }
}
