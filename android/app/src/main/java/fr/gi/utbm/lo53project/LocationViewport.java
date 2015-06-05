package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by celian on 05/05/15 for LO53Project
 */
public class LocationViewport extends AbstractViewport{

    /**
     * {@inheritDoc}
     * @param context
     * @param attrs
     */
    public LocationViewport(Context context, AttributeSet attrs) {
        super(context, attrs, null);
    }

    /**
     * {@inheritDoc}
     * @param context
     * @param attrs
     * @param map
     */
    public LocationViewport(Context context, AttributeSet attrs, WorldMap map) {
        super(context, attrs, map);
    }

    /**
     * {@inheritDoc}
     * @param canvas canvas
     */
    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        // Draw calibration squares
        mMap.drawSquares(canvas, Square.Type.CALIBRATION);

        // Draw location squares
        if (mMap.drawSquares(canvas, Square.Type.LOCATION))
            invalidate();
    }

}
