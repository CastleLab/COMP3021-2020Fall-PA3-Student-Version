package castle.comp3021.assignment.piece;

import castle.comp3021.assignment.protocol.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
    static class InvalidMove extends Move {
        public InvalidMove() {
            super(-1, -1, -1, -1);
        }
    }

    /**
     * A BlockingDeque containing the candidate move
     */
    private final BlockingDeque<Move> candidateMoveQueue;

    /**
     * A LinkedBlockingDeque storing the parameters {@link Game} and {@link Place}
     * When calculateMoveParametersQueue is empty, the current piece thread should be waiting
     * until parameters {@link Game} and {@link Place} are passed in, the thread starts calculate the candidate move.
     */
    private final BlockingDeque<Object[]> calculateMoveParametersQueue;

    public Archer(Player player, Behavior behavior) {
        super(player, behavior);
        this.candidateMoveQueue = new LinkedBlockingDeque<>();
        this.calculateMoveParametersQueue = new LinkedBlockingDeque<>();
    }

    public Archer(Player player) {
        super(player);
        this.candidateMoveQueue = new LinkedBlockingDeque<>();
        this.calculateMoveParametersQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        var moves = new ArrayList<Move>();
        for (int x = 0; x < game.getConfiguration().getSize(); x++) {
            if (x != source.x()) {
                moves.add(new Move(source, x, source.y()));
            }
        }
        for (int y = 0; y < game.getConfiguration().getSize(); y++) {
            if (y != source.y()) {
                moves.add(new Move(source, source.x(), y));
            }
        }

        return moves.stream()
                .filter(move -> validateMove(game, move))
                .toArray(Move[]::new);
    }

    /**
     * Returns a valid candidate move given the current game {@link Game} and place  {@link Place} of the piece.
     * A 1 second timeout should be set.
     * If time is out, then no candidate move is proposed for this piece this round
     * The implementation is the same as {@link Knight#getCandidateMove(Game, Place)}
     * <p>
     * Hint:
     * - The actual candidate move is selected in {@link Archer#run}
     * - if the returned move is invalid, nothing should be returned.
     * - Handle {@link InterruptedException}:
     * - nothing should be returned in such case
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return one candidate move
     */
    @Override
    public Move getCandidateMove(Game game, Place source) {
        //TODO
        var parameters = new Object[]{game, source};
        try {
            this.calculateMoveParametersQueue.put(parameters);
            var move = this.candidateMoveQueue.poll(1, TimeUnit.SECONDS);
            if (move instanceof Archer.InvalidMove) {
                return null;
            }
            return move;
        } catch (InterruptedException ignored) {
            return null;
        }
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[]{
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new VacantRule(),
                new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
                new ArcherMoveRule(),
                new CriticalRegionRule(),
        };
        for (var rule :
                rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }

    /**
     * An atomic boolean variable which marks whether this piece thread is running
     * running = true: this piece is running.
     * running = false: this piece is paused.
     */
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * An atomic boolean variable which marks whether this piece thread is stopped
     * stopped = false: this piece is running.
     * stopped = true: this piece stops, and cannot be paused again.
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    /**
     * Pause this piece thread.
     * Hint:
     * - Using {@link Archer#running}
     */
    @Override
    public void pause() {
        //TODO
        running.set(false);
    }

    /**
     * Resume the piece thread
     * Hint:
     * - Using {@link Archer#running}
     * - Using {@link Object#notifyAll()}
     */
    @Override
    public void resume() {
        //TODO
        running.set(true);
        synchronized (running) {
            running.notifyAll();
        }
    }

    /**
     * Stop the piece thread
     * Hint:
     * - Using {@link Archer#stopped}
     */
    @Override
    public void terminate() {
        //TODO
        stopped.set(true);
    }

    /**
     * The piece should be runnable
     * Consider the following situations:
     * - When it is NOT the turn of the player which this piece belongs to:
     * - this thread should be waiting ({@link Object#wait()})
     * - When it is the turn of the player which this piece belongs to (marked by {@link Archer#running}):
     * - take out the {@link Game} and {@link Place} objects from calculateMoveParametersQueue
     * - propose a candidate move (you may take advantage of {@link Archer#getAvailableMoves}
     * - if {@link this#behavior} is {@link Behavior#RANDOM}:
     * randomly pick one from {@link Archer#getAvailableMoves(Game, Place)}
     * - if {@link this#behavior} is {@link Behavior#GREEDY}:
     * come up with any strategy to pick one from {@link Archer#getAvailableMoves(Game, Place)}
     * - add to {@link Archer#candidateMoveQueue}
     * - When this piece has been stopped (marked by {@link Archer#stopped}): no more reaction
     * - Handle {@link InterruptedException}
     */
    @Override
    public void run() {
        //TODO
        while (true) {
            try {
                if (stopped.get()) {
                    return;
                }

                synchronized (running) {
                    while (!running.get()) {
                        running.wait();
                    }
                }
                // wait until it is time to calculate move
                var parameters = this.calculateMoveParametersQueue.take();
                var game = (Game) parameters[0];
                var source = (Place) parameters[1];
                var moves = this.getAvailableMoves(game, source);
                var rand = new Random();
                if (moves.length == 0) {
                    this.candidateMoveQueue.put(new InvalidMove());
                } else {
                    this.candidateMoveQueue.put(new MoveByBehavior(game, moves, this.behavior).getNextMove());
                }
            } catch (InterruptedException e) {
//                System.out.println("Piece is interrupted");
            }
        }
    }
}
