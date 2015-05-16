package fr.gi.utbm.lo53project;

import android.graphics.PointF;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class Position extends PointF {

    public enum Type {
        LOCATION,
        CALIBRATION
    }

    public Position(float x, float y) {
        super(x, y);
    }

}