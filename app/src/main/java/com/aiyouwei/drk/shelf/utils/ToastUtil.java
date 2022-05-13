package com.aiyouwei.drk.shelf.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.R;


public class ToastUtil {
    private static Toast mToast;

    public static void showToast(String content) {
        if (TextUtils.isEmpty( content )) {
            return;
        }
        cancelToast();
        mToast = Toast.makeText( AiYouWei.getInstance(), content, Toast.LENGTH_LONG );
        mToast.show();
    }




    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
