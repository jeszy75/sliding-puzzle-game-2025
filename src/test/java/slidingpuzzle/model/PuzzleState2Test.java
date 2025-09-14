package slidingpuzzle.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static slidingpuzzle.model.PuzzleState.*;

public class PuzzleState2Test {

    PuzzleState2 state1 = new PuzzleState2(); // the original initial state

    PuzzleState2 state2 = new PuzzleState2(new Position(1, 1),
            new Position(1, 1),
            new Position(1, 1),
            new Position(1, 2)); // a goal state

    PuzzleState2 state3 = new PuzzleState2(new Position(1, 1),
            new Position(2, 0),
            new Position(1, 1),
            new Position(0, 2)); // a non-goal state

    PuzzleState2 state4 = new PuzzleState2(new Position(0, 0),
            new Position(1, 0),
            new Position(0, 1),
            new Position(0, 0)); // a dead-end state with no legal moves

    @Test
    void constructor() {
        var positions = new Position[] {
                new Position(0, 0),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)
        };
        PuzzleState2 state = new PuzzleState2(positions);
        for (var i = 0; i < 4; i++) {
            assertEquals(positions[i], state.getPosition(i));
        }
    }

    @Test
    void constructor_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState2(new Position(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState2(new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2),
                new Position(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState2(new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1)));
    }

    @Test
    void isSolved() {
        assertFalse(state1.isSolved());
        assertTrue(state2.isSolved());
        assertFalse(state3.isSolved());
        assertFalse(state4.isSolved());
    }

    @Test
    void isLegalMove_state1() {
        assertFalse(state1.isLegalMove(Direction.UP));
        assertTrue(state1.isLegalMove(Direction.RIGHT));
        assertTrue(state1.isLegalMove(Direction.DOWN));
        assertFalse(state1.isLegalMove(Direction.LEFT));
    }

    @Test
    void isLegalMove_state2() {
        assertTrue(state2.isLegalMove(Direction.UP));
        assertFalse(state2.isLegalMove(Direction.RIGHT));
        assertTrue(state2.isLegalMove(Direction.DOWN));
        assertTrue(state2.isLegalMove(Direction.LEFT));
    }

    @Test
    void isLegalMove_state3() {
        assertTrue(state3.isLegalMove(Direction.UP));
        assertTrue(state3.isLegalMove(Direction.RIGHT));
        assertTrue(state3.isLegalMove(Direction.DOWN));
        assertTrue(state3.isLegalMove(Direction.LEFT));
    }

    @Test
    void isLegalMove_state4() {
        assertFalse(state4.isLegalMove(Direction.UP));
        assertFalse(state4.isLegalMove(Direction.RIGHT));
        assertFalse(state4.isLegalMove(Direction.DOWN));
        assertFalse(state4.isLegalMove(Direction.LEFT));
    }

    @Test
    void makeMove_right_state1() {
        var stateBeforeMove = state1.clone();
        state1.makeMove(Direction.RIGHT);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveRight(), state1.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state1.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE), state1.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state1.getPosition(BLACK_SHOE));
    }

    @Test
    void makeMove_down_state1() {
        var stateBeforeMove = state1.clone();
        state1.makeMove(Direction.DOWN);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveDown(), state1.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state1.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE), state1.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state1.getPosition(BLACK_SHOE));
    }

    @Test
    void makeMove_up_state3() {
        var stateBeforeMove = state3.clone();
        state3.makeMove(Direction.UP);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveUp(), state3.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state3.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE), state3.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state3.getPosition(BLACK_SHOE));
    }

    @Test
    void makeMove_right_state3() {
        var stateBeforeMove = state3.clone();
        state3.makeMove(Direction.RIGHT);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveRight(), state3.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state3.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE).moveRight(), state3.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state3.getPosition(BLACK_SHOE));
    }

    @Test
    void makeMove_down_state3() {
        var stateBeforeMove = state3.clone();
        state3.makeMove(Direction.DOWN);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveDown(), state3.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state3.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE).moveDown(), state3.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state3.getPosition(BLACK_SHOE));
    }

    @Test
    void makeMove_left_state3() {
        var stateBeforeMove = state3.clone();
        state3.makeMove(Direction.LEFT);
        assertEquals(stateBeforeMove.getPosition(BLOCK).moveLeft(), state3.getPosition(BLOCK));
        assertEquals(stateBeforeMove.getPosition(RED_SHOE), state3.getPosition(RED_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLUE_SHOE).moveLeft(), state3.getPosition(BLUE_SHOE));
        assertEquals(stateBeforeMove.getPosition(BLACK_SHOE), state3.getPosition(BLACK_SHOE));
    }

    @Test
    void getLegalMoves() {
        assertEquals(EnumSet.of(Direction.DOWN, Direction.RIGHT), state1.getLegalMoves());
        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT), state2.getLegalMoves());
        assertEquals(EnumSet.allOf(Direction.class), state3.getLegalMoves());
        assertEquals(EnumSet.noneOf(Direction.class), state4.getLegalMoves());
    }

    @Test
    void testEquals() {
        assertTrue(state1.equals(state1));

        var clone = state1.clone();
        clone.makeMove(Direction.RIGHT);
        assertFalse(clone.equals(state1));

        assertFalse(state1.equals(null));
        assertFalse(state1.equals("Hello, World!"));
        assertFalse(state1.equals(state2));
    }

    @Test
    void testHashCode() {
        assertTrue(state1.hashCode() == state1.hashCode());
        assertTrue(state1.hashCode() == state1.clone().hashCode());
    }

    @Test
    void testClone() {
        var clone = state1.clone();
        assertTrue(clone.equals(state1));
        assertNotSame(clone, state1);
    }

    @Test
    void testToString() {
        assertEquals("[(0,0),(2,0),(1,1),(0,2)]", state1.toString());
        assertEquals("[(1,1),(1,1),(1,1),(1,2)]", state2.toString());
        assertEquals("[(1,1),(2,0),(1,1),(0,2)]", state3.toString());
        assertEquals("[(0,0),(1,0),(0,1),(0,0)]", state4.toString());
    }

    /**
     * This test is intentionally disabled.
     * Enable it if you want to run a comparison of PuzzleState vs. PuzzleState2 on ALL valid configurations.
     */
    @ParameterizedTest
    @Disabled
    @MethodSource("allValidConfigurations")
    void puzzleState2ShouldBeEqualToTheOriginalImplementationInAllPossibleConfigurationsOfA3by3Grid(Position block,
                                      Position red,
                                      Position blue,
                                      Position black,
                                      Direction dir) {
        PuzzleState original = new PuzzleState(block, red, blue, black);
        PuzzleState2 refactored = new PuzzleState2(block, red, blue, black);
        assertEquals(original.toString(), refactored.toString());
        assertEquals(original.isLegalMove(dir), refactored.isLegalMove(dir));
        if (original.isLegalMove(dir)) {
            original.makeMove(dir);
            refactored.makeMove(dir);
        }
        assertEquals(original.toString(), refactored.toString());
    }

    static Stream<Arguments> allValidConfigurations() {
        final var builder = Stream.<Arguments>builder();
        final var positions = new Position[9];
        int k = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                positions[k++] = new Position(r, c);
            }
        }
        for (var dir: new Direction[]{Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT}) {
            for (var block: positions) {
                for (var red: positions) {
                    for (var black: positions) {
                        for (var blue: positions) {
                            if (!blue.equals(black)) {
                                builder.add(Arguments.of(block, red, black, blue, dir));
                            }
                        }
                    }
                }
            }
        }
        return builder.build();
    }
}
