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
    public static int LIFE_MAX = 255;
    public static int LIFE_SPEED = 10;
    private boolean bDead;

    public Position(int x, int y) {
        super(x, y);
        life = LIFE_MAX;
        bDead = false;
    }

    public boolean equals (Position p) {
        return (x == p.x && y == p.y);
    }

    public void decreaseLife () {
        life = (life - LIFE_SPEED < 0) ? 0 : life - LIFE_SPEED;
        if (life == 0) bDead = true;
    }

    public void recoverLife() {
        life = LIFE_MAX;
    }

    public boolean isDead() {
        return bDead;
    }

}