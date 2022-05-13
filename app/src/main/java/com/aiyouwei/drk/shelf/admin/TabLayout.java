package com.aiyouwei.drk.shelf.admin;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.aiyouwei.drk.shelf.OnTabSelectedListener;
import com.aiyouwei.drk.shelf.R;

public class TabLayout extends LinearLayout implements View.OnClickListener {

    private static final String TAG_ITEM = "item_%d";

    private int index = -1;

    private OnTabSelectedListener listener;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);

        init();
    }

    public void setOnTabClickedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    private void init() {
        String[] titles = getResources().getStringArray(R.array.tabs);

        LayoutParams lp = new LayoutParams(0, -1);
        lp.weight = 1;
        for (int i = 0; i < titles.length; i++) {
            TabTextView v = new TabTextView(getContext());
            v.setText(titles[i]);
            v.setTag(String.format(TAG_ITEM, i));
            v.setOnClickListener(this);
            addView(v, lp);
        }

        selectAt(0);
    }

    private void selectAt(int i) {
        if (index != -1) {
            findViewWithTag(String.format(TAG_ITEM, index)).setSelected(false);
        }
        findViewWithTag(String.format(TAG_ITEM, i)).setSelected(true);
        index = i;
    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        int idx = Integer.parseInt(tag.split("_")[1]);
        selectAt(idx);
        if (null != listener) listener.onTabSelected(idx);
    }
}
