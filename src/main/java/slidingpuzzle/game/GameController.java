package slidingpuzzle.game;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import jfxutils.images.ImageStorage;
import org.tinylog.Logger;

import slidingpuzzle.model.Direction;
import slidingpuzzle.model.PuzzleState;
import jfxutils.images.OrdinalImageStorage;

import java.util.Optional;

public class GameController {

    private static final ImageStorage<Integer> imageStorage = new OrdinalImageStorage(GameController.class,
            "block.png",
            "red-shoe.png",
            "blue-shoe.png",
            "black-shoe.png");

    @FXML
    private GridPane grid;

    @FXML
    private TextField numberOfMovesField;

    private JFXPuzzleState state;

    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);

    @FXML
    private void initialize() {
        bindNumberOfMoves();
        registerKeyEventHandler();
        restartGame();
    }

    private void bindNumberOfMoves() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
    }

    private void restartGame() {
        createState();
        numberOfMoves.set(0);
        clearAndPopulateGrid();
    }

    private void createState() {
        state = new JFXPuzzleState();
        state.solvedProperty().addListener(this::handleSolved);
    }

    private void handleSolved(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            Platform.runLater(this::showSolvedAlert);
        }
    }

    private void showSolvedAlert() {
        final var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("Congratulations, you have solved the puzzle!");
        alert.showAndWait();
        restartGame();
    }

    private void registerKeyEventHandler() {
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        final var restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        final var quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        if (restartKeyCombination.match(keyEvent)) {
            Logger.debug("Restarting game");
            restartGame();
        } else if (quitKeyCombination.match(keyEvent)) {
            Logger.debug("Exiting");
            Platform.exit();
        } else if (keyEvent.getCode() == KeyCode.UP) {
            Logger.debug("UP pressed");
            makeMoveIfLegal(Direction.UP);
        } else if (keyEvent.getCode() == KeyCode.RIGHT) {
            Logger.debug("RIGHT pressed");
            makeMoveIfLegal(Direction.RIGHT);
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            Logger.debug("DOWN pressed");
            makeMoveIfLegal(Direction.DOWN);
        } else if (keyEvent.getCode() == KeyCode.LEFT) {
            Logger.debug("LEFT pressed");
            makeMoveIfLegal(Direction.LEFT);
        }
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        final var source = (Node) event.getSource();
        final var row = GridPane.getRowIndex(source);
        final var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square ({},{})", row, col);
        getDirectionFromClick(row, col).ifPresentOrElse(this::makeMoveIfLegal,
                () -> Logger.warn("Click does not correspond to any of the directions"));
    }

    private void makeMoveIfLegal(Direction direction) {
        if (state.isLegalMove(direction)) {
            Logger.info("Moving in direction {}", direction);
            state.makeMove(direction);
            Logger.trace("New state after move: {}", state);
            numberOfMoves.set(numberOfMoves.get() + 1);
        } else {
            Logger.warn("Illegal move: {}", direction);
        }
    }

    private void clearAndPopulateGrid() {
        grid.getChildren().clear();
        for (var row = 0; row < grid.getRowCount(); row++) {
            for (var col = 0; col < grid.getColumnCount(); col++) {
                final var square = createSquare(row, col);
                grid.add(square, col, row);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        final var square = new StackPane();
        square.getStyleClass().add("square");
        square.getStyleClass().add((row + col) % 2 == 0 ? "light": "dark");
        for (var i = 0; i < 4; i++) {
            final var imageView = createImageViewForPieceOnPosition(i, row, col);
            square.getChildren().add(imageView);
        }
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private ImageView createImageViewForPieceOnPosition(int index, int row, int col) {
        final var imageView = new ImageView(imageStorage.get(index).orElseThrow());
        imageView.visibleProperty().bind(createBindingToCheckPieceIsOnPosition(index, row, col));
        return imageView;
    }

    private BooleanBinding createBindingToCheckPieceIsOnPosition(int index, int row, int col) {
        return new BooleanBinding() {
            {
                super.bind(state.positionProperty(index));
            }
            @Override
            protected boolean computeValue() {
                final var position = state.getPosition(index);
                return position.row() == row && position.col() == col;
            }
        };
    }

    private Optional<Direction> getDirectionFromClick(int row, int col) {
        final var positionOfBlock = state.getPosition(PuzzleState.BLOCK);
        try {
            return Optional.of(Direction.of(row - positionOfBlock.row(), col - positionOfBlock.col()));
        } catch (IllegalArgumentException e) {
            // The click does not correspond to any of the four directions
        }
        return Optional.empty();
    }

}