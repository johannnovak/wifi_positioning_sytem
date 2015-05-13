package fr.gi.utbm.lo53project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class WorldMap implements Serializable {

    private List<Position> mPositions;

    public WorldMap() {
        mPositions = new ArrayList<>();
    }

    /**
     * Add a position to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t position type
     */
    public void addPosition(float x, float y, Position.Type t) {
        mPositions.add(new Position(x, y, t));
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mPositions.clear();
    }

    /**
     * Get a list of positions
     * @return list of positions
     */
    public List<Position> getPositions () {
        return mPositions;
    }

    /**
     * Get a list of positions which are of type given
     * @param t type
     * @return list of positions of type t
     */
    public List<Position> getPositionsOfType (Position.Type t) {
        List<Position> ret = new ArrayList<>();
        for (Position p:mPositions) {
            if(p.type == t) {
                ret.add(p);
            }
        }
        return ret;
    }

    /**
     * Clear all position of type given
     * @param t type
     */
    @SuppressWarnings("unused")
    public void clearType(Position.Type t) {

        for (Position p:mPositions ) {
            if (p.type == t) {
                mPositions.remove(p);
            }
        }
    }


}
