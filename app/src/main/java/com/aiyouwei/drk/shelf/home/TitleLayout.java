package com.aiyouwei.drk.shelf.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.text.TextUtils.isEmpty;

public class TitleLayout extends RelativeLayout {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DisplayMetrics metrics;

    private float splitW, splitH;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        paint.setColor(0xFF0248FF);
        paint.setStyle(Paint.Style.FILL);
        splitH = 23.f / 300 * height;
        splitW = 3.f / 1024 * metrics.widthPixels;
    }

    public void setTitleInfo(String title, String subTitle,int leftMargin) {
        LayoutParams lp = new LayoutParams(-2, -2);

        if(leftMargin>0)
            lp.leftMargin = leftMargin;
            else
        lp.leftMargin = Math.round(3.f / 256 * metrics.widthPixels);

        lp.addRule(CENTER_VERTICAL);
        TextView v = new TextView(getContext());
        v.setTextColor(Color.WHITE);
        v.setTypeface(null, Typeface.BOLD);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.f / 20 * metrics.heightPixels);
        v.setText(Html.fromHtml(title));
        addView(v, lp);

        if (!isEmpty(subTitle)) {
            LayoutParams lp2 = new LayoutParams(-2, -2);
            lp2.addRule(CENTER_VERTICAL);
            lp2.addRule(ALIGN_PARENT_RIGHT);
            TextView tipView = new TextView(getContext());
            tipView.setText(subTitle);
            tipView.setTextColor(0x99FFFFFF);
            tipView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 19.f / 600 * metrics.heightPixels);
            addView(tipView, lp2);
        }
    }
    public void setTitleInfo(String title,int leftMargin){
        this.setTitleInfo(title,"",leftMargin);
    }
    public void setTitleInfo(String title, String subTitle){
        this.setTitleInfo(title,subTitle,-1);

    }
    public void setTitleInfo(String title){
        this.setTitleInfo(title,"",-1);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        canvas.drawRect(0, 0, splitW, splitH, paint);
    }
}
