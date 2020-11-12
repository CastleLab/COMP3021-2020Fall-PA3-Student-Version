package castle.comp3021.assignment.player;

import castle.comp3021.assignment.action.*;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.ActionException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * The player that makes move according to user input from console.
 */
public class ConsolePlayer extends HumanPlayer {
    public ConsolePlayer(String name, Color color) {
        super(name, color);
    }

    public ConsolePlayer(String name) {
        this(name, Color.GREEN);
    }


    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        var in = new Scanner(System.in);
        Move move;
        while (true) {
            System.out.printf("[%s] Make a Move/Action: ", game.getCurrentPlayer().getName());
            var input = in.nextLine().toLowerCase();

            var action = parseAction(game, input);
            if (action != null) {
                try {
                    action.perform();
                } catch (ActionException e) {
                    System.out.println("[Invalid Action]: " + e.getMessage());
                    continue;
                }
                System.out.println(action.toString() + " performed");
            } else {
                move = parseMove(input);
                if (move != null) {
                    var error = validateMove(game, move);
                    if (error != null) {
                        System.out.println("[Invalid Move]: " + error);
                        continue;
                    }
                    var piece = game.getPiece(move.getSource());
                    if (piece == null) {
                        System.out.println("[Invalid Move]: no piece at place " + move.getSource());
                        continue;
                    }
                    if (!piece.getPlayer().equals(this)) {
                        System.out.println("[Invalid Move]: cannot move a piece that does not belong to you");
                        continue;
                    }
                    break;
                }
                System.out.println("[Invalid Move]: Incorrect format");
            }
        }
        return move;
    }

    /**
     * Given a {@link Game} object and the input string, return {@link Action} if it is a valid action string
     * Valid input action strings:
     * - undo:
     *      - format: undo
     *      - description: undo till the human player's move
     *      - implemented in {@link Game#undo()}
     * - history:
     *      - format: history
     *      - description: show history
     *      - implemented in {@link Game#showHistoryMove()}
     * - pause:
     *      - format: pause {@link Place} (example: pause a1)
     *      - description: pause a piece at the given place
     *      - implemented in {@link Piece#pause()}
     * - resume:
     *      - format: resume {@link Place} (example: resume a1)
     *      - description: resume a piece the given place
     *      - implemented in {@link Piece#resume()}
     * - stop:
     *      - format: terminate {@link Place} (example: terminate a1)
     *      - description: terminates a piece the given place, once terminates, that piece cannot be resumed again
     *      - implemented in {@link Piece#terminate()}
     *
     * @param game {@link Game} object
     * @param str  The input from console, options include: undo, pause, resume and terminate. Not necessarily be an
     *             action.
     * @return {@link Action} or null
     */
    private static Action parseAction(Game game, String str) {
        str = str.strip();
        var segments = Arrays.stream(str.split(" ")).filter(s -> s.length() > 0).collect(Collectors.toList());

        // args starts after the actional keyword, e.g., the input string "pause a1", args = "a1"
        var args = segments.subList(1, segments.size()).toArray(new String[]{});
        switch (segments.get(0)) {
            case "undo" -> {
                return new UndoAction(game, args);
            }
            case "history" -> {
                return new ShowHistoryAction(game, args);
            }
            case "pause" -> {
                return new PausePieceAction(game, args);
            }
            case "resume" -> {
                return new ResumePieceAction(game, args);
            }
            case "terminate" -> {
                return new TerminatePieceAction(game, args);
            }
        }
        return null;
    }

    public static Place parsePlace(String str) {
        if (str.length() < 2) {
            return null;
        }
        try {
            var x = str.charAt(0) - 'a';
            var y = Integer.parseInt(str.substring(1)) - 1;
            return new Place(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Move parseMove(String str) {
        var segments = str.split("->");
        if (segments.length < 2) {
            return null;
        }
        var source = parsePlace(segments[0].strip());
        if (source == null) {
            return null;
        }
        var destination = parsePlace(segments[1].strip());
        if (destination == null) {
            return null;
        }
        return new Move(source, destination);
    }
}









