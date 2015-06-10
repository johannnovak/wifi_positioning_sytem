package fr.gi.utbm.lo53project;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class Square extends Point implements Serializable {

    /**
     * Square type enumeration
     */
    public enum Type {
        HOVER,
        LOCATION,
        CALIBRATION
    }

    /**
     * Life parameters
     */
    public int life;
    public static int LIFE_MAX = 255;
    public static int LIFE_SPEED = 10;
    private boolean bDead;
    private boolean bImmortal;

    /**
     * Build a square from its indices in the map
     * @param x row index
     * @param y column index
     */
    public Square(int x, int y) {
        super(x, y);
        life = LIFE_MAX;
        bDead = false;
        bImmortal = false;
    }

    /**
     * Say if square equals another
     * @param s square to test
     * @return result of equality
     */
    @SuppressWarnings("unused")
    public boolean equals (Square s) {
        return (x == s.x && y == s.y);
    }

    /**
     * Decrease life it the square is mortal. Inversely, an immortal square will totally recover his
     * life.
     * If the life is equal to zero, the square is dead
     */
    public void decreaseLife () {
        if (!bImmortal)
            life = (life - LIFE_SPEED < 0) ? 0 : life - LIFE_SPEED;
        else {
            recoverLife();
        }
        if (life == 0) bDead = true;
    }

    /**
     * Recover the maximum of life
     */
    public void recoverLife() {
        life = LIFE_MAX;
    }

    /**
     * @return true if the square is dead
     */
    public boolean isDead() {
        return bDead;
    }

    /**
     * @return the square as a string
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Set the immortality attribute
     * @param immortality immortal or not (respectively true or false)
     */
    public void setImmortality( boolean immortality) {
        bImmortal = immortality;
    }

    /**
     * @return true it the square is immortal
     */
    @SuppressWarnings("unused")
    public boolean isImmortal () {
        return bImmortal;
    }

}