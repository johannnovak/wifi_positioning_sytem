package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by celian on 07/04/15.
 */
public class CanvasView extends View {
    public int width;
    public int height;
    private List<PointF> mPoints;
    Context context;
    private Paint mPaint;
    private float mOffsetX;

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context  = c;
        setBackgroundColor(Color.DKGRAY);
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mOffsetX = 0;

        mPoints = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);

        canvas.translate(mOffsetX, 0);
        for (PointF p : mPoints) {
            canvas.drawPoint(p.x, p.y, mPaint);
        }

        canvas.restore();
    }

    public void drawPoint(float x, float y) {
        mPoints.add(new PointF(x, y));
        invalidate();// Force to redraw the canvas
    }

    public void translateX (float t) {
        mOffsetX = t;
        invalidate();
    }
    public void clearCanvas() {
        mPoints.clear();
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }
}
