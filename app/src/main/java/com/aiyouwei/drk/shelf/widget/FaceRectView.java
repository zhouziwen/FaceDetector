package com.aiyouwei.drk.shelf.widget;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.aiyouwei.drk.shelf.model.DrawInfo;

import java.util.ArrayList;
import java.util.List;

public class FaceRectView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<RectF> rectList = new ArrayList<>();

    private final Path path = new Path();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float d = getResources().getDisplayMetrics().density;
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3 * d);
    }

    public void clearRectInfo() {
        rectList.clear();
        invalidate();
    }

    public void clearFaceInfo() {

    }

    public void addFaceInfo(List<DrawInfo> drawInfoList) {

    }

    public void setRectInfo(List<Rect> list,
                            int previewWidth, int previewHeight,
                            int viewWidth, int viewHeight) {
        rectList.clear();

        float horizontalRatio = viewWidth * 1.f / previewWidth;
        float verticalRatio = viewHeight * 1.f / previewHeight;

        for (Rect rect : list) {
            RectF newRect = new RectF();
            newRect.left = rect.left * horizontalRatio;
            newRect.right = rect.right * horizontalRatio;
            newRect.top = rect.top * verticalRatio;
            newRect.bottom = rect.bottom * verticalRatio;

            rectList.add(newRect);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = getPaddingLeft() + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                width = Math.min(width, size);
            }
        }

        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
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

        if (rectList.size() > 0) {
            for (RectF rect : rectList) {
                drawRect(canvas, rect);
            }
        }
    }

    private void drawRect(Canvas canvas, RectF rect) {
        float left = rect.left;
        float top = rect.top;
        float right = rect.right;
        float bottom = rect.bottom;

        float rw = right - left;
        float rh = bottom - top;
        path.reset();
        path.moveTo(left, top + rh / 4);
        path.lineTo(left, top);
        path.lineTo(left + rw / 4, top);

        path.moveTo(right - rw / 4, top);
        path.lineTo(right, top);
        path.lineTo(right, top + rh / 4);

        path.moveTo(right, bottom - rh / 4);
        path.lineTo(right, bottom);
        path.lineTo(right - rw / 4, bottom);

        path.moveTo(left + rw / 4, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, bottom - rh / 4);

        paint.setColor(Color.YELLOW);
        canvas.drawPath(path, paint);
    }
}