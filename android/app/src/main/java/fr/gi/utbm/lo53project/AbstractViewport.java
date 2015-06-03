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
    protected int ADD_ROW_HEIGHT = 0;
    protected int ADD_COL_WIDTH = 0;

    // Scale factor
    private float mScaleFactor = 1.f;

    // Frames
    protected SRectF mViewportFrame = new SRectF();
    protected SRectF mOldViewportFrame = new SRectF();

    // Gesture Detectors
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    // World Map
    protected WorldMap mMap;

    private SelectionListener mSelectionListener;

    protected RectF mAddRowButton;
    protected RectF mAddColumnButton;
    protected Paint mButtonPaint;


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

        mAddRowButton = null;
        mAddColumnButton = null;
        mButtonPaint = null;
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

    protected void addPosition(int x, int y, Position.Type t) {
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

        // Update viewport frame
        mViewportFrame.bottom = mViewportFrame.top + fromViewToWorld(canvas.getHeight());
        mViewportFrame.right = mViewportFrame.left + fromViewToWorld(canvas.getWidth());

        // Scale the viewport
        canvas.scale(mScaleFactor, mScaleFactor);

        // Translate the viewport
        canvas.translate(-mViewportFrame.left, -mViewportFrame.top);

        // Draw the world map grid
        mMap.drawGrid(canvas);
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
            SRectF bounds = mMap.getBounds();

            float dS = detector.getScaleFactor();
            // We need to update viewportFrame size without onDraw function
            mViewportFrame.right = mViewportFrame.left + mViewportFrame.width() * dS;
            mViewportFrame.bottom = mViewportFrame.top + mViewportFrame.height() * dS;

            float world_offset_x_start = fromViewToWorld(OFFSET_X);
            float world_offset_y_start = fromViewToWorld(OFFSET_Y);
            float world_offset_x_end = fromViewToWorld(OFFSET_X + ADD_COL_WIDTH);
            float world_offset_y_end = fromViewToWorld(OFFSET_Y + ADD_ROW_HEIGHT);

            // Backup the viewport cloning it
            mOldViewportFrame.left = mViewportFrame.left;
            mOldViewportFrame.right = mViewportFrame.right;
            mOldViewportFrame.top = mViewportFrame.top;
            mOldViewportFrame.bottom = mViewportFrame.bottom;

            if (!limitViewportToBounds(
                    bounds.left - world_offset_x_start,
                    bounds.top - world_offset_y_start,
                    bounds.right + world_offset_x_end,
                    bounds.bottom + world_offset_y_end
                )|| dS > 1.00f
            ) {
                mScaleFactor *= dS;
            }

            return true;
        }
    }

    public boolean limitViewportToXBounds(float left, float right) {
        float new_left = mViewportFrame.left;
        boolean zoom_is_max = mOldViewportFrame.containsX(left, right);

        if (zoom_is_max) {
            new_left = left;
        }
        else {
            // Check limits
            if (mViewportFrame.left < left) {
                new_left = left;
            } else if (mViewportFrame.right > right) {
                new_left = right - mViewportFrame.width();
            }
        }

        // Offset viewport
        mViewportFrame.offsetTo(new_left, mViewportFrame.top);

        return zoom_is_max;
    }

    public boolean limitViewportToYBounds(float top, float bottom) {
        float new_top = mViewportFrame.top;
        boolean zoom_is_max = mOldViewportFrame.containsY(top, bottom);

        if (zoom_is_max) {
            new_top = top;
        }
        else {
            // Check limits
            if (mViewportFrame.top < top) {
                new_top = top;
            } else if (mViewportFrame.bottom > bottom) {
                new_top = bottom - mViewportFrame.height();
            }
        }

        // Offset viewport
        mViewportFrame.offsetTo(mViewportFrame.left, new_top);

        return zoom_is_max;
    }

    public boolean limitViewportToBounds(float left, float top, float right, float bottom) {
        return (
            limitViewportToXBounds(left, right) &&
            limitViewportToYBounds(top, bottom)
        );
    }

    public void limitViewportToBounds(RectF bounds) {
        limitViewportToBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
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

                // Backup the viewport cloning it
                mOldViewportFrame.left = mViewportFrame.left;
                mOldViewportFrame.right = mViewportFrame.right;
                mOldViewportFrame.top = mViewportFrame.top;
                mOldViewportFrame.bottom = mViewportFrame.bottom;

                float world_offset_x_start = fromViewToWorld(OFFSET_X);
                float world_offset_y_start = fromViewToWorld(OFFSET_Y);
                float world_offset_x_end = fromViewToWorld(OFFSET_X + ADD_COL_WIDTH);
                float world_offset_y_end = fromViewToWorld(OFFSET_Y + ADD_ROW_HEIGHT);

                // Offset the viewport according to the finger distance
                mViewportFrame.offset(distanceX, 0);

                // Now, we just have to ensure that we are not crossing world bounds
                limitViewportToXBounds(
                    bounds.left - world_offset_x_start,
                    bounds.right + world_offset_x_end
                );

                // Offset the viewport according to the finger distance
                mViewportFrame.offset(0, distanceY);

                // Now, we just have to ensure that we are not crossing world bounds
                limitViewportToYBounds(
                    bounds.top - world_offset_y_start,
                    bounds.bottom + world_offset_y_end
                );

            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed (MotionEvent e) {
            PointF tap = fromViewToWorld(e.getX(), e.getY());
            try {
                if (mAddRowButton.contains(tap.x, tap.y)) {
                    mMap.addRow();
                    invalidate();
                } else if (mAddColumnButton.contains(tap.x, tap.y)) {
                    mMap.addColumn();
                    invalidate();
                }
            }
            catch(NullPointerException ex) {
                return false;
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

