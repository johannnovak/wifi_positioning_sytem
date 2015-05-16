package fr.gi.utbm.lo53project;

import android.graphics.Point;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class Position extends Point {

    public enum Type {
        HOVER,
        LOCATION,
        CALIBRATION
    }

    public int life;

    public Position(int x, int y) {
        super(x, y);
        life = 255;
    }

    public boolean equals (Position p) {
        return (x == p.x && y == p.y);
    }

}