package slidingpuzzle.model;

import java.util.Objects;

/**
 * Describes a shape.
 * Most shapes on the board are U-shapes, aka open from 1 side.
 * While the block is open from no sides at all.
 * @param size the size of the shape, block being 0.
 * @param open the direction in which a U-shape is open, or in case of a block it is null.
 */
public record Shape(int size, Direction open) {

    /**
     * {@return whether the shape has a side in the supplied direction.}
     *
     * @param dir the direction
     */
    public boolean isClosedFrom(Direction dir) {
        return !Objects.equals(dir, this.open);
    }
}
