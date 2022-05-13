package com.aiyouwei.drk.shelf.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class TabTextView extends View {

    private final int TEXT_COLOR = 0x80FFFFFF;

    private final int TEXT_COLOR_SELECTED = 0xFFFFFFFF;

    private final int STRIP_COLOR = 0xFF2CFFAE;

    private final int STRIP_HEIGHT = 3;

    private final int STRIP_WIDTH = 46;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String text;

    private final Rect textRect = new Rect();

    private float density;

    public TabTextView(Context context) {
        this(context, null);
    }

    public TabTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(density * 20);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        invalidate();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = getPaddingLeft() + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                width = Math.min(width, size);
            }
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = getPaddingTop() + getPaddingBottom();
            if (mode == MeasureSpec.AT_MOST) {
                height = Math.min(height, size);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        paint.setColor(isSelected() ? TEXT_COLOR_SELECTED : TEXT_COLOR);
        paint.getTextBounds(text, 0, text.length(), textRect);
        float x = w / 2f;
        float y = h / 2f - textRect.exactCenterY();
        canvas.drawText(text, x, y, paint);

        if (isSelected()) {
            float sw = STRIP_WIDTH * density;
            float sh = STRIP_HEIGHT * density;
            paint.setColor(STRIP_COLOR);
            y = h * 1.f - sh - 25 * density;
            canvas.drawRect(x - sw / 2, h * 1.f - sh - 25 * density, x + sw / 2, y + sh, paint);
        }
    }
}
