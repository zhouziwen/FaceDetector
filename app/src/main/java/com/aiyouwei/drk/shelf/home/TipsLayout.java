package com.aiyouwei.drk.shelf.home;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.R;

public class TipsLayout extends LinearLayout {

    public TipsLayout(Context context) {
        this(context, null);
    }

    public TipsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.alpha(1));
        setWeightSum(3);
        setGravity(Gravity.CENTER_VERTICAL);

        int[] icons = {
                R.drawable.icon_look,
                R.drawable.icon_cover,
                R.drawable.icon_eyeglasses
        };
        String[] titles = getResources().getStringArray(R.array.tips);

        LayoutParams lp = new LayoutParams(0, -2);
        lp.weight = 1;
        for (int i = 0; i < icons.length; i++) {
            View v = getItemView(icons[i], titles[i]);
            addView(v, lp);
        }
    }

    private View getItemView(int iconId, String s) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        LinearLayout line = new LinearLayout(getContext());
        line.setGravity(Gravity.CENTER_HORIZONTAL);
        line.setOrientation(LinearLayout.VERTICAL);

        int width = Math.round(2.f / 25 * height);
        ImageView iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(iconId);
        line.addView(iv, new LayoutParams(width, width));

        LayoutParams lp = new LayoutParams(-2, -2);
        lp.topMargin = Math.round(1.f / 60 * height);
        lp.width = 115;
        TextView tv = new TextView(getContext());
        tv.setText(s);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 7.f / 300 * height);
        tv.setTextColor(Color.BLACK);
        line.addView(tv, lp);

        return line;
    }
}
