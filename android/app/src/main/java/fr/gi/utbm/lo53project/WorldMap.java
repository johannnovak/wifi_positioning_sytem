package fr.gi.utbm.lo53project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private float mSquareWidth;
    private float mSquareHeight;

    public Map<Position.Type , Paint> paints;

    private boolean b_selecting;
    private Position mCurrentHoverPosition;

    public WorldMap() {

        mBounds = new RectF(0, 0, 2000, 2000);
        mSquareWidth = mBounds.width() / GRID_WIDTH;
        mSquareHeight = mBounds.height() / GRID_HEIGHT;

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
        final Paint hoverPaint = new Paint();
        hoverPaint.setAntiAlias(true);
        hoverPaint.setStyle(Paint.Style.FILL);
        hoverPaint.setColor(Color.GREEN);

        // Paint dedicated to calibration points
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.WHITE);

        paints = new HashMap<Position.Type , Paint>() {{
            put(Position.Type.HOVER,        hoverPaint);
            put(Position.Type.CALIBRATION,  calibrationPaint);
            put(Position.Type.LOCATION,     locationPaint);
        }};

        mPositions = new HashMap<Position.Type, List<Position>>() {{
            put(Position.Type.CALIBRATION, new ArrayList<Position>());
            put(Position.Type.LOCATION, new ArrayList<Position>());
            put(Position.Type.HOVER, new ArrayList<Position>());
        }};

        mCurrentHoverPosition = null;
    }

    /**
     * Give the bounds of the world map
     * @return bounds
     */
    public RectF getBounds() {
        return mBounds;
    }

    public void unhovered() {
        mCurrentHoverPosition = null;
        b_selecting = false;
    }

    /**
     * Add a position to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t position type
     */
    public void addPosition(float x, float y, Position.Type t) {
        mPositions.get(t).add(toPosition(x, y));
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mPositions.get(Position.Type.CALIBRATION).clear();
        mPositions.get(Position.Type.LOCATION).clear();
        mPositions.get(Position.Type.HOVER).clear();
    }

    /**
     * Clear position of type given
     * @param t type of position
     */
    @SuppressWarnings("unused")
    public void clear (Position.Type t) {
        mPositions.get(t).clear();
    }

    public void drawPosition (Canvas canvas, Position p, Position.Type t) {

        Paint paint = paints.get(t);

        if (t == Position.Type.HOVER) {
            paint.setAlpha(p.life);
        }

        canvas.drawRect(
                p.x * mSquareWidth,
                p.y * mSquareHeight,
                p.x * mSquareWidth + mSquareWidth,
                p.y * mSquareHeight + mSquareHeight,
                paint
        );
    }

    /**
     * Draw all positions of given type in the canvas
     * @param canvas canvas where to draw
     * @param t type of position
     */
    public boolean drawPositions (Canvas canvas, Position.Type t) {
        boolean result = false;

        if (t == Position.Type.HOVER) {
            result = updateLife();
        }

        for (Position p : mPositions.get(t)) {
            drawPosition(canvas, p, t);
        }

        return result;
    }

    private boolean updateLife() {

        // Update life value of hover positions
        for (Position p : mPositions.get(Position.Type.HOVER)) {
            // If we are selecting and the point p is the hovered one
            if (b_selecting && p.equals(mCurrentHoverPosition)) {
                p.recoverLife();
            }
            else {
                p.decreaseLife();
            }
        }

        // Remove dead positions
        Iterator it = mPositions.get(Position.Type.HOVER).listIterator();
        while(it.hasNext()) {
            if (((Position) it.next()).isDead()) {
                it.remove();
            }
        }

        return mPositions.get(Position.Type.HOVER).size() != 0;
    }

    /**
     * Draw all positions of both types CALIBRATION & LOCATION in the canvas
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

    public void addHoverPosition (float x, float y) {
        Position p = toPosition(x, y);
        if (!mPositions.get(Position.Type.HOVER).contains(p)) {
            mPositions.get(Position.Type.HOVER).add(p);
        }
        mCurrentHoverPosition = p;

        b_selecting = true;
    }

    /**
     * Take real coordinates and translate it in a position in the grid
     * @param x real x coordinate
     * @param y real y coordinate
     * @return position in the grid
     */
    private Position toPosition (float x, float y) {
        return new Position (
            (int)Math.floor(x * GRID_WIDTH / mBounds.width()),
            (int)Math.floor(y * GRID_HEIGHT / mBounds.height())
        );
    }
}
