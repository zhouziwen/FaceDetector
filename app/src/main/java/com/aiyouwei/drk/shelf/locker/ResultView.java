package com.aiyouwei.drk.shelf.locker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;

public class ResultView extends LinearLayout {

    private static final int TIME_COUNT = 5;

    private static final int UPDATE_TIME = 1;

    private int mCount = TIME_COUNT;

    private OnTimeEndListener listener;

    private TextView mTextView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != UPDATE_TIME) return;

            if (mCount <= 0) {
                listener.onTimeOut();
            } else {
                mCount--;
                setTimeText(mCount);
                sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
            }
        }
    };

    public ResultView(Context context) {
        this(context, null);
    }

    public ResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setUserId(String userId, OnTimeEndListener listener) {
        removeAllViews();

        this.listener = listener;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Context ctx = getContext();

        Employee e = DatabaseHelper.getInstance(ctx).queryEmployee(userId);

        RelativeLayout avatarLayout = new RelativeLayout(ctx);
        addView(avatarLayout, new LayoutParams(-2, -2));

        int w = Math.round(1.f / 5 * height);
        CircleImageView imageView = new CircleImageView(ctx);
        imageView.setId(R.id.avatar);
        String photo = e.photo;
        if (!isEmpty(photo)) {
            byte[] bytes = Base64.decode(photo, Base64.DEFAULT);
            if (null != bytes && bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }
        avatarLayout.addView(imageView, new RelativeLayout.LayoutParams(w, w));

        w = Math.round(13.f / 300 * height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, w);
        lp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.avatar);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.avatar);
        ImageView icon = new ImageView(ctx);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageResource(R.drawable.ic_success);
        avatarLayout.addView(icon, lp);

        LayoutParams lp1 = new LayoutParams(-2, -2);
        lp1.topMargin = Math.round(2.f / 75 * height);
        TextView nameText = new TextView(ctx);
        nameText.setText(e.username);
        nameText.setTextColor(Color.WHITE);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.f / 300 * height);
        addView(nameText, lp1);

        w = Math.round(45.f / 128 * width);
        int h = Math.round(w * 48.f / 271);
        LayoutParams lp2 = new LayoutParams(w, h);
        lp2.topMargin = Math.round(3.f / 50 * height);
        LinearLayout line = new LinearLayout(ctx);
        line.setOrientation(HORIZONTAL);
        line.setBackgroundResource(R.drawable.bg_text);
        addView(line, lp2);

        LayoutParams lp3 = new LayoutParams(h, -1);
        mTextView = new TextView(ctx);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.f / 100 * height);
        mTextView.setTextColor(0xFF00FFDF);
        mTextView.setTypeface(null, Typeface.BOLD);
        mTextView.setGravity(Gravity.CENTER);
        line.addView(mTextView, lp3);

        LayoutParams lp4 = new LayoutParams(-1, -1);
        TextView label = new TextView(ctx);
        label.setText("Lock is open，entering please");
        label.setTextColor(Color.WHITE);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.f / 100 * height);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER);
        line.addView(label, lp4);

        setTimeText(5);
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
    }

    private void setTimeText(int t) {
        mTextView.setText(String.format("%ds", t));
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(null);
        mHandler.removeMessages(UPDATE_TIME);
        mHandler = null;
        super.onDetachedFromWindow();
    }
}
