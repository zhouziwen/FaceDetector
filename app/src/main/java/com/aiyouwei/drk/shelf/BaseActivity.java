package com.aiyouwei.drk.shelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import io.socket.client.IO;
import io.socket.client.Socket;

import com.aiyouwei.drk.shelf.admin.AdminActivity;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.JWebSocketClient;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.ThreadPoolUtil;
import com.aiyouwei.drk.shelf.utils.Utils;
import com.aiyouwei.drk.shelf.widget.ProgressHud;
import com.zhangke.zlog.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static android.text.TextUtils.isEmpty;

public class BaseActivity extends FragmentActivity implements View.OnSystemUiVisibilityChangeListener {

    private final String TAG = getClass().getSimpleName();

    private ProgressHud mProgress;

    private View mDecorView;

    private int mFlag;

    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView = getWindow().getDecorView();
            mFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            mDecorView.setSystemUiVisibility(mFlag);
            mDecorView.setOnSystemUiVisibilityChangeListener(this);
        }
    }




    @Override
    public void onSystemUiVisibilityChange(int i) {
        if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            mDecorView.setSystemUiVisibility(mFlag);
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.setSystemUiVisibility(mFlag);
        }
    }



    public void hideProgress() {
        if (null != mProgress && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    public void showProgress() {
        showProgress(getString(R.string.please_wait));
    }

    public void showProgress(String msg) {
        if (null == mProgress) {
            mProgress = new ProgressHud(this);
        }
        mProgress.setMessage(msg);
        mProgress.show();
    }




    @SuppressLint("ShowToast")
    protected void showToast(final int resId) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(toast == null){
                    toast =  Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT);
                }else {
                    toast.setText(resId);
                }

                toast.show();
            }
        });

        toast.show();

    }

    @SuppressLint("ShowToast")
    public void showToast(@NonNull final String s) {
        ZLog.i(TAG,"showToast : "+s);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(toast == null){
                    toast =  Toast.makeText(BaseActivity.this, s, Toast.LENGTH_SHORT);
                }else {
                    toast.setText(s);
                }

                toast.show();
            }
        });

    }
}
