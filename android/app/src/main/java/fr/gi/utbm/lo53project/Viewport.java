package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celian on 07/04/15.
 */
public class Viewport extends View {

    private List<PointF> mPoints;
    Context mContext;
    private Paint mPaint;


    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private static int MIN_X = -100;
    private static int MIN_Y = -100;
    private static int MAX_X = 1500;
    private static int MAX_Y = 1800;

    private boolean bScaling;

    private float mScaleFactor = 1.f;
    private PointF mViewportOffset = new PointF(0, 0);
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;


    public Viewport(Context c, AttributeSet attrs) {
        super(c, attrs);
        mContext  = c;

        setBackgroundColor(Color.DKGRAY);

        // Gestures detectors
        mScaleGestureDetector   = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector        = new GestureDetector(getContext(), new ScrollListener());

        // Array of points
        mPoints = new ArrayList<>();

        // Paints for the points
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);
    }

    /**
     * Transmit the motionEvent to the detectors and say to the canvas to redraw.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean isScalingOrDragging = mScaleGestureDetector.onTouchEvent(event);
        isScalingOrDragging = mGestureDetector.onTouchEvent(event) || isScalingOrDragging;

        if (isScalingOrDragging) invalidate();
        return isScalingOrDragging || super.onTouchEvent(event);

    }

    /**
     * Perform the scale, the translation, and draw the list of Points
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Scale the viewport
        canvas.scale(mScaleFactor, mScaleFactor);

        // Translate the viewport
        canvas.translate(mViewportOffset.x, mViewportOffset.y);

        // Draw all points
        // TO DO :
        //  - verify the points we draw are inside the viewport.
        //  - draw only the points which are not already drawn
        for (PointF p : mPoints) {
            canvas.drawPoint(p.x, p.y, mPaint);
        }
    }

    /**
     * Add a point to the viewport
     * @param x
     * @param y
     */
    public void drawPoint(float x, float y) {
        mPoints.add(new PointF(x, y));
        invalidate();// Force to redraw the canvas
    }

    /**
     * Clear the canvas
     */
    public void clearCanvas() {
        mPoints.clear();
        mScaleFactor = 1.0f;
        mViewportOffset.set(0, 0);

        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        /**
         * Called when the user perform a scale with fingers
         * @param detector : the scale gesture detector
         * @return
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor  = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));

            bScaling = true;

            return true;
        }
    }

    private class ScrollListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * Called when the user performs a scroll with a finger
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            // Ensure that we are not scaling the viewport
            if (!bScaling) {

                // Set the viewport offset according to the finger distance.
                mViewportOffset.set(
                    Math.max(MIN_X, Math.min(mViewportOffset.x - distanceX, MAX_X)),
                    Math.max(MIN_Y, Math.min(mViewportOffset.y - distanceY, MAX_Y))
                );
                return true;
            }

            bScaling = false;
            return false;

        }
    }


}

