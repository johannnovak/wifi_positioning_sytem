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

    /**
     * A map containing all squares of different types
     */
    private Map< Square.Type, List<Square>> mSquares;

    /**
     * Bounds (in real coordinates)
     */
    private SRectF mBounds;

    /**
     * Sizes (in number of squares)
     */
    private int mGridWidth;
    private int mGridHeight;

    /**
     * Size of a square
     */
    private float mSquareWidth;
    private float mSquareHeight;

    /**
     * Paints
     */
    private Map<Square.Type , SPaint> mPaints;
    private SPaint mGridPaint;

    /**
     * Current Hover and Locate squares
     */
    private Square mCurrentHoverSquare;
    private Square mCurrentLocateSquare;

    /**
     * Constructor
     * @param width initial grid width (in number of squares)
     * @param height initial grid height (in number of squares)
     */
    public WorldMap (int width, int height) {
        initialize();
        mGridWidth = width;
        mGridHeight = height;
        mBounds = new SRectF(0, 0, mSquareWidth * mGridWidth, mSquareHeight * mGridHeight);
    }
    /**
     * Hard initialize all parameters of the world map
     */
    private void initialize() {
        mSquareWidth = 200;
        mSquareHeight = 200;

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

        mPaints = new HashMap<Square.Type , SPaint>() {{
            put(Square.Type.HOVER,        hoverPaint);
            put(Square.Type.CALIBRATION,  calibrationPaint);
            put(Square.Type.LOCATION,     locationPaint);
        }};

        mSquares = new HashMap<Square.Type, List<Square>>() {{
            put(Square.Type.CALIBRATION, new ArrayList<Square>());
            put(Square.Type.LOCATION, new ArrayList<Square>());
            put(Square.Type.HOVER, new ArrayList<Square>());
        }};

        mCurrentHoverSquare = null;

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
     * @return the current hover square
     */
    public Square getCurrentHoverSquare() {
        return mCurrentHoverSquare;
    }

    /**
     * Start to wait setting the "current hover square" immortality to true
     */
    public void startWaiting () {
        mCurrentHoverSquare.setImmortality(true);
    }

    /**
     * Stop to wait setting the "current hover square" immortality to false
     */
    public void stopWaiting () {
        mCurrentHoverSquare.setImmortality(false);
    }

    /**
     * Add a position of the given type to the map
     * @param x x coordinate
     * @param y y coordinate
     * @param t square type
     */
    public void addPosition(float x, float y, Square.Type t) {
        addSquare(toSquare(x, y), t);
    }

    /**
     * Add a square of the given type to the map
     * @param x row index
     * @param y column index
     * @param t square type
     */
    public void addSquare(int x, int y, Square.Type t) {
        addSquare(new Square(x, y), t);
    }

    /**
     * Add a square of the given type to the map
     * @param s square
     * @param t square type
     */
    public void addSquare(Square s, Square.Type t) {
        boolean add_permission = false;

        // Set immortality according to the type of square
        switch (t) {
            case HOVER:

                // If the square appear to be inside the grid bounds
                if (s.x >= 0 && s.x < mGridWidth && s.y >= 0 && s.y < mGridHeight ) {
                    // And if it is not already stored
                    if (!mSquares.get(t).contains(s)) {

                        // We set the previous square to mortal
                        if (mCurrentHoverSquare != null ) {
                            mCurrentHoverSquare.setImmortality(false);
                        }

                        // We can know assign the current square
                        mCurrentHoverSquare = s;
                        mCurrentHoverSquare.setImmortality(true); // It becomes the new immortal

                        // Let the function add it to the stored squares
                        add_permission = true;
                    }
                }
                else return;
                break;

            case CALIBRATION:

                // Calibration square is always immortal and cannot be killed
                s.setImmortality(true);

                // Useless to make several draw of the same point at the same time, so we verify if
                // it is stored or not
                if (!mSquares.get(t).contains(s)) {
                    add_permission = true;
                }
                break;

            case LOCATION:

                // We set the previous square to mortal
                if (mCurrentLocateSquare != null ) {
                    mCurrentLocateSquare.setImmortality(false);
                }

                // We can know assign the current square
                mCurrentLocateSquare = s;
                mCurrentLocateSquare.setImmortality(true); // It becomes the new immortal

                // A location square is always drawn
                add_permission = true;
                break;
        }

        // Add the square if it is permitted
        if (add_permission) mSquares.get(t).add(s);
    }

    /**
     * Clear all positions from the map
     */
    @SuppressWarnings("unused")
    public void clearAll() {
        mSquares.get(Square.Type.CALIBRATION).clear();
        mSquares.get(Square.Type.LOCATION).clear();
        mSquares.get(Square.Type.HOVER).clear();
    }

    /**
     * Clear position of type given
     * @param t type of position
     */
    @SuppressWarnings("unused")
    public void clear (Square.Type t) {
        mSquares.get(t).clear();
    }

    /**
     * Draw a square of the given type into the canvas
     * @param canvas canvas where to draw
     * @param s square to draw
     * @param t type of the square to draw
     */
    public void drawSquare (Canvas canvas, Square s, Square.Type t) {
        Paint paint = mPaints.get(t);

        // Setting the alpha value according to its life
        paint.setAlpha(s.life);

        canvas.drawRect(
                s.x * mSquareWidth,
                s.y * mSquareHeight,
                s.x * mSquareWidth + mSquareWidth,
                s.y * mSquareHeight + mSquareHeight,
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
        boolean redraw = updateLives(t);

        for (Square s : mSquares.get(t)) {
            drawSquare(canvas, s, t);
        }

        return redraw;
    }

    /**
     * Draw all positions of all types in the canvas
     * @param canvas canvas where to draw
     */
    @SuppressWarnings("unused")
    public boolean drawAllSquares (Canvas canvas) {
        boolean r1 = drawSquares(canvas, Square.Type.CALIBRATION);
        boolean r2 = drawSquares(canvas, Square.Type.LOCATION);
        boolean r3 = drawSquares(canvas, Square.Type.HOVER);
        return r1 || r2 || r3;
    }

    /**
     * Update lives of each square of a given type (naturally, immortals square's life will not decrease !)
     * @param t type of squares to update
     * @return true if it remains some alive squares
     */
    private boolean updateLives (Square.Type t) {
        // Update life value of squares of type t
        for (Square s : mSquares.get(t)) {
            s.decreaseLife(); // will not decrease if the square is immortal
        }

        // Remove dead squares
        Iterator it = mSquares.get(t).listIterator();
        while(it.hasNext()) {
            if (((Square) it.next()).isDead()) {
                it.remove();
            }
        }

        return !mSquares.get(t).isEmpty();
    }


    /**
     * Draw the grid in the canvas
     * @param canvas canvas where to draw
     */
    public void drawGrid(Canvas canvas) {

        float dx = mSquareWidth;
        float dy = mSquareHeight;

        // Row lines
        for (int i = 0; i < mGridHeight; i++)
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
        for (int i = 0; i < mGridWidth; i++)
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

    /**
     * Add a row to the map
     */
    public void addRow() {
        mGridHeight ++;
        mBounds.bottom += mSquareHeight;
    }

    /**
     * Add a column to the map
     */
    public void addColumn() {
        mGridWidth ++;
        mBounds.right += mSquareWidth;
    }

    /**
     * Take real coordinates and translate it in a position in the grid
     * @param x real x coordinate
     * @param y real y coordinate
     * @return position in the grid
     */
    public Square toSquare (float x, float y) {
        return new Square(
                (int)Math.floor(x / mSquareWidth),
                (int)Math.floor(y / mSquareHeight)
        );
    }

    /**
     * Get real coordinates of the square. It corresponds to the center point of the square
     * @param s the square
     * @return real square's coordinates
     */
    @SuppressWarnings("unused")
    public PointF toReal(Square s) {
        return new PointF(
                s.x * mSquareWidth + mSquareWidth/2,
                s.y * mSquareHeight + mSquareHeight/2
        );
    }

    /**
     * Store the map in a string, in the goal to be displayed in a console.
     * @return the map as a string
     */
    public String toString() {
        String str = "--------------World map------------------\n";
        str += "| Calibration \t\tLocation \t\tHover\n";

        List<Square> c_pos = mSquares.get(Square.Type.CALIBRATION);
        List<Square> l_pos = mSquares.get(Square.Type.LOCATION);
        List<Square> h_pos = mSquares.get(Square.Type.HOVER);

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
