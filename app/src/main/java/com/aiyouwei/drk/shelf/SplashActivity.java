package com.aiyouwei.drk.shelf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aiyouwei.drk.shelf.locker.LockerHomeActivity;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE = 1254;

    private FrameLayout mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mRootView = (FrameLayout) findViewById(android.R.id.content);
        setupView();

        if (checkSoFile()) {
            checkPermission();
        } else {
            showToast(R.string.library_not_found);
            finish();
        }
    }

    private void setupView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ImageView logo = (ImageView) findViewById(R.id.iv_logo);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) logo.getLayoutParams();
        int h = Math.round(101.f / 600 * height);
        int w = Math.round(671.f * h / 143);
        lp.width = w;
        lp.height = h;
        lp.topMargin = Math.round(253.f / 600 * height);

        ImageView titleBg = (ImageView) findViewById(R.id.iv_title_bg);
        h = Math.round(79.0f / 600 * height);
        w = Math.round(25.f / 32 * width);
        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) titleBg.getLayoutParams();
        lp1.width = w;
        lp1.height = h;

        ImageView titleView = (ImageView) findViewById(R.id.iv_title);
        h = Math.round(3.f / 50 * height);
        w = Math.round(181.f / 512 * width);
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) titleView.getLayoutParams();
        lp2.width = w;
        lp2.height = h;
        lp2.topMargin = Math.round(17.f / 600 * height);
    }

    private boolean checkSoFile() {
        String[] libs = {
                "libarcsoft_face_engine.so",
                "libarcsoft_face.so",
                "libarcsoft_image_util.so",
        };

        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libs) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    private void handleActive(int code) {
        if (code == ErrorInfo.MOK || code == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            startHome();
        } else {
            startHome();
            showToast(getString(R.string.active_failed, code));
        }
        finish();
    }

    private void activeEngine() {
        int activeCode = FaceEngine.activeOnline(SplashActivity.this,Config.getActiveKey(this),Constants.APP_ID,
                Constants.SDK_KEY);
        handleActive(activeCode);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            activeEngine();
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA};

            boolean needCheck = false;
            for (String s : permissions) {
                if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                    needCheck = true;
                    break;
                }
            }

            if (needCheck) {
                requestPermissions(permissions, REQUEST_CODE);
            } else {
                mRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startHome();
                        finish();
                    }
                }, 1000);
            }
        }
    }

    private void startHome() {
        Intent intent = new Intent();
        if (BuildConfig.APP_TYPE == 0) {
            intent.setClass(this, MainActivity.class);
        } else {
            intent.setClass(this, LockerHomeActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int i : grantResults) {
            isAllGranted &= (i == PackageManager.PERMISSION_GRANTED);
        }

        if (isAllGranted) {
            activeEngine();
        } else {
            showToast(R.string.permission_denied);
            finish();
        }
    }
}
