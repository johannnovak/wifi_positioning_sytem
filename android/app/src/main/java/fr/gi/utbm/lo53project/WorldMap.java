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

    private Map< Position.Type, List<Position>> mPositions;
    private Paint mGridPaint;

    private RectF mBounds;

    private static int GRID_WIDTH = 10;
    private static int GRID_HEIGHT = 10;

    public Map<Position.Type , Paint> paints;

    public WorldMap() {

        mBounds = new RectF(0, 0, 2000, 2000);

        // Paint dedicated to calibration points
        final Paint calibrationPaint  = new Paint();
        calibrationPaint.setAntiAlias(true);
        calibrationPaint.setStyle(Paint.Style.STROKE);
        calibrationPaint.setStrokeJoin(Paint.Join.ROUND);
        calibrationPaint.setStrokeWidth(20f);
        calibrationPaint.setColor(Color.BLACK);

        // Paint dedicated to calibration points
        final Paint locationPaint = new Paint();
        locationPaint.setAntiAlias(true);
        locationPaint.setStyle(Paint.Style.STROKE);
        locationPaint.setStrokeJoin(Paint.Join.ROUND);
        locationPaint.setStrokeWidth(20f);
        locationPaint.setColor(Color.WHITE);

        // Paint dedicated to calibration points
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.WHITE);

        paints = new HashMap<Position.Type , Paint>() {{
            put(Position.Type.CALIBRATION, calibrationPaint);
            put(Position.Type.LOCATION, locationPaint);
        }};

        mPositions = new HashMap<Position.Type, List<Position>>() {{
            put(Position.Type.CALIBRATION, new ArrayList<Position>());
            put(Position.Type.LOCATION, new ArrayList<Position>());
        }};
    }

    /**
     * Give the bounds of the world map
     * @return bounds
     */
    public RectF getBounds() {
        return mBounds;
    }

    /**
     * Add a position to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t position type
     */
    public void addPosition(float x, float y, Position.Type t) {
        mPositions.get(t).add(new Position(x, y));
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mPositions.get(Position.Type.CALIBRATION).clear();
        mPositions.get(Position.Type.LOCATION).clear();
    }

    /**
     * Clear position of type given
     * @param t type of position
     */
    @SuppressWarnings("unused")
    public void clear (Position.Type t) {
        mPositions.get(t).clear();
    }

    /**
     * Draw all positions of given type in the canvas
     * @param canvas canvas where to draw
     * @param t type of position
     */
    public void drawPositions (Canvas canvas, Position.Type t) {
        for (Position p : mPositions.get(t)) {
            canvas.drawPoint(p.x, p.y, paints.get(t));
        }
    }

    /**
     * Draw all positions of both types in the canvas
     * @param canvas canvas where to draw
     */
    public void drawPositions (Canvas canvas) {
        drawPositions(canvas, Position.Type.CALIBRATION);
        drawPositions(canvas, Position.Type.LOCATION);
    }


    /**
     * Draw the grid in the canvas
     * @param canvas canvas where to draw
     */
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
