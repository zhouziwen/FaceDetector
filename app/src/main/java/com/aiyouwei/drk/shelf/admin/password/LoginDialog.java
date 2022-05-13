package com.aiyouwei.drk.shelf.admin.password;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.widget.IndicatorLayout;

import static android.text.TextUtils.isEmpty;

public class LoginDialog extends Dialog implements View.OnClickListener {

    private EditText mEditText;

    private OnLoginListener mListener;

    public LoginDialog(Context context, OnLoginListener listener) {
        super(context, R.style.FullHeightDialog);
        mListener = listener;
        setContentView(R.layout.login_dialog);
        setCanceledOnTouchOutside(true);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        findViewById(R.id.btn_login).setOnClickListener(this);
        mEditText = findViewById(R.id.edit_password);

        IndicatorLayout indicator = findViewById(R.id.login_title);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
        lp.width = -1;
        lp.height = Math.round(23.f / 300 * height);

        TextView btn = findViewById(R.id.btn_login);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.f / 100 * height);
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        lp1.width = -1;
        lp1.height = Math.round(11.f / 150 * height);

        RelativeLayout layout = findViewById(R.id.content_layout);
        layout.setPadding(Math.round(5.f / 256 * width), 0,
                Math.round(27.f / 1024 * width), 0);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        lp2.width = -1;
        lp2.height = Math.round(127.f / 600 * height);

        RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams) findViewById(R.id.icon).getLayoutParams();
        lp3.width = Math.round(1.f / 30 * height);
        lp3.height = lp3.width;

        int p = Math.round(13.f / 1024 * width);
        mEditText.setPadding(p, 0, p, 0);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        RelativeLayout.LayoutParams lp4 = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        lp4.width = Math.round(117.f / 512 * width);
        lp4.height = Math.round(19.f / 300 * height);

        TextView v = findViewById(R.id.login_label);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.f / 25 * height);
        RelativeLayout.LayoutParams lp5 = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp5.leftMargin = Math.round(7.f / 512 * width);
    }

    @Override
    public void show() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = Math.round(79.f / 256 * metrics.widthPixels);
        lp.height = Math.round(229.f / 600 * metrics.heightPixels);
        super.show();
    }

    @Override
    public void onClick(View view) {
        String text = mEditText.getText().toString();
        if (isEmpty(text)) {
            Toast.makeText(getContext(), "password can not be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mListener.onConfirm(text);
        dismiss();
    }

    public interface OnLoginListener {

        void onConfirm(String pwd);
    }
}
