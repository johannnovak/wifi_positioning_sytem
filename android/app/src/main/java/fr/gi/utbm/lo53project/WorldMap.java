package fr.gi.utbm.lo53project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by celian on 29/04/15 for LO53Project
 */
public class WorldMap implements Serializable {

    private List<Position> mPositions;
    private Paint mCalibrationPaint;
    private Paint mLocationPaint;
    private Paint mGridPaint;

    private RectF mBounds;

    private static int GRID_WIDTH = 10;
    private static int GRID_HEIGHT = 10;

    public Map<Position.Type , Paint> paints;

    public WorldMap() {
        mPositions = new ArrayList<>();

        mBounds = new RectF(0, 0, 2000, 2000);

        // Paint dedicated to calibration points
        mCalibrationPaint  = new Paint();
        mCalibrationPaint.setAntiAlias(true);
        mCalibrationPaint.setStyle(Paint.Style.STROKE);
        mCalibrationPaint.setStrokeJoin(Paint.Join.ROUND);
        mCalibrationPaint.setStrokeWidth(20f);
        mCalibrationPaint.setColor(Color.BLACK);

        // Paint dedicated to calibration points
        mLocationPaint = new Paint();
        mLocationPaint.setAntiAlias(true);
        mLocationPaint.setStyle(Paint.Style.STROKE);
        mLocationPaint.setStrokeJoin(Paint.Join.ROUND);
        mLocationPaint.setStrokeWidth(20f);
        mLocationPaint.setColor(Color.WHITE);

        // Paint dedicated to calibration points
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.WHITE);

        paints = new HashMap<Position.Type , Paint>() {{
            put(Position.Type.CALIBRATION, mCalibrationPaint);
            put(Position.Type.LOCATION, mLocationPaint);
        }};
    }

    /**
     * Add a position to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t position type
     */
    public void addPosition(float x, float y, Position.Type t) {
        mPositions.add(new Position(x, y, t));
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mPositions.clear();
    }

    /**
     * Get a list of positions
     * @return list of positions
     */
    public List<Position> getPositions () {
        return mPositions;
    }

    /**
     * Get a list of positions which are of type given
     * @param t type
     * @return list of positions of type t
     */
    public List<Position> getPositionsOfType (Position.Type t) {
        List<Position> ret = new ArrayList<>();
        for (Position p:mPositions) {
            if(p.type == t) {
                ret.add(p);
            }
        }
        return ret;
    }

    public RectF getBounds() {
        return mBounds;
    }

    /**
     * Clear all position of type given
     * @param t type
     */
    @SuppressWarnings("unused")
    public void clearType(Position.Type t) {

        for (Position p:mPositions ) {
            if (p.type == t) {
                mPositions.remove(p);
            }
        }
    }

    public void drawGrid(Canvas canvas) {

        float dx = mBounds.width() / GRID_WIDTH;
        float dy = mBounds.height() / GRID_HEIGHT;

        // Row lines
        for (int i = 0; i < GRID_HEIGHT; i++)
        {
            canvas.drawLine(
                mBounds.left, i * dy, // start X & Y
                mBounds.right, i * dy, // end X & Y
                mGridPaint
            );
        }
        canvas.drawLine(
                mBounds.left, mBounds.height(), // start X & Y
                mBounds.right, mBounds.height(), // end X & Y
                mGridPaint
        );

        // Column lines
        for (int i = 0; i < GRID_WIDTH; i++)
        {
            canvas.drawLine(
                i * dx, mBounds.top, // start X & Y
                i * dx, mBounds.bottom, // end X & Y
                mGridPaint
            );
        }
        canvas.drawLine(
                mBounds.width(), mBounds.top, // start X & Y
                mBounds.width(), mBounds.bottom, // end X & Y
                mGridPaint
        );
    }


}
