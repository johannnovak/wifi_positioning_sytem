package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    // Limits
    public static float MIN_ZOOM = 1f;
    public static float MAX_ZOOM = 5f;

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
            throw new AssertionError("Error SELECTING State reached meanwhile but listener is not defined.");

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mState == State.SELECTING) {
                    PointF selected = fromViewToWorld(e.getX(), e.getY());
                    mSelectionListener.onSelect(selected.x, selected.y);
                    mMap.outFinger();
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

        // Update viewport frame
        mViewportFrame.bottom = mViewportFrame.top + canvas.getHeight();
        mViewportFrame.right = mViewportFrame.left + canvas.getWidth();

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
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor  = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));

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

                // Offset along X-axis
                if (mViewportFrame.width() < bounds.width()) {
                    float dx = (distanceX / mScaleFactor);

                    // Offset the viewport according to the finger distance
                    mViewportFrame.offset(dx, 0);


                    // Now, we just have to ensure that we are not crossing world bounds
                    if (mViewportFrame.left < bounds.left) {
                        mViewportFrame.offsetTo(bounds.left, mViewportFrame.top);
                    } else if (mViewportFrame.left > bounds.right - fromViewToWorld(mViewportFrame.width())) {
                        mViewportFrame.offsetTo(bounds.right - fromViewToWorld(mViewportFrame.width()), mViewportFrame.top);
                    }
                }

                // Offset along Y-axis
                if (mViewportFrame.height() < bounds.height()) {
                    float dy = (distanceY / mScaleFactor);

                    // Offset the viewport according to the finger distance
                    mViewportFrame.offset(0, dy);

                    // Now, we just have to ensure that we are not crossing world bounds
                    if (mViewportFrame.top < bounds.top) {
                        mViewportFrame.offsetTo(mViewportFrame.left, bounds.top);
                    } else if (mViewportFrame.top > bounds.bottom - fromViewToWorld(mViewportFrame.height())) {
                        mViewportFrame.offsetTo(mViewportFrame.left, bounds.bottom - fromViewToWorld(mViewportFrame.height()));
                    }
                }
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

