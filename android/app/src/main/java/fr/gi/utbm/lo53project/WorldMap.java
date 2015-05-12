package fr.gi.utbm.lo53project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by celian on 29/04/15.
 */
public class WorldMap implements Serializable {

    private List<Position> mPositions;

    public WorldMap() {
        mPositions = new ArrayList<>();
    }

    public void addPosition(float x, float y, Position.Type t) {
        mPositions.add(new Position(x, y, t));
    }

    public void clearAll() {
        mPositions.clear();
    }

    public List<Position> getPositions () {
        return mPositions;
    }

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
     * Not good
     * @param t
     */
    public void clearType(Position.Type t) {

        for (Position p:mPositions ) {
            if (p.type == t) {
                mPositions.remove(p);
            }
        }
    }


}
