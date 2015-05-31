package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by celian on 07/04/15 for LO53Project
 */
public abstract class AbstractViewport extends View {

    // State
    private enum State {
        NONE,
        SCROLLING,
        SELECTING,
        SCALING
    }
    private State mState;

    public static float OFFSET_X = 25f;
    public static float OFFSET_Y = 25f;
    public static int ADD_ROW_HEIGHT = 150;
    public static int ADD_COL_WIDTH = 150;

    // Scale factor
    private float mScaleFactor = 1.f;

    // Frames
    private RectF mViewportFrame = new RectF();

    // Gesture Detectors
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    // World Map
    protected WorldMap mMap;

    private SelectionListener mSelectionListener;
    private RectF mAddRowButton;
    private RectF mAddColumnButton;
    private Paint mButtonPaint;

    /**
     * Constructor with a selection listener
     * @param c context
     * @param attrs attribute set
     * @param map world map
     * @param listener if not null, allows the viewport to select a position.
     */
    public AbstractViewport(Context c, AttributeSet attrs, WorldMap map, SelectionListener listener) {
        super(c, attrs);

        mMap = map;

        setBackgroundColor(Color.DKGRAY);

        // Gestures detectors
        mScaleGestureDetector   = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector        = new GestureDetector(getContext(), new ScrollListener());

        mState = State.NONE;
        mSelectionListener = listener;

        mAddRowButton = new RectF();
        mAddColumnButton = new RectF();
        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.WHITE);
    }

    /**
     * Constructor without selection listener
     * @param c context
     * @param attrs attribute set
     * @param map world map
     */
    public AbstractViewport(Context c, AttributeSet attrs, WorldMap map) {
        this(c, attrs, map, null);
    }

    /**
     * Transmit the motionEvent to the detectors and say to the canvas to redraw.
     * @param event the motion event
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        updateState(event);
        updateHoverSelection(event);

        boolean isScalingOrDragging = mScaleGestureDetector.onTouchEvent(event);
        isScalingOrDragging = mGestureDetector.onTouchEvent(event) || isScalingOrDragging;

        if (isScalingOrDragging) invalidate();
        return isScalingOrDragging || super.onTouchEvent(event);
    }

    /**
     * Update the state according to the motion event action
     * @param e : motion event
     */
    private void updateState(MotionEvent e) {

        // Ensure that we are not in SELECTING state when selection listener is not defined
        if (mState == State.SELECTING && mSelectionListener == null)
            throw new AssertionError("Error SELECTING State reached but listener is not defined.");

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mState == State.SELECTING) {
                    PointF selected = mMap.fingerUp();
                    if(selected != null)
                        mSelectionListener.onSelect(selected.x, selected.y);
                }
                mState = State.NONE;
                break;
            case MotionEvent.ACTION_DOWN:
                mState = State.SCROLLING;
                break;
        }
    }

    /**
     * Add hover position to the world map if we are selecting
     * @param e motion event
     */
    private void updateHoverSelection(MotionEvent e) {
        if(mState == State.SELECTING) {
            PointF hover = fromViewToWorld(e.getX(), e.getY());
            mMap.addHoverPosition(hover.x, hover.y);
            invalidate();
        }
    }

    /**
     * Add a point to the world map and force the viewport to redraw
     * @param x x coordinate
     * @param y y coordinate
     */
//    synchronized
    protected void addPoint(float x, float y, Position.Type t) {
        mMap.addPosition(x, y, t);
        this.invalidate(); // Force the viewport to redraw
    }

    /**
     * Convert coordinates from view space to world space
     * @param x x coordinate
     * @param y y coordinate
     * @return the converted point
     */
    public PointF fromViewToWorld (float x, float y) {
        return new PointF(
            (x / mScaleFactor) + mViewportFrame.left,
            (y / mScaleFactor) + mViewportFrame.top
        );
    }

    /**
     * Convert distance from view space to world space
     * @param d distance to convert
     * @return the converted distance
     */
    public float fromViewToWorld (float d) {
        return d / mScaleFactor;
    }


    /**
     * Convert coordinates from world space to view space
     * @param x x coordinate
     * @param y y coordinate
     * @return the converted point
     */
    @SuppressWarnings("unused")
    public PointF fromWorldToView (float x, float y) {
        return new PointF(
            (x - mViewportFrame.left) * mScaleFactor,
            (y - mViewportFrame.top) * mScaleFactor
        );
    }

    /**
     * Convert distance from world space to view space
     * @param d distance to convert
     * @return the converted distance
     */
    @SuppressWarnings("unused")
    public float fromWorldToView (float d) {
        return d * mScaleFactor;
    }

    /**
     * Perform the scale, the translation
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Scale the viewport
        canvas.scale(mScaleFactor, mScaleFactor);

        // Update viewport frame
        mViewportFrame.bottom = mViewportFrame.top + canvas.getHeight();
        mViewportFrame.right = mViewportFrame.left + canvas.getWidth();

        // Translate the viewport
        canvas.translate(-mViewportFrame.left, -mViewportFrame.top);

        // Draw the world map grid
        mMap.drawGrid(canvas);

        // Draw add row and add column buttons
        this.updateAddRowButton();
        this.updateAddColumnButton();
        canvas.drawRect(mAddRowButton, mButtonPaint);
        canvas.drawRect(mAddColumnButton, mButtonPaint);
    }

    /**
     * Clear the canvas
     */
    @SuppressWarnings("unused")
    public void clearCanvas() {
        mScaleFactor = 1.0f;
        mViewportFrame.set(0, 0, 0, 0);
        invalidate();
    }

    private void updateAddRowButton() {

        float map_view_width = fromWorldToView(mMap.getBounds().width());
        mAddRowButton.top = mMap.getBounds().height() ;
        mAddRowButton.bottom = mAddRowButton.top + ADD_ROW_HEIGHT;

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
        mAddColumnButton.right = mAddColumnButton.left + ADD_COL_WIDTH;

        if (map_view_height < mViewportFrame.height()) {
            mAddColumnButton.top = 0;
            mAddColumnButton.bottom = mAddColumnButton.top + mMap.getBounds().height();
        }
        else {
            mAddColumnButton.top = mViewportFrame.top + fromViewToWorld(OFFSET_Y);
            mAddColumnButton.bottom = mAddColumnButton.top + fromViewToWorld(mViewportFrame.height()- 2*OFFSET_Y);
        }
    }

    /***********************************************
     *  Gesture Listeners
     ***********************************************/
    /**
     * ScaleListener : Listen the scale of the viewport
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        /**
         * Called when a scale gesture begin
         * @param detector : the scale gesture detector
         * @return true anyway
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mState = State.SCALING;
            return true;
        }

        /**
         * Called when a scale gesture end
         * @param detector : the scale gesture detector
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mState = State.SCROLLING;
        }

        /**
         * Called when the user perform a scale with fingers
         * @param detector : the scale gesture detector
         * @return true anyway
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor_backup = mScaleFactor;
            mScaleFactor *= detector.getScaleFactor();

            // Limit zoom out
            float worldMap_width = mMap.getBounds().width();
            float worldMap_height = mMap.getBounds().height();
            float worldMap_view_width = fromWorldToView(worldMap_width);
            float worldMap_view_height = fromWorldToView(worldMap_height);

            if (worldMap_view_width + 2 * OFFSET_X + ADD_COL_WIDTH < mViewportFrame.width() &&
                    worldMap_view_height + 2 * OFFSET_Y + ADD_ROW_HEIGHT < mViewportFrame.height()    ) {
                mScaleFactor = scaleFactor_backup;
            }

            // Limit zoom in
            float square_width = mMap.squareWidth;
            float square_height = mMap.squareHeight;
            float square_view_width = fromWorldToView(square_width);
            float square_view_height = fromWorldToView(square_height);

            if (3 * square_view_width + OFFSET_X + ADD_COL_WIDTH > mViewportFrame.width() ||
                    3 * square_view_height + OFFSET_Y + ADD_ROW_HEIGHT > mViewportFrame.height()) {
                mScaleFactor = scaleFactor_backup;
            }
            return true;
        }
    }

    /**
     * ScrollListener: Listen the scroll of the viewport
     */
    private class ScrollListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * Called when the user performs a long press
         * @param e : motion event
         */
        @Override
        public void onLongPress(MotionEvent e) {
            if (mSelectionListener != null) {
                mState = State.SELECTING;
                updateHoverSelection(e);
            }
        }

        /**
         * Called when the user performs a scroll with a finger
         * @param e1 : first motion event
         * @param e2 : second motion event
         * @param distanceX : distance covered in X
         * @param distanceY : distance covered in Y
         * @return true anyway
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            if (mState != State.SCALING ) {

                RectF bounds = mMap.getBounds();

                PointF map_top_left = fromWorldToView(bounds.left, bounds.top);
                PointF map_bot_right = fromWorldToView(bounds.right, bounds.bottom);
                float world_offset_x_start = fromViewToWorld(OFFSET_X);
                float world_offset_y_start = fromViewToWorld(OFFSET_Y);
                float world_offset_x_end = fromViewToWorld(OFFSET_X + ADD_COL_WIDTH);
                float world_offset_y_end = fromViewToWorld(OFFSET_Y + ADD_ROW_HEIGHT);
                float world_viewport_width = fromViewToWorld(mViewportFrame.width());
                float world_viewport_height = fromViewToWorld(mViewportFrame.height());

                // Offset along X-axis
                if (!(mViewportFrame.left < map_top_left.x - OFFSET_X && mViewportFrame.right > map_bot_right.x - OFFSET_X)) {

                    // Offset the viewport according to the finger distance
                    mViewportFrame.offset(distanceX, 0);

                    // Now, we just have to ensure that we are not crossing world bounds
                    if (mViewportFrame.left < bounds.left - world_offset_x_start) {
                        mViewportFrame.offsetTo(bounds.left - world_offset_x_start, mViewportFrame.top);
                    } else if (mViewportFrame.left > bounds.right + world_offset_x_end - world_viewport_width) {
                        mViewportFrame.offsetTo(bounds.right + world_offset_x_end - world_viewport_width, mViewportFrame.top);
                    }
                }

                // Offset along Y-axis
                if (!(mViewportFrame.top < map_top_left.y - OFFSET_Y && mViewportFrame.bottom > map_bot_right.y - OFFSET_Y)) {

                    // Offset the viewport according to the finger distance
                    mViewportFrame.offset(0, distanceY);

                    // Now, we just have to ensure that we are not crossing world bounds
                    if (mViewportFrame.top < bounds.top - world_offset_y_start) {
                        mViewportFrame.offsetTo(mViewportFrame.left, bounds.top - world_offset_y_start);
                    } else if (mViewportFrame.top > bounds.bottom + world_offset_y_end - world_viewport_height) {
                        mViewportFrame.offsetTo(mViewportFrame.left, bounds.bottom + world_offset_y_end - world_viewport_height);
                    }
                }
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed (MotionEvent e) {
            PointF tap = fromViewToWorld(e.getX(), e.getY());
            if (mAddRowButton.contains(tap.x, tap.y)) {
                mMap.addRow();
                invalidate();
            }
            else if (mAddColumnButton.contains(tap.x, tap.y)) {
                mMap.addColumn();
                invalidate();
            }
            return true;
        }

    }


    /***********************************************
     *  Selection Listener
     ***********************************************/
    public interface SelectionListener {
        /**
         * Called when we select a location
         * @param x : x of selected location
         * @param y : y of selected location
         */
        void onSelect(float x, float y);
    }

}

