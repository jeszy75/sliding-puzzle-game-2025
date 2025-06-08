package slidingpuzzle.game;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;
import slidingpuzzle.model.Position;
import slidingpuzzle.model.PuzzleState;

/**
 * Extends the {@code PuzzleState} class by wrapping the positions of the pieces
 * in properties.
 */
public class JFXPuzzleState extends PuzzleState {

    private final ReadOnlyObjectWrapper<Position>[] positions;

    private final ReadOnlyBooleanWrapper solved;

    /**
     * Creates a {@code JFXPuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public JFXPuzzleState() {
        this(START);
    }

    /**
     * Creates a {@code JFXPuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * four {@code Position} objects or four {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public JFXPuzzleState(Position... positions) {
        super(positions);
        this.positions = new ReadOnlyObjectWrapper[4];
        initializePositions();
        solved = new ReadOnlyBooleanWrapper(isSolved());
    }

    private void initializePositions() {
        for (int i = 0; i < 4; i++) {
            positions[i] = new ReadOnlyObjectWrapper<>(getPosition(i));
            addChangeListener(i);
        }
    }

    /**
     * {@return a read-only property that wraps the position of the piece
     * specified}
     *
     * @param index the index of the piece
     */
    public ReadOnlyObjectProperty<Position> positionProperty(int index) {
        return positions[index].getReadOnlyProperty();
    }

    /**
     * {@return a read-only property that wraps whether the puzzle is solved}
     */
    public ReadOnlyBooleanProperty solvedProperty() {
        return solved.getReadOnlyProperty();
    }

    @Override
    protected void setPosition(int index, Position position) {
        super.setPosition(index, position);
        positions[index].set(position);
        if (isSolved()) {
            solved.set(true);
        }
    }

    private void addChangeListener(int i) {
        positions[i].addListener((observableValue, oldValue, newValue) -> Logger.trace("positions[{}] is changed from {} to {}", i, oldValue, newValue));
    }

}
