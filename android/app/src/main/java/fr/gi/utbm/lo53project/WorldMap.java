package fr.gi.utbm.lo53project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

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

    private Map< Square.Type, List<Square>> mPositions;
    private SPaint mGridPaint;

    private SRectF mBounds;

    private int gridWidth;
    private int gridHeight;
    public float squareWidth;
    public float squareHeight;

    public Map<Square.Type , SPaint> paints;

    private boolean bCurrentHoverPositionIsImmortal;
    private Square mCurrentHoverPosition;

    public WorldMap() {
        squareWidth = 200;
        squareHeight = 200;
        gridHeight = 3;
        gridWidth = 3;
        mBounds = new SRectF(0, 0, squareWidth * gridWidth, squareHeight * gridHeight);

        // Paint dedicated to calibration points
        final SPaint calibrationPaint  = new SPaint();
        calibrationPaint.setAntiAlias(true);
        calibrationPaint.setStyle(Paint.Style.STROKE);
        calibrationPaint.setStrokeJoin(Paint.Join.ROUND);
        calibrationPaint.setStrokeWidth(20f);
        calibrationPaint.setColor(Color.BLACK);

        // Paint dedicated to calibration points
        final SPaint locationPaint = new SPaint();
        locationPaint.setAntiAlias(true);
        locationPaint.setStyle(Paint.Style.STROKE);
        locationPaint.setStrokeJoin(Paint.Join.ROUND);
        locationPaint.setStrokeWidth(20f);
        locationPaint.setColor(Color.WHITE);

        // Paint dedicated to calibration points
        final SPaint hoverPaint = new SPaint();
        hoverPaint.setAntiAlias(true);
        hoverPaint.setStyle(Paint.Style.FILL);
        hoverPaint.setColor(Color.GREEN);

        // Paint dedicated to calibration points
        mGridPaint = new SPaint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.WHITE);

        paints = new HashMap<Square.Type , SPaint>() {{
            put(Square.Type.HOVER,        hoverPaint);
            put(Square.Type.CALIBRATION,  calibrationPaint);
            put(Square.Type.LOCATION,     locationPaint);
        }};

        mPositions = new HashMap<Square.Type, List<Square>>() {{
            put(Square.Type.CALIBRATION, new ArrayList<Square>());
            put(Square.Type.LOCATION, new ArrayList<Square>());
            put(Square.Type.HOVER, new ArrayList<Square>());
        }};

        mCurrentHoverPosition = null;

        System.out.println("WorldMap : constructor");
    }

    /**
     * Give the bounds of the world map
     * @return bounds
     */
    public SRectF getBounds() {
        return mBounds;
    }

    /**
     *
     * @return
     */
    public PointF fingerUp() {
        return toReal(mCurrentHoverPosition);
    }

    /**
     *
     */
    public void startWaiting () {
        bCurrentHoverPositionIsImmortal = true;
    }

    /**
     *
     */
    public void stopWaiting () {
        bCurrentHoverPositionIsImmortal = false;
    }

    /**
     * Add a position to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t position type
     */
    public void addPosition(float x, float y, Square.Type t) {
        mPositions.get(t).add(toSquare(x, y));
    }

    public void addSquare(int x, int y, Square.Type t) {
        mPositions.get(t).add(new Square(x, y));
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mPositions.get(Square.Type.CALIBRATION).clear();
        mPositions.get(Square.Type.LOCATION).clear();
        mPositions.get(Square.Type.HOVER).clear();
    }

    /**
     * Clear position of type given
     * @param t type of position
     */
    @SuppressWarnings("unused")
    public void clear (Square.Type t) {
        mPositions.get(t).clear();
    }

    /**
     * Draw a position of the given type into the canvas
     * @param canvas canvas where to draw
     * @param p position to draw
     * @param t type of the position to draw
     */
    public void drawSquare (Canvas canvas, Square p, Square.Type t) {
        Paint paint = paints.get(t);

        if (t == Square.Type.HOVER) {
            paint.setAlpha(p.life);
        }

        canvas.drawRect(
                p.x * squareWidth,
                p.y * squareHeight,
                p.x * squareWidth + squareWidth,
                p.y * squareHeight + squareHeight,
                paint
        );
    }

    /**
     * Draw all positions of given type in the canvas
     * @param canvas canvas where to draw
     * @param t type of position
     * @return have to redraw after that
     */
    public boolean drawSquares (Canvas canvas, Square.Type t) {
        boolean redraw = false;

        if (t == Square.Type.HOVER) {
            redraw = updateLife();
        }

        for (Square p : mPositions.get(t)) {
            drawSquare(canvas, p, t);
        }

        return redraw;
    }

    /**
     * Recover or decrease life and remove hovered points if are dead
     * @return have to redraw after that
     */
    private boolean updateLife() {

        // Update life value of hover positions
        for (Square p : mPositions.get(Square.Type.HOVER)) {
            // If we are selecting and the point p is the hovered one
            if ((bCurrentHoverPositionIsImmortal) && p.equals(mCurrentHoverPosition)) {
                p.recoverLife();
            }
            else {
                p.decreaseLife();
            }
        }

        // Remove dead positions
        Iterator it = mPositions.get(Square.Type.HOVER).listIterator();
        while(it.hasNext()) {
            if (((Square) it.next()).isDead()) {
                it.remove();
            }
        }

        return !mPositions.get(Square.Type.HOVER).isEmpty();
    }

    /**
     * Draw all positions of both types CALIBRATION & LOCATION in the canvas
     * @param canvas canvas where to draw
     */
    public void drawSquares (Canvas canvas) {
        drawSquares(canvas, Square.Type.CALIBRATION);
        drawSquares(canvas, Square.Type.LOCATION);
    }

    /**
     * Draw the grid in the canvas
     * @param canvas canvas where to draw
     */
    public void drawGrid(Canvas canvas) {

        float dx = squareWidth;
        float dy = squareHeight;

        // Row lines
        for (int i = 0; i < gridHeight; i++)
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
        for (int i = 0; i < gridWidth; i++)
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
        if (mBounds.contains(x, y)) {
            Square p = toSquare(x, y);
            if (!mPositions.get(Square.Type.HOVER).contains(p)) {
                mPositions.get(Square.Type.HOVER).add(p);
            }
            mCurrentHoverPosition = p;

            bCurrentHoverPositionIsImmortal = true;
        }
    }

    public void addRow() {
        gridHeight ++;
        mBounds.bottom += squareHeight;
    }

    public void addColumn() {
        gridWidth ++;
        mBounds.right += squareWidth;
    }

    /**
     * Take real coordinates and translate it in a position in the grid
     * @param x real x coordinate
     * @param y real y coordinate
     * @return position in the grid
     */
    public Square toSquare (float x, float y) {
        return new Square(
            (int)Math.floor(x / squareWidth),
            (int)Math.floor(y / squareHeight)
        );
    }

    private PointF toReal(Square p) {
        return new PointF(
            p.x * squareWidth,
            p.y * squareHeight
        );
    }

    public String toString() {
        String str = "--------------World map------------------\n";
        str += "| Calibration \t\tLocation \t\tHover\n";

        List<Square> c_pos = mPositions.get(Square.Type.CALIBRATION);
        List<Square> l_pos = mPositions.get(Square.Type.LOCATION);
        List<Square> h_pos = mPositions.get(Square.Type.HOVER);

        int c_pos_size = c_pos.size();
        int l_pos_size = l_pos.size();
        int h_pos_size = h_pos.size();

        int max = Math.max(Math.max(c_pos.size(), l_pos.size()), h_pos.size());

        // Print calibration positions
        for (int i = 0; i < max; i++) {

            if ( i < c_pos_size ) {
                str += "| " + c_pos.get(i).toString() + "\t\t\t";
            }
            else {
                str += "|\t\t\t\t\t";
            }

            if ( i < l_pos_size ) {
                str += l_pos.get(i).toString() + "\t\t\t";
            }
            else {
                str += "\t\t\t\t";
            }

            if ( i < h_pos_size ) {
                str += h_pos.get(i).toString() + "\t";
            }
            else {
                str += "";
            }
            str += "\n";
        }

        str += "-----------------------------------------\n";

        return str;
    }
}
