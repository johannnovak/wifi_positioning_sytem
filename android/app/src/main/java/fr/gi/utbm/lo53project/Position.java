package fr.gi.utbm.lo53project;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class Position extends PointF {

    public enum Type {
        LOCATION,
        CALIBRATION
    }
    public static final Map<Type , Integer> COLORS = new HashMap<Type , Integer>() {{
        put(Position.Type.LOCATION,      Color.WHITE);
        put(Position.Type.CALIBRATION,   Color.BLACK);
    }};

    // Attributes ( we can add others )
    public int color;
    public Type type;

    public Position(float x, float y, Type t) {
        super(x, y);
        type = t;
        color = COLORS.get(t);
    }

}