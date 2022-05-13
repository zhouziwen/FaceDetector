package com.aiyouwei.drk.shelf.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.LocaleList;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


import com.aiyouwei.drk.shelf.SplashActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;

public class UIUtils {
    private static long lastClickTime = 0;
    private static long FAST_CLICK_TIME = 500;
    private static HashMap<String, Locale> sLanguagesList = new HashMap<>();

    public static void setAlphaChange(final View... views) {
        for (View view : views) {
            if(view == null){
                return;
            }
            view.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setAlpha( 0.6f );
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setAlpha( 1.0f );
                            break;
                    }
                    return false;
                }
            } );
        }
    }

    public static boolean isFastClick() {
        try {
            long clickTime = SystemClock.elapsedRealtime();// System.currentTimeMillis();
            if ((clickTime - lastClickTime) > FAST_CLICK_TIME) {
                lastClickTime = clickTime;
                return false;
            }
        } catch (Exception ignored) {}
        return true;
    }


    public static Bitmap getScaledBitmap(String filePath, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile( filePath, options );

        int w = options.outWidth;
        int h = options.outHeight;
        int finalW = 0;
        int finalH = 0;
        if (w >= h) {
            finalH = width;
            finalW = (int) (finalH * w * 1.f / h);
        } else {
            finalW = width;
            finalH = (int) (width * h * 1.0f / w);
        }

        options.inSampleSize = calculateInSampleSize( options, finalW, finalH );
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile( filePath, options );
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static byte[] bitmap2bytes(Bitmap bitmap) {
        if (null == bitmap) return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream );
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        return byteArray;
    }

    /**
     * 获取屏幕宽宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {

        WindowManager manager = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE );
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics( metrics );
        int width = metrics.widthPixels;
        int sHeight = metrics.heightPixels;
        return width;
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE );
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics( metrics );
        int height = metrics.heightPixels;
        return height;
    }
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @SuppressLint("NewApi")
    public static void hideNextButton(final Button butGoto) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(butGoto, "translationY", 0f, 80f);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                butGoto.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    @SuppressLint("NewApi")
    public static void showNextButton(final Button butGoto) {
        if (butGoto.getVisibility() == View.VISIBLE) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(butGoto, "translationY", 80f, 0f);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                butGoto.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }
    public static void setTextColor(TextView textview,String color,int start,int end){
        SpannableStringBuilder style=new SpannableStringBuilder(textview.getText().toString());
        style.setSpan(new ForegroundColorSpan(Color.RED),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(style);
    }
    public static void setTextColor(TextView textview,int start,int end,int two,int two_end){
        SpannableStringBuilder style=new SpannableStringBuilder(textview.getText().toString());
        style.setSpan(new ForegroundColorSpan(Color.RED),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED),two,two_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(style);
    }
    public static void ShowKeyboard(Context context,View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
    public static void Hidekeyboard(Context context,View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    public static void changeAppLanguageWithoutRestart(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        Locale locale = getLocaleByLanguage(TextUtils.isEmpty(language) ? "en" : language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            context.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }


    public static Locale getLocaleByLanguage(String language) {
        if (sLanguagesList.size() == 0) {
            sLanguagesList.put("en", Locale.ENGLISH);
            sLanguagesList.put("zh_CN", Locale.CHINESE);
        }
        Locale locale = sLanguagesList.get(language);
        if (null != locale) {
            return locale;
        } else {
            return Locale.ENGLISH;
        }
    }

    public static void changeAppLanguage(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent mPendingIntent = PendingIntent
                .getActivity(context, 43452, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
