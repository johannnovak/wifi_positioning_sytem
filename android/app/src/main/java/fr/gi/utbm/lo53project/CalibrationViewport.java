package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by celian on 05/05/15 for LO53Project
 */
public class CalibrationViewport extends AbstractViewport {

    /**
     * {@inheritDoc}
     * @param context
     * @param attrs
     */
    public CalibrationViewport(Context context, AttributeSet attrs) {
        super(context, attrs, null);
    }

    /**
     * {@inheritDoc}
     * @param context
     * @param attrs
     * @param map
     * @param listener
     */
    public CalibrationViewport(Context context, AttributeSet attrs, WorldMap map, SelectionListener listener) {
        super(context, attrs, map, listener);

        mAddRowButton = new RectF();
        mAddColumnButton = new RectF();
        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.WHITE);

        ADD_ROW_HEIGHT = 150;
        ADD_COL_WIDTH = 150;
    }

    /**
     * {@inheritDoc}
     * @param canvas canvas
     */
    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        // Draw add row and add column buttons
        this.updateAddRowButton();
        this.updateAddColumnButton();
        canvas.drawRect(mAddRowButton, mButtonPaint);
        canvas.drawRect(mAddColumnButton, mButtonPaint);

        mMap.drawSquares(canvas, Square.Type.CALIBRATION);
        if (mMap.drawSquares(canvas, Square.Type.HOVER))
            invalidate();
    }

    /**
     * Update size of "add row" button
     */
    private void updateAddRowButton() {

        float map_left = mMap.getBounds().left;
        float map_right = mMap.getBounds().right;
        mAddRowButton.top = mMap.getBounds().height() ;
        mAddRowButton.bottom = mAddRowButton.top + fromViewToWorld(ADD_ROW_HEIGHT);

        mAddRowButton.left = (mViewportFrame.left < map_left) ? map_left : mViewportFrame.left;
        mAddRowButton.right = (mViewportFrame.right > map_right) ? map_right : mViewportFrame.right;
    }

    /**
     * Update size of "add column" button
     */
    private void updateAddColumnButton() {

        float map_top = mMap.getBounds().top;
        float map_bottom = mMap.getBounds().bottom;
        mAddColumnButton.left = mMap.getBounds().width() ;
        mAddColumnButton.right = mAddColumnButton.left + fromViewToWorld(ADD_COL_WIDTH);

        mAddColumnButton.top = (mViewportFrame.top < map_top) ? map_top : mViewportFrame.top;
        mAddColumnButton.bottom = (mViewportFrame.bottom > map_bottom) ? map_bottom : mViewportFrame.bottom;
    }

}
