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

    // Limits
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    // Scale factor
    private float mScaleFactor = 1.f;

    // Frames
    private RectF mViewportFrame = new RectF();
    private RectF mWorldBounds = new RectF(0, 0, 2000, 2000);

    // Gesture Detectors
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    // Paint
    protected Paint mPaint;

    // World Map
    protected WorldMap mMap;

    private SelectionListener mSelectionListener;

    public AbstractViewport(Context c, AttributeSet attrs, WorldMap map, SelectionListener listener) {
        super(c, attrs);

        mMap = map;

        setBackgroundColor(Color.DKGRAY);

        // Gestures detectors
        mScaleGestureDetector   = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector        = new GestureDetector(getContext(), new ScrollListener());

        // Paints for the points
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);

        mState = State.NONE;
        mSelectionListener = listener;
    }

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
            throw new AssertionError("Error SELECTING State reached meanwhile selection listener is not defined.");

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mState == State.SELECTING) {
                    PointF selected = fromViewToWorld(e.getX(), e.getY());
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
            (x / mScaleFactor) - mViewportFrame.left,
            (y / mScaleFactor) - mViewportFrame.top
        );
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
        canvas.translate(mViewportFrame.left, mViewportFrame.top);

        canvas.drawRect(200, 200, 500, 500, mPaint);

        invalidate();
    }

    /**
     * Clear the canvas
     */
    @SuppressWarnings("unused")
    public void clearCanvas() {
        mScaleFactor = 1.0f;
//        mViewportOffset.set(0, 0);
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
            if (mSelectionListener != null)
                mState = State.SELECTING;
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

            if (mState != State.SCALING) {

                float dx = -(distanceX / mScaleFactor);
                float dy = -(distanceY / mScaleFactor);

                // Offset the viewport according to the finger distance
                mViewportFrame.offset(dx, dy);

                // Ensure that we don't cross world bounds
                if ( mViewportFrame.left < mWorldBounds.left || mViewportFrame.right > mWorldBounds.right) {
                    mViewportFrame.offset(-dx, 0);
                }
                if ( mViewportFrame.top < mWorldBounds.top|| mViewportFrame.bottom > mWorldBounds.bottom) {
                    mViewportFrame.offset(0, -dy);
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

