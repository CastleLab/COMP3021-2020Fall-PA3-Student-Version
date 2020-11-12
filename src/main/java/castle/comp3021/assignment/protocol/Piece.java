package castle.comp3021.assignment.protocol;


/**
 * The abstract class that a game piece should extend.
 */
public abstract class Piece implements Runnable {
    protected Behavior behavior;
    /**
     * The player that owns this piece.
     */
    Player player;

    public Piece(Player player, Behavior behavior) {
        this.player = player;
        this.behavior = behavior;
    }

    public Piece(Player player) {
        this(player, Behavior.RANDOM);
    }

    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Returns a char which is used to facilitate output in the console
     *
     * @return the label to represent the piece on the board
     */
    public abstract char getLabel();

    /**
     * Returns a set of moves that are valid to make given the current place of the piece.
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return a set of available moves
     */
    public abstract Move[] getAvailableMoves(Game game, Place source);

    public abstract Move getCandidateMove(Game game, Place source);

    public abstract void pause();

    public abstract void resume();

    public abstract void terminate();

}
