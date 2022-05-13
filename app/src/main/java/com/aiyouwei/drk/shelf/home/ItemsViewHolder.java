package com.aiyouwei.drk.shelf.home;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aiyouwei.drk.shelf.R;

public class ItemsViewHolder extends RecyclerView.ViewHolder {

    public TextView nameText, pnText,binText,countText;

    public ItemsViewHolder(View itemView) {
        super(itemView);

        DisplayMetrics metrics = itemView.getResources().getDisplayMetrics();

        int width = metrics.widthPixels;

        int height = metrics.heightPixels;

        float textSize = (3.f / 100) * height;

        nameText = itemView.findViewById(R.id.name);
        ViewGroup.LayoutParams layoutParams1 = nameText.getLayoutParams();
        layoutParams1.width = Math.round(0.255859375f * width);


        pnText = itemView.findViewById(R.id.pn);
        ViewGroup.LayoutParams layoutParams2 = pnText.getLayoutParams();
        layoutParams2.width =  Math.round(0.12f * width);


        binText = itemView.findViewById(R.id.bin);
        ViewGroup.LayoutParams layoutParams3 = binText.getLayoutParams();
        layoutParams3.width =  Math.round(0.0771484375f * width);

        countText = itemView.findViewById(R.id.count);

        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        nameText.setTextColor(Color.WHITE);

        pnText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        pnText.setTextColor(Color.WHITE);

        binText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binText.setTextColor(Color.WHITE);

        countText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        countText.setTextColor(Color.WHITE);

    }
}
