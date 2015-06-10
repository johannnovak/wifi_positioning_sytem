package fr.gi.utbm.lo53project;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by celian on 19/05/15 for LO53Project
 */
public class SRectF extends RectF implements Serializable {

    public SRectF () {
        super();
    }
    public SRectF (float left, float top, float right, float bottom ) {
        super(left, top, right, bottom);
    }

    public boolean containsX(RectF bounds) {
        return (bounds.left >= left && bounds.right <= right );
    }

    public boolean containsX(float l, float r) {
        return (l >= left && r <= right );
    }

    public boolean containsY(RectF bounds) {
        return (bounds.top >= top - 5 && bounds.bottom <= bottom + 5);
    }

    public boolean containsY(float t, float b) {
//        System.out.println( t + ">= " + top + ";" + b + "<=" + bottom);
        return (t >= top -1 && b <= bottom  +1);
    }
}
