package com.aiyouwei.drk.shelf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

public class IndicatorLayout extends RelativeLayout {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int sw, sh;

    public IndicatorLayout(Context context) {
        this(context, null);
    }

    public IndicatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        sw = metrics.widthPixels;
        sh = metrics.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF2CFFAE);

        float width = 3.f / 1024 * sw;
        canvas.drawRect(0, 0, width, h, paint);

        width = 1.f / 150 * sh;
        float x = 1.f / 128 * sw;
        float y = 3.f / 512 * sh;
        canvas.drawRect(x, y, x + width, y + width, paint);

        x += width + 1.f / 512 * sw;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(x, y, x + width, y + width, paint);
    }
}
