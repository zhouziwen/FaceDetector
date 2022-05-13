package com.aiyouwei.drk.shelf.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiyouwei.drk.shelf.R;

import static android.text.TextUtils.isEmpty;

public class SubheadLayout extends LinearLayout {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DisplayMetrics metrics;

    private float splitW, splitH;

    public SubheadLayout(Context context) {
        this(context, null);
    }

    public SubheadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubheadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        TextView textView1 = new TextView(context);
        TextPaint paint1 = textView1.getPaint();
        paint1.setFakeBoldText(true);
        textView1.setText("Itemname");
        textView1.setTextSize(0.01953125f*width);
        textView1.setTextColor(getResources().getColor(R.color.blue));
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(-1, -1);
        lp1.weight =0.55f ;
        lp1.leftMargin = Math.round(0.01171875f*width);
        lp1.topMargin = Math.round(0.01875f*height) ;


        TextView textView2 = new TextView(context);
        TextPaint paint2 = textView2.getPaint();
        paint2.setFakeBoldText(true);
        textView2.setText("P/N");
        textView2.setTextSize(0.01953125f*width);
        textView2.setTextColor(getResources().getColor(R.color.blue));
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -1);
        lp2.weight =  0.85f ;
        lp2.topMargin = Math.round(0.01875f*height) ;


        TextView textView3 = new TextView(context);
        TextPaint paint3 = textView3.getPaint();
        paint3.setFakeBoldText(true);
        textView3.setText("Bin");
        textView3.setTextSize(0.01953125f*width);
        textView3.setTextColor(getResources().getColor(R.color.blue));
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(-1, -1);
        lp3.weight =0.95f ;
        lp3.topMargin = Math.round(0.01875f*height) ;

        TextView textView4 = new TextView(context);
        TextPaint paint4 = textView4.getPaint();
        paint4.setFakeBoldText(true);
        textView4.setText("Qty");
        textView4.setTextSize(0.01953125f*width);
        textView4.setTextColor(getResources().getColor(R.color.blue));
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(-1, -1);
        lp4.weight =1 ;
        lp4.topMargin = Math.round(0.01875f*height) ;

        addView(textView1,lp1);
        addView(textView2,lp2);
        addView(textView3,lp3);
        addView(textView4,lp4);
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
