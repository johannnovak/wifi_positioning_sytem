package fr.gi.utbm.lo53project;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by celian on 19/05/15 for LO53Project
 */
public class SRectF extends RectF implements Serializable {
    public SRectF (int left, int top, int right, int bottom ) {
        super(left, top, right, bottom);
    }
}
