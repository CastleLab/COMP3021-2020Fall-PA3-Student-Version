package castle.comp3021.assignment.protocol;

import java.util.Objects;

/**
 * A square (position, place) in the gameboard.
 * Represented by coordinates a 2-D coordinate system.
 * <p>
 * x and y coordinates of a place on gameboard are two fields of this class.
 */
public class Place implements Cloneable {
    /**
     * x coordinate
     */
    private final int x;

    /**
     * y coordinate
     */
    private final int y;

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /* Getters start */
    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
    /* Getters end */


    /* Object methods start */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return x == place.x &&
                y == place.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }

    /**
     * Converting a place to string
     * @return a string of place in form of (%d, %d)
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }
}