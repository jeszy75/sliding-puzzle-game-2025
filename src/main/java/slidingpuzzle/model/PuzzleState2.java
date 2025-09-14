package slidingpuzzle.model;

import puzzle.State;

import java.util.*;
import java.util.stream.IntStream;

import static slidingpuzzle.model.Direction.*;

/**
 * Represents the state of the puzzle.
 * <br>
 * The PuzzleState2 could be refactored to support arbitrary board sizes, shape counts and shape kinds.
 * The algorithm itself is general, you just need to change the underlying data.
 * The core concepts which enable this are:
 * <ul>
 *     <li>the size and orientation of U-blocks is modeled too (besides the positions),</li>
 *     <li>for collision detection you only need to consider the greatest pushed element (aka it has a side in the direction of movement),</li>
 *     <li>for determining what is going to be 'dragged' with the square,
 *         you only need one auxiliary thing: the greatest pushed shape
 *         (aka it has a side in the direction of movement),
 *         which can also pull other shapes (aka it has a side in the opposite of the direction of movement)</li>
 * </ul>
 * In short: Make the shapes, positions and board-size freely parameterizable, then you're good to go,
 * (aka get rid of statics).
 */
public class PuzzleState2 implements State<Direction> {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 3;

    /**
     * The index of the block.
     */
    public static final int BLOCK = 0;

    /**
     * The index of the red shoe.
     */
    public static final int RED_SHOE = 1;

    /**
     * The index of the blue shoe.
     */
    public static final int BLUE_SHOE = 2;

    /**
     * The index of the black shoe.
     */
    public static final int BLACK_SHOE = 3;

    /**
     * The block's and the U-shapes' model.
     * U-shapes have a size and an open side.
     * The block has size 0 and no open side.
     * This hashmap is used by the algorithmic solution (could be an array too).
     */
    public static final Map<Integer, Shape> SHAPES = Map.of(
            BLOCK, new Shape(0, null),
            RED_SHOE, new Shape(1, UP),
            BLUE_SHOE, new Shape(2, UP),
            BLACK_SHOE, new Shape(2, LEFT)
    );

    protected static final Position[] START = {
            new Position(0, 0),
            new Position(2, 0),
            new Position(1, 1),
            new Position(0, 2)
    };

    private Position[] positions;

    /**
     * Creates a {@code PuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public PuzzleState2() {
        this(START);
    }

    /**
     * Creates a {@code PuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * four {@code Position} objects or four {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public PuzzleState2(Position... positions) {
        checkPositions(positions);
        this.positions = positions.clone();
    }

    private void checkPositions(Position[] positions) {
        if (positions.length != 4) {
            throw new IllegalArgumentException();
        }
        for (var position : positions) {
            if (isOutOfBounds(position)) {
                throw new IllegalArgumentException();
            }
        }
        if (positions[BLUE_SHOE].equals(positions[BLACK_SHOE])) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param index the number of a piece
     */
    public Position getPosition(int index) {
        return positions[index];
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isSolved() {
        return Objects.equals(positions[RED_SHOE], positions[BLUE_SHOE]);
    }

    /**
     * {@return whether the block can be moved in the direction specified}
     *
     * @param dir a direction in which the block is intended to be moved
     */
    public boolean isLegalMove(Direction dir) {
        final var opp = dir.opposite();
        final var beg = positions[BLOCK];
        final var end = beg.move(dir);
        if (isOutOfBounds(end)) return false;
        final int biggestPushed = IntStream.range(0, positions.length)
                .filter(i -> beg.equals(positions[i]))
                .mapToObj(SHAPES::get)
                .filter(s -> s.isClosedFrom(dir))
                .mapToInt(Shape::size)
                .max()
                .orElse(0);
        final var biggestHole = IntStream.range(0, positions.length)
                .filter(i -> end.equals(positions[i]))
                .mapToObj(SHAPES::get)
                .mapToInt(s -> s.isClosedFrom(opp) ? 0 : s.size())
                .max()
                .orElse(biggestPushed + 1);
        return biggestPushed < biggestHole;
    }

    /**
     * Moves the block in the direction specified.
     *
     * @param dir the direction in which the block is moved
     */
    public void makeMove(Direction dir) {
        final var opp = dir.opposite();
        final var beg = positions[BLOCK];
        final var end = beg.move(dir);
        final int biggestPuller = IntStream.range(0, positions.length)
                .filter(i -> beg.equals(positions[i]))
                .mapToObj(SHAPES::get)
                .filter(s -> s.isClosedFrom(dir) && s.isClosedFrom(opp))
                .mapToInt(Shape::size)
                .max()
                .orElse(0);
        IntStream.range(0, positions.length)
                .filter(i -> beg.equals(positions[i]))
                .filter(i -> {
                    final var s = SHAPES.get(i);
                    return i == BLOCK || s.isClosedFrom(dir) || s.size() < biggestPuller;
                }).forEach(i -> setPosition(i, end));
    }

    /**
     * Sets the position of the piece specified. This method is intended to be
     * overridden by subclasses.
     *
     * @param index the index of the piece
     * @param position the new position of the piece
     */
    protected void setPosition(int index, Position position) {
        positions[index] = position;
    }

    /**
     * {@return the set of directions in which the block can be moved}
     */
    public Set<Direction> getLegalMoves() {
        final var legalMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (isLegalMove(direction)) {
                legalMoves.add(direction);
            }
        }
        return legalMoves;
    }

    private boolean isOutOfBounds(Position position) {
        return position.row() < 0 || BOARD_SIZE <= position.row() ||
                position.col() < 0 || BOARD_SIZE <= position.col();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof PuzzleState2 other) && Arrays.equals(positions, other.positions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(positions);
    }

    @Override
    public PuzzleState2 clone() {
        try {
            final PuzzleState2 copy = (PuzzleState2) super.clone();
            copy.positions = positions.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        final var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
            sj.add(position.toString());
        }
        return sj.toString();
    }

}
