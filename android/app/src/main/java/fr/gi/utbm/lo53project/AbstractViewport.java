package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
    private static int MIN_X = -100;
    private static int MIN_Y = -100;
    private static int MAX_X = 100;
    private static int MAX_Y = 100;

    // Moving and scaling values
    private float mScaleFactor = 1.f;
    private PointF mViewportOffset = new PointF(0, 0);

    // Gesture Detectors
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    // Paint
    protected Paint mPaint;

    // World Map
    protected WorldMap mMap;

    private SelectListener mSelectListener;

    public AbstractViewport(Context c, AttributeSet attrs, WorldMap map) {
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
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mState == State.SELECTING) {
                    PointF selected = fromViewToWorld(e.getX(), e.getY());
                    mSelectListener.onSelect(selected.x, selected.y);
                }
                mState = State.NONE;
                break;
            case MotionEvent.ACTION_DOWN:
                mState = State.SCROLLING;
                break;
        }
    }

    /**
     * Convert coordinates from view space to world space
     * @param x x coordinate
     * @param y y coordinate
     * @return the converted point
     */
    public PointF fromViewToWorld (float x, float y) {
        return new PointF(
            (x / mScaleFactor) - mViewportOffset.x,
            (y / mScaleFactor) - mViewportOffset.y
        );
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

        // Translate the viewport
        canvas.translate(mViewportOffset.x, mViewportOffset.y);

        canvas.drawRect(200, 200, 500, 500, mPaint);

        invalidate();
    }

//    /**
//     * Clear the canvas
//     */
//    public void clearCanvas() {
//        mPoints.clear();
//        mScaleFactor = 1.0f;
//        mViewportOffset.set(0, 0);
//
//        invalidate();
//    }

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
        public void onLongPress(MotionEvent e) { mState = State.SELECTING; }

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
                // Set the viewport offset according to the finger distance.
                mViewportOffset.set(
                        Math.max(MIN_X, Math.min(mViewportOffset.x - (distanceX / mScaleFactor), MAX_X)),
                        Math.max(MIN_Y, Math.min(mViewportOffset.y - (distanceY / mScaleFactor), MAX_Y))
                );
            }

            return true;
        }
    }


    /***********************************************
     *  Selection Listener
     ***********************************************/
    /**
     * Set the selection listener
     * @param l : the selection listener
     */
    public void setOnSelectListener ( SelectListener l ) {
        this.mSelectListener = l;
    }

    /**
     * Select Listener interface
     */
    public interface SelectListener {
        /**
         * Called when we select a location
         * @param x : x of selected location
         * @param y : y of selected location
         */
        void onSelect(float x, float y);
    }

}

