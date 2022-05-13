package com.aiyouwei.drk.shelf.locker;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.aiyouwei.drk.shelf.FaceDetectActivity;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.SyncService;
import com.kongqw.serialportlibrary.OpenSerialPortService;

import static android.widget.FrameLayout.LayoutParams;

public class LockerHomeActivity extends FaceDetectActivity implements OnTimeEndListener, OpenSerialPortService.OnSerialPortReceiveDataListener {
    private final static String VIEW_TAG = "result_view";

    private final byte[] OPEN_DOOR_COMMAND = {0x01, (byte) 0xcc, (byte) 0xff};

    private OpenSerialPortService serialPortService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_locker_home;
    }

    @Override
    protected void setupView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        int h = Math.round(79.0f / 600 * height);
        int w = Math.round(25.f / 32 * width);
        LayoutParams lp = (LayoutParams) (findViewById(R.id.iv_title_bg)).getLayoutParams();
        lp.width = w;
        lp.height = h;

        try {
            serialPortService = new OpenSerialPortService();
            serialPortService.open(this);
        } catch (Exception e) {
            serialPortService = null;
            showToast("serial port init fail " + e.toString());
        }

        LayoutParams layoutParams = (LayoutParams) rgbView.getLayoutParams();
        layoutParams.width = Math.round(75.f / 128 * width);
        layoutParams.height = Math.round(39.f / 50 * height);
        layoutParams.topMargin = Math.round(23.f / 150 * height);

        LayoutParams irlp = (LayoutParams) irView.getLayoutParams();
        irlp.width = layoutParams.width;
        irlp.height = layoutParams.height;
        irlp.topMargin = layoutParams.topMargin;

        viewWidth = layoutParams.width;
        viewHeight = layoutParams.height;

        LayoutParams fl = (LayoutParams) faceRectView.getLayoutParams();
        fl.width = layoutParams.width;
        fl.height = layoutParams.height;
        fl.topMargin = layoutParams.topMargin;

        ImageView titleView = (ImageView) findViewById(R.id.iv_title);
        titleView.setOnLongClickListener(this);
        LayoutParams lp1 = (LayoutParams) titleView.getLayoutParams();
        lp1.width = Math.round(181.f / 512 * width);
        lp1.height = Math.round(3.f / 50 * height);
        lp1.topMargin = Math.round(17.f / 600 * height);

        LayoutParams lp2 = (LayoutParams) (findViewById(R.id.tip_layout)).getLayoutParams();
        lp2.width = Math.round(75.f / 128 * width);
        lp2.height = Math.round(1.f / 5 * height);
        lp2.bottomMargin = Math.round(1.f / 15 * height);

        LayoutParams lp3 = (LayoutParams) (findViewById(R.id.iv_logo)).getLayoutParams();
        lp3.width = Math.round(41.f / 256 * width);
        lp3.height = Math.round(47.f / 600 * height);
        lp3.leftMargin = Math.round(29.f / 1024 * width);
        lp3.topMargin = Math.round(31.f / 600 * height);
    }

    @Override
    protected void detectSuccess(String userId) {
        showResult(userId);
    }

    private void showResult(String useId) {
        removeResultView();

        rgbView.setVisibility(View.INVISIBLE);
        irView.setVisibility(View.INVISIBLE);
        faceRectView.setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.image_bkg)).setImageResource(R.drawable.bkg);

        LayoutParams lp = new LayoutParams(-2, -2);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = Math.round(143.f / 600 * getResources().getDisplayMetrics().heightPixels);
        ResultView resultView = new ResultView(this);
        resultView.setTag(VIEW_TAG);
        resultView.setUserId(useId, this);
        ((FrameLayout) findViewById(R.id.content_view)).addView(resultView, lp);

        if (null != serialPortService) {
            serialPortService.onSend(OPEN_DOOR_COMMAND);
        }
    }

    @Override
    public void onSerialPortReceiveData(String s) {
        showToast("Locker init success! " + s);
    }

    private void removeResultView() {
        FrameLayout container = (FrameLayout) findViewById(R.id.content_view);
        View v = container.findViewWithTag(VIEW_TAG);
        if (null != v) container.removeView(v);
    }

    @Override
    public void onTimeOut() {
        removeResultView();
        rgbView.setVisibility(View.VISIBLE);
        irView.setVisibility(View.VISIBLE);
        faceRectView.setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.image_bkg)).setImageResource(R.drawable.locker_bkg);

        rgbView.start();
        irView.start();
    }

    @Override
    public boolean onLongClick(View view) {
        showToast("Reloading face data");
        Intent intent = new Intent(this, SyncService.class);
        startService(intent);
        return true;
    }
}
