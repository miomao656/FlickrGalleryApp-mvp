package com.misotest.flickrgalleryapp.presentation.views.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by miomao on 3/21/15.
 */
public class CircleView extends View {

    private float x;
    private float y;
    private int r;

    // setup initial color
    private final int paintColor = Color.WHITE;
    // defines paint and canvas
    private Paint drawPaint;

    public CircleView(Context context, float x, float y, int r) {
        super(context);
        setupPaint();
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, r, drawPaint);
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(3);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

}
