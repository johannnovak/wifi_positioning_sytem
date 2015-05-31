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

    public CalibrationViewport(Context context, AttributeSet attrs) {
        super(context, attrs, null);
    }

    public CalibrationViewport(Context context, AttributeSet attrs, WorldMap map, SelectionListener listener) {
        super(context, attrs, map, listener);

        mAddRowButton = new RectF();
        mAddColumnButton = new RectF();
        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.WHITE);

        ADD_ROW_HEIGHT = 150;
        ADD_COL_WIDTH = 150;
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        // Draw add row and add column buttons
        this.updateAddRowButton();
        this.updateAddColumnButton();
        canvas.drawRect(mAddRowButton, mButtonPaint);
        canvas.drawRect(mAddColumnButton, mButtonPaint);

        mMap.drawPositions(canvas, Position.Type.CALIBRATION);
        if (mMap.drawPositions(canvas, Position.Type.HOVER))
            invalidate();
    }

    private void updateAddRowButton() {

        float map_view_width = fromWorldToView(mMap.getBounds().width());
        mAddRowButton.top = mMap.getBounds().height() ;
        mAddRowButton.bottom = mAddRowButton.top + fromViewToWorld(ADD_ROW_HEIGHT);

        if (map_view_width < mViewportFrame.width()) {
            mAddRowButton.left = 0;
            mAddRowButton.right = mAddRowButton.left + mMap.getBounds().width();
        }
        else {
            mAddRowButton.left = mViewportFrame.left + fromViewToWorld(OFFSET_X);
            mAddRowButton.right = mAddRowButton.left + fromViewToWorld(mViewportFrame.width()- 2*OFFSET_X);
        }
    }

    private void updateAddColumnButton() {

        float map_view_height = fromWorldToView(mMap.getBounds().height());
        mAddColumnButton.left = mMap.getBounds().width() ;
        mAddColumnButton.right = mAddColumnButton.left + fromViewToWorld(ADD_COL_WIDTH);

        if (map_view_height < mViewportFrame.height()) {
            mAddColumnButton.top = 0;
            mAddColumnButton.bottom = mAddColumnButton.top + mMap.getBounds().height();
        }
        else {
            mAddColumnButton.top = mViewportFrame.top + fromViewToWorld(OFFSET_Y);
            mAddColumnButton.bottom = mAddColumnButton.top + fromViewToWorld(mViewportFrame.height()- 2*OFFSET_Y);
        }
    }

}
