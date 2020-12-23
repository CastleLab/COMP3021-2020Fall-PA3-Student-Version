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

    /**
     * A snapshot class which stores the necessary information for {@link this#undo()}
     */
    static class Snapshot {
        Map<Player, Integer> playerScoreMap;
        int numMoves;
        Piece[][] board;
        Player currentPlayer;
        List<MoveRecord> moveRecords;

        public Snapshot(Player[] players, Player currentPlayer, int numMoves, Piece[][] board, List<MoveRecord> records) {
            this.playerScoreMap = new HashMap<>();
            this.moveRecords = new ArrayList<>();

            for (var player :
                    players) {
                this.playerScoreMap.put(player, player.getScore());
            }

            for (var record : records){
                moveRecords.add(new MoveRecord(record));
            }

            this.numMoves = numMoves;
            this.board = new Piece[board.length][];
            for (int i = 0; i < board.length; i++) {
                this.board[i] = new Piece[board[i].length];
                System.arraycopy(board[i], 0, this.board[i], 0, board[i].length);
            }
            this.currentPlayer = currentPlayer;
        }

        public Snapshot(Snapshot snapshot) {
            this(snapshot.playerScoreMap.keySet().toArray(new Player[0]), snapshot.currentPlayer, snapshot.numMoves,
                    snapshot.board, snapshot.moveRecords);
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
     * a stack of {@link Snapshot}with limited capacity
     * Storing history information for {@link this#undo()}
     */
    private final Stack<Snapshot> histories = new LimitedStack<>(undoLimit * 2 + 1);

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

        // save snapshot
        this.histories.push(new Snapshot(this.configuration.getPlayers(), this.currentPlayer, this.numMoves,
                this.board, this.moveRecords));

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

                // save snapshot
                this.histories.push(new Snapshot(this.configuration.getPlayers(), this.currentPlayer, this.numMoves,
                        this.board, this.moveRecords));
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
     *          return an array containing only ONE move selected from all available moves
     *
     * @param player the player whose available moves to get
     * @return an array of available moves
     */
    public @NotNull Move[] getAvailableMoves(Player player) {
        //TODO
        var map = new HashMap<Place, Piece>();
        // find all pieces belonging to the player
        for (int i = 0; i < this.configuration.getSize(); i++) {
            for (int j = 0; j < this.configuration.getSize(); j++) {
                var piece = this.getPiece(i, j);
                if (piece == null) {
                    continue;
                }
                if (!piece.getPlayer().equals(player)) {
                    continue;
                }
                map.put(new Place(i, j), piece);
            }
        }
        if (player instanceof ComputerPlayer) {
            System.out.println("Computer is figuring out next move...");
        }
        return map.entrySet().parallelStream()
                .map(placePieceEntry -> {
                    var piece = placePieceEntry.getValue();
                    var place = placePieceEntry.getKey();
                    if (player instanceof HumanPlayer) {
                        var availableMoves = piece.getAvailableMoves(this, place);
                        return Arrays.asList(availableMoves);
                    } else {
                        var candidateMove = piece.getCandidateMove(this, place);
                        if (candidateMove != null) {
                            return Collections.singletonList(candidateMove);
                        }
                        return new ArrayList<Move>();
                    }
                })
                .flatMap(Collection::parallelStream).toArray(Move[]::new);
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
     *     - Utilize {@link this#histories} which stores the snapshot of each round of game
     *       which includes necessary game information (e.g., board, player scores)
     *     - Some useful methods for {@link this#histories}:
     *          {@link Stack#pop()}, {@link Stack#peek()}, {@link Stack#push(Object)}
     *     - After undo, refresh the output
     *     - Print "Game state reverted"
     * </p>
     *
     * @throws UndoException which is defined under protocol/exception
     *
     */
    @Override
    public void undo() throws UndoException {
        //TODO
        var players = this.configuration.getPlayers();
        var errorMsg = "Undo is only supported when there is one human player and one computer player";
        if (players.length != 2
                || !(players[0] instanceof ComputerPlayer && players[1] instanceof HumanPlayer)
                && !(players[1] instanceof ComputerPlayer && players[0] instanceof HumanPlayer)) {
            throw new UndoException(errorMsg);
        }

        if (this.histories.size() <= 1) {
            throw new UndoException("No further undo is allowed");
        }

        this.histories.pop();
        while (this.histories.peek().numMoves > 0
                && !(this.histories.peek().currentPlayer instanceof ComputerPlayer)) {
            this.histories.pop();
        }
        var snapshot = this.histories.pop();
        this.board = snapshot.board;
        this.numMoves = snapshot.numMoves;
        this.moveRecords = snapshot.moveRecords;

        for (var player :
                this.configuration.getPlayers()) {
            player.setScore(snapshot.playerScoreMap.get(player));
        }
        this.histories.push(new Snapshot(snapshot));
        this.refreshOutput();
        System.out.println("Game state reverted");
    }

    @Override
    public void showHistoryMove() {
        //TODO
        if (this.moveRecords.isEmpty()){
            System.out.println("No move history.");
            return;
        }

        StringBuilder moveRecordString = new StringBuilder();
        for (var record : this.moveRecords) {
            moveRecordString.append(record.toString()).append("\n");
        }
        System.out.println("\nGame History:");
        System.out.println(moveRecordString.toString());
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
