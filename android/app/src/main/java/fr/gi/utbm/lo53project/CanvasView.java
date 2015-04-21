package fr.gi.utbm.lo53project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
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

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context  = c;
        setBackgroundColor(Color.DKGRAY);

        mPoints = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PointF p : mPoints) {
            canvas.drawPoint(p.x, p.y, mPaint);
        }
    }

    public void drawPoint(float x, float y) {
        mPoints.add(new PointF(x, y));
        invalidate();// Force to redraw the canvas
    }

    public void clearCanvas() {
        mPoints.clear();
        invalidate();
    }

}
