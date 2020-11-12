package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Game configuration, including:
 * 1. size of gameboard
 * 2. place (square) of central square in classical Jeson Mor
 * 3. the initial game board with pieces on it, which is configurable through {@link Configuration#addInitialPiece(Piece, Place)}
 * 4. the two players.
 */
public class Configuration implements Cloneable {
    /**
     * Set default size to 9.
     * Set default numMovesProtection to 3
     */
    protected final static int DEFAULTSIZE = 9;
    protected final static int DEFAULTPROTECTMOVE = 1;

    /**
     * Size of gameboard.
     * The gameboard has equal size in width and height.
     * The size should be an odd number.
     */
    protected int size;

    /**
     * The size of critical region.
     * Note that the size refers to the "height" of the region.
     * The width of the region is always equal to the size of game board.
     */
    protected int criticalRegionSize;

    /**
     * The maximum number of knights of each player that are allowed to be inside critical region.
     */
    protected int criticalRegionCapacity;

    /**
     * Mapping from piece object to thread object of the piece.
     */
    protected Map<Piece, Thread> pieceThreadMap;

    /**
     * All players in the game.
     */
    protected Player[] players;

    /**
     * The initial map of the gameboard, containing initial pieces and their places.
     * This map has keys for all places in the gameboard, with or without pieces.
     * If there is no piece in one place, the place is mapped to null.
     */
    protected Piece[][] initialBoard;

    /**
     * The central square of the gameboard.
     */
    protected Place centralPlace;

    protected int numMovesProtection;

    /**
     * Add configuration validation for critical region
     * - If {@link this#criticalRegionSize} is smaller than 1, throw {@link InvalidConfigurationError}
     *      states "critical region size of gameboard must be at least 1"
     * - If {@link this#criticalRegionSize} is an even number, throw {@link InvalidConfigurationError}
     *      states "critical region size of gameboard must be an odd number"
     * - If {@link this#criticalRegionSize} is mover than board size - 2, throw {@link InvalidConfigurationError}
     *      states "critical region size of gameboard is at most size of gameboard - 2"
     * - If {@link this#criticalRegionSize} is larger than board size or smaller than 1,
     *      throw {@link InvalidConfigurationError}
     *      states "capacity of critical region size for each player of gameboard is at least 1 and at most size
     *      of game board"
     */
    public void validateConfiguration() {
        // validate size
        if (size < 3) {
            throw new InvalidConfigurationError("size of gameboard must be at least 3");
        }
        if (size % 2 != 1) {
            throw new InvalidConfigurationError("size of gameboard must be an odd number");
        }
        if (size > 26) {
            throw new InvalidConfigurationError("size of gameboard is at most 26");
        }

        //TODO
        // validate {@link this#criticalRegionSize} here

        //validate number of players
        if (players.length != 2) {
            throw new InvalidConfigurationError("there must be exactly two players");
        }

        //validate numMovesProtection, cannot be negative
        if (numMovesProtection < 0) {
            throw new InvalidConfigurationError("number of moves with capture protection cannot be negative");
        }
    }

    /**
     * Constructor of configuration
     *
     * @param size size of the game board.
     */
    public Configuration(int size, Player[] players, int numMovesProtection, int criticalRegionSize, int criticalRegionCapacity) {
        this.size = size;
        this.criticalRegionSize = criticalRegionSize;
        this.criticalRegionCapacity = criticalRegionCapacity;
        this.pieceThreadMap = new HashMap<>();
        // We only have 2 players
        this.players = players;

        this.numMovesProtection = numMovesProtection;

        if (players.length != 2) {
            throw new InvalidConfigurationError("there must be exactly two players");
        }
        // initialize map of the game board by putting every place null (meaning no piece)
        this.initialBoard = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.initialBoard[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.initialBoard[x][y] = null;
            }
        }
        // calculate the central place
        this.centralPlace = new Place(size / 2, size / 2);
        this.validateConfiguration();
    }

    public Configuration(int size, Player[] players, int numMovesProtection) {
        this(size, players, numMovesProtection, 1, size);
    }

    public Configuration(int size, Player[] players) {
        this(size, players, 0);
        this.validateConfiguration();
    }

    public Configuration(Player[] players) {
        this(Configuration.DEFAULTSIZE, players, Configuration.DEFAULTPROTECTMOVE);
        this.validateConfiguration();
    }

    public Configuration() {
        this.size = DEFAULTSIZE;
        this.numMovesProtection = DEFAULTPROTECTMOVE;
        Player whitePlayer = new ComputerPlayer("White");
        Player blackPlayer = new ComputerPlayer("Black");
        this.players = new Player[]{whitePlayer, blackPlayer};
    }

    /**
     * Add piece to the initial game board.
     * The player that this piece belongs to will be automatically added into the configuration.
     *
     * - create a thread for this piece
     * - start the thread
     * Hint:
     *      remember to record this thread to {@link Configuration#pieceThreadMap}
     * @param piece piece to be added
     * @param place place to put the piece
     */
    public void addInitialPiece(Piece piece, Place place) {

        if (!piece.getPlayer().equals(this.players[0]) && !piece.getPlayer().equals(this.players[1])) {
            throw new InvalidConfigurationError("the player of the piece is unknown");
        }
        if (place.x() >= this.size || place.y() >= this.size) {
            // The place must be inside the game board
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the game board");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }

        // put the piece on the initial board
        this.initialBoard[place.x()][place.y()] = piece;

        // TODO
        // start piece thread and update {@link Configuration#pieceThreadMap} here
    }

    public void addInitialPiece(Piece piece, int x, int y) {
        this.addInitialPiece(piece, new Place(x, y));
    }

    public int getSize() {
        return size;
    }

    public int getCriticalRegionSize() {
        return criticalRegionSize;
    }

    public int getCriticalRegionCapacity() {
        return criticalRegionCapacity;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Piece[][] getInitialBoard() {
        return initialBoard;
    }

    public Place getCentralPlace() {
        return centralPlace;
    }

    public int getNumMovesProtection() {
        return numMovesProtection;
    }

    public Map<Piece, Thread> getPieceThreadMap() {
        return pieceThreadMap;
    }

    public Thread getPieceThread(Piece piece) {
        return pieceThreadMap.get(piece);
    }

    public List<Thread> getAllThreads() {
        return this.pieceThreadMap.entrySet().parallelStream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public Configuration clone() throws CloneNotSupportedException {
        var cloned = (Configuration) super.clone();
        var playerCloneMap = new HashMap<Player, Player>();
        for (var player :
                cloned.getPlayers()) {
            playerCloneMap.put(player, player.clone());
        }
        cloned.players = playerCloneMap.values().toArray(new Player[0]);
        for (int i = 0; i < this.players.length; i++) {
            cloned.players[i] = this.players[i].clone();
        }
        cloned.initialBoard = this.initialBoard.clone();
        for (int i = 0; i < this.size; i++) {
            cloned.initialBoard[i] = this.initialBoard[i].clone();
            System.arraycopy(this.initialBoard[i], 0, cloned.initialBoard[i], 0, this.size);
            for (int j = 0; j < this.size; j++) {
                // update the cloned player
                if (cloned.initialBoard[i][j] != null)
                    cloned.initialBoard[i][j].player = playerCloneMap.get(this.initialBoard[i][j].player);
            }
        }
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNumMovesProtection(int numMovesProtection) {
        this.numMovesProtection = numMovesProtection;
    }

    public boolean isWhitePlayerHuman() {
        return players[0] instanceof ConsolePlayer;
    }

    public boolean isBlackPlayerHuman() {
        return players[1] instanceof ConsolePlayer;
    }

    public void setWhitePlayer(boolean isHuman) {
        if (isHuman) {
            players[0] = new ConsolePlayer("White");
        } else {
            players[0] = new ComputerPlayer("White");
        }
    }

    public void setBlackPlayer(boolean isHuman) {
        if (isHuman) {
            players[1] = new ConsolePlayer("Black");
        } else {
            players[1] = new ComputerPlayer("Black");
        }
    }

    public void setAllInitialPieces() {
        Player whitePlayer = this.getPlayers()[1];
        Player blackPlayer = this.getPlayers()[0];

        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                this.addInitialPiece(new Knight(whitePlayer), i, size - 1);
            } else {
                this.addInitialPiece(new Archer(whitePlayer), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                this.addInitialPiece(new Knight(blackPlayer), i, 0);
            } else {
                this.addInitialPiece(new Archer(blackPlayer), i, 0);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("size:%d\nnumMovesProtection:%d\ncentralPlace:%s\nnumPlayers:%d\n\n#Player info\n#player1:\n%s\n#player2:\n%s",
                this.getSize(),
                this.getNumMovesProtection(),
                this.getCentralPlace().toString(),
                this.getPlayers().length,
                this.getPlayers()[0].toString(),
                this.getPlayers()[1].toString());
    }
}
