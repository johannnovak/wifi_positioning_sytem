package fr.gi.utbm.lo53project;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class Square extends Point implements Serializable {

    public enum Type {
        HOVER,
        LOCATION,
        CALIBRATION
    }

    public int life;
    public static int LIFE_MAX = 255;
    public static int LIFE_SPEED = 10;
    private boolean bDead;
    private boolean bImmortal;

    public Square(int x, int y) {
        super(x, y);
        life = LIFE_MAX;
        bDead = false;
        bImmortal = false;
    }

    public boolean equals (Square p) {
        return (x == p.x && y == p.y);
    }

    public void decreaseLife () {
        if (!bImmortal)
            life = (life - LIFE_SPEED < 0) ? 0 : life - LIFE_SPEED;
        else {
            recoverLife();
        }
        if (life == 0) bDead = true;
    }

    public void recoverLife() {
        life = LIFE_MAX;
    }

    public boolean isDead() {
        return bDead;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public void setImmortality( boolean immortality) {
        bImmortal = immortality;
    }
    public boolean isImmortal () {
        return bImmortal;
    }

}