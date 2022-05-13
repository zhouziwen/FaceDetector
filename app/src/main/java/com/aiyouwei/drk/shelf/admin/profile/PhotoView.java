package com.aiyouwei.drk.shelf.admin.profile;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.aiyouwei.drk.shelf.R;

public class PhotoView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap bitmap, placeBmp;

    private final Rect src = new Rect();
    private final RectF dst = new RectF();

    private int originWidth, originHeight;

    private Rect faceRect;

    private float density;

    private final Path path = new Path();

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        density = metrics.density;

        paint.setTextSize(density * 14);
        paint.setTextAlign(Paint.Align.CENTER);

        placeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_face);
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

    public void setOriginSize(int width, int height) {
        if (width == 0 || height == 0) return;

        originWidth = width;
        originHeight = height;
    }

    public void fillBitmap(Bitmap bitmap) {
        if (null == bitmap) return;

        this.bitmap = bitmap;
        invalidate();
    }

    public void clearRect() {
        faceRect = null;
        invalidate();
    }

    public void setFaceRect(Rect rect) {
        if (null == rect) return;

        faceRect = rect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x26FFFFFF);
        canvas.drawRect(1.f, 1.f, w * 1.f - 1, h * 1.f - 1, paint);

        if (null == bitmap) {
            src.set(0, 0, placeBmp.getWidth(), placeBmp.getHeight());
            dst.set(0, 0, w, h);
            canvas.drawBitmap(placeBmp, src, dst, null);
            return;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int bw, bh;
        src.set(0, 0, width, height);
        if (width >= height) {
            bw = w;
            bh = Math.round(bw * height * 1.f / width);
            dst.set(0, (h - bh) / 2.f, bw, (h + bh) / 2.f);
        } else {
            bh = h;
            bw = Math.round(width * bh * 1.f / height);
            dst.set((w - bw) / 2.f, 0, (w + bw) / 2.f, bh);
        }
        canvas.drawBitmap(bitmap, src, dst, null);

        if (null != faceRect) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(density);

            float left = faceRect.left * bw * 1.f / originWidth;
            float top = faceRect.top * bh * 1.f / originHeight;
            float right = faceRect.right * bw * 1.f / originWidth;
            float bottom = faceRect.bottom * bh * 1.f / originHeight;

            if (width >= height) {
                top += (h - bh) / 2.f;
                bottom += (h - bh) / 2.f;
            } else {
                left += (w - bw) / 2.f;
                right += (w - bw) / 2.f;
            }

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
}
