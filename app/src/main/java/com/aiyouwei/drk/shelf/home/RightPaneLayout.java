package com.aiyouwei.drk.shelf.home;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.aiyouwei.drk.shelf.Access;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.widget.ItemListView;

public class RightPaneLayout extends LinearLayout {

    public RightPaneLayout(Context context) {
        this(context, null);
    }

    public RightPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightPaneLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        float splitH = 23.f / 300 * height;
        int h = Math.round(splitH);
        int w = Math.round(80.5f / 151 * width);


        /*
            总标题
         */
        TitleLayout titleLayout = new TitleLayout(context);

        String title = "<font color='#000000'>Room</font> <font color='#0600CC'> "+ Config.getRoom(context)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   <font color='#000000'> Shelf</font> <font color='#0600CC'>"+ Config.getShelf(context) +"</font>";
//        String subTitle = "Current Inventory";

        titleLayout.setTitleInfo(title,55);

        titleLayout.getBackground();

        addView(titleLayout, new LayoutParams(w, h));

        h = Math.round(0.703f * height);

        w = Math.round(80.5f / 151* width);

        ItemListView listView = new ItemListView(context);

        addView(listView, new LayoutParams(w, h));
    }
}
