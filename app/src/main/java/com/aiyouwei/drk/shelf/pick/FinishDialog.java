package com.aiyouwei.drk.shelf.pick;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.aiyouwei.drk.shelf.R;

public class FinishDialog extends Dialog implements View.OnClickListener {

    private static final int TIME_COUNT = 4;

    private static final int UPDATE_TIME = 1;

    private int mCount = TIME_COUNT;

    private TextView mButton;

    private OnFinishListener mListener;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != UPDATE_TIME) return;

            if (mCount <= 0) {
                dismiss();
            } else {
                mCount--;
                setTimeText(mCount);
                sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
            }
        }
    };

    public FinishDialog(@NonNull Context context, OnFinishListener listener) {
        super(context, R.style.FullHeightDialog);
        setContentView(R.layout.dialog_finish_layout);
        setCanceledOnTouchOutside(false);
        mListener = listener;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp.width = Math.round(184.f / 600 * height);
        lp.height = lp.width;
        lp.topMargin = Math.round(11.f / 200 * height);

        ((TextView) findViewById(R.id.label)).setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.f / 300 * height);

        mButton = (TextView) findViewById(R.id.btn_back);
        mButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        mButton.setOnClickListener(this);

        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mButton.getLayoutParams();
        lp1.width = Math.round(15.f / 64 * width);
        lp1.height = Math.round(9.f / 100 * height);
        lp1.topMargin = Math.round(13.f / 100 * height);

        setTimeText(5);
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
    }

    private void setTimeText(int t) {
        mButton.setText(String.format("Back (%d)", t));
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    @Override
    public void show() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = Math.round(153.f / 256 * metrics.widthPixels);
        lp.height = Math.round(113.f / 150 * metrics.heightPixels);
        super.show();
    }

    @Override
    public void dismiss() {
        mHandler.removeCallbacks(null);
        mHandler.removeMessages(UPDATE_TIME);
        mHandler = null;
        mListener.onBackHomeClicked();
        super.dismiss();
    }

    public interface OnFinishListener {

        void onBackHomeClicked();
    }
}
