package com.aiyouwei.drk.shelf.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.R;

/**
 * Created by jishichen on 2017/8/21.
 */
public class ProgressHud extends Dialog {

    public ProgressHud(Context context) {
        super(context, R.style.FullHeightDialog);
        setContentView(R.layout.progress_hud);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(int resId) {
        setMessage(getContext().getString(resId));
    }

    public void setMessage(String message) {
        if (TextUtils.isEmpty(message)) return;

        findViewById(R.id.message).setVisibility(View.VISIBLE);
        TextView txt = (TextView) findViewById(R.id.message);
        txt.setText(message);
        txt.invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }

    @Override
    public void show() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        super.show();
    }
}
