package com.aiyouwei.drk.shelf.pick;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aiyouwei.drk.shelf.R;


public class BaseDialog extends Dialog {
    private TextView messageTextView;
    private Window window;

    public BaseDialog(Context context) {
        super( context, R.style.MyDialog );
        this.window = getWindow();
        setCanceledOnTouchOutside( false );
    }


    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    private void focusNotAle(Window window) {
        window.setFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE );
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    private void clearFocusNotAle(Window window) {
        window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE );
    }

    /**
     * 隐藏虚拟栏 ，显示的时候再隐藏掉
     *
     * @param window
     */
    public void hideNavigationBar(final Window window) {
        window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        window.getDecorView().setOnSystemUiVisibilityChangeListener( new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                window.getDecorView().setSystemUiVisibility( uiOptions );
            }
        } );
    }

    @Override
    public void show() {
        focusNotAle( window );
        if (!isShowing()) {
            super.show();
        }
        hideNavigationBar( window );
        clearFocusNotAle( window );
    }

    public void setTextSize(View v, int size) {
        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt( i );
                setTextSize( child, size );
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTextSize( size );
        }
    }

}
