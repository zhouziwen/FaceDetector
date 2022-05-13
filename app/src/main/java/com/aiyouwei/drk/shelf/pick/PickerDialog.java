package com.aiyouwei.drk.shelf.pick;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;

public class PickerDialog extends Dialog implements View.OnClickListener {

    private static final int TIME_COUNT = 59;

    private static final int UPDATE_TIME = 1;

    private int mCount = TIME_COUNT;

    private TextView mTimeText;

    private String mUserId;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != UPDATE_TIME) return;

            if (mCount <= 0) {
                dismiss();
            } else {
                mCount--;
                mTimeText.setText(String.format("(%ss)", mCount));
                sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
            }
        }
    };

    public PickerDialog(Context context, String userId) {
        super(context, R.style.FullHeightDialog);
        setContentView(R.layout.pick_dialog_layout);
        mUserId = userId;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Employee e = DatabaseHelper.getInstance(context).queryEmployee(userId);

        mTimeText = ((TextView) findViewById(R.id.time));

        CircleImageView imageView = (CircleImageView) findViewById(R.id.avatar);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp.height = Math.round(3.f / 15 * height);
        lp.width = lp.height;

        String photo = e.photo;
        if (!isEmpty(photo)) {
            byte[] bytes = Base64.decode(photo, Base64.DEFAULT);
            if (null != bytes && bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }

        TextView nameText = (TextView) findViewById(R.id.name);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.f / 300 * height);
        nameText.setText(e.username);
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) nameText.getLayoutParams();
        lp1.topMargin = Math.round(4.f / 150 * height);

        TextView idText = (TextView) findViewById(R.id.user_id);
        idText.setText(String.format("User ID: %s", mUserId));
        idText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) idText.getLayoutParams();
        lp2.topMargin = Math.round(1.f / 150 * height);

        TextView pickBtn = (TextView) findViewById(R.id.btn_picking);
        pickBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        pickBtn.setOnClickListener(this);
        LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) pickBtn.getLayoutParams();
        lp3.width = Math.round(15.f / 64 * width);
        lp3.height = Math.round(9.f / 100 * height);
        lp3.topMargin = Math.round(3.f / 40 * height);

        TextView idenBtn = (TextView) findViewById(R.id.btn_identify);
        idenBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        idenBtn.setOnClickListener(this);
        LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) idenBtn.getLayoutParams();
        lp4.width = lp3.width;
        lp4.height = lp3.height;
        lp4.topMargin = Math.round(1.f / 30 * height);

        mTimeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) mTimeText.getLayoutParams();
        lp5.leftMargin = Math.round(5.f / 256 * width);
        lp5.topMargin = Math.round(1.f / 30 * height);

        mTimeText.setText("(60s)");
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_picking) {
            Intent intent = new Intent(getContext(), PickerActivity.class);
            intent.putExtra("userId", mUserId);
            getContext().startActivity(intent);
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        mHandler.removeCallbacks(null);
        mHandler.removeMessages(UPDATE_TIME);
        mHandler = null;
        super.dismiss();
    }

    @Override
    public void show() {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = Math.round(153.f / 256 * metrics.widthPixels);
        lp.height = Math.round(113.f / 150 * metrics.heightPixels);
        super.show();
    }
}
