package com.aiyouwei.drk.shelf.pick;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiyouwei.drk.shelf.Access;
import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.home.TitleLayout;
import com.aiyouwei.drk.shelf.model.DeviceInfo;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.Utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;

public class PickerActivity extends BaseActivity implements View.OnClickListener, FinishDialog.OnFinishListener {
    private static final int TIME_COUNT = 119;

    private static final int UPDATE_TIME = 1;

    private int mCount = TIME_COUNT;

    private TextView mButton;

    private  Employee employee;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != UPDATE_TIME) return;

            if (mCount <= 0) {
                saveRecord(true);
            } else {
                mCount--;
                mButton.setText(String.format("Click Finish(%ss)", mCount));
                sendEmptyMessageDelayed(UPDATE_TIME, 1000L);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        mButton =  findViewById(R.id.btn_finish);
        mButton.setOnClickListener(this);
        setupView();
    }

    private void setupView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ImageView titleBg = (ImageView) findViewById(R.id.iv_title_bg);
        int h = Math.round(79.0f / 600 * height);
        int w = Math.round(25.f / 32 * width);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) titleBg.getLayoutParams();
        lp.width = w;
        lp.height = h;

        ImageView titleView = findViewById(R.id.iv_title);
        h = Math.round(3.f / 50 * height);
        w = Math.round(381.f / 1024 * width);
        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) titleView.getLayoutParams();
        lp1.width = w;
        lp1.height = h;
        lp1.topMargin = Math.round(17.f / 600 * height);

        ImageView logo = findViewById(R.id.iv_logo);
        FrameLayout.LayoutParams lp5 = (FrameLayout.LayoutParams) logo.getLayoutParams();
        h = Math.round(47.f / 600 * height);
        lp5.width = Math.round(41.f / 256 * width);
        lp5.height = h;
        lp5.topMargin = Math.round(31.f / 600 * height);
        lp5.leftMargin = Math.round(29.f / 1024 * width);

        mButton.setText("Click Finish (120s)");
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000L);

        Bundle args = getIntent().getExtras();
        String userId = args.getString("userId");
        if (isEmpty(userId)) return;

         employee = DatabaseHelper.getInstance(this).queryEmployee(userId);

        TitleLayout titleLayout = findViewById(R.id.title_layout);
        titleLayout.setTitleInfo("Items List", null);
        float splitH = 23.f / 300 * height;
        h = Math.round(splitH);
        w = Math.round(160.f * h / 23);
        LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
        lp6.width = w;
        lp6.height = h;

        LinearLayout.LayoutParams lp7 = (LinearLayout.LayoutParams) (findViewById(R.id.left_pane)).getLayoutParams();
        lp7.width = Math.round(75.f / 128 * width);
        lp7.height = -1;
        lp7.leftMargin = Math.round(41.f / 1024 * width);
        lp7.topMargin = Math.round(23.f / 150 * height);
        lp7.bottomMargin = Math.round(1.f / 15 * height);

        LinearLayout.LayoutParams lp8 = (LinearLayout.LayoutParams) (findViewById(R.id.right_pane)).getLayoutParams();
        lp8.width = -1;
        lp8.height = -1;
        lp8.topMargin = lp7.topMargin;
        lp8.rightMargin = lp7.leftMargin;
        lp8.bottomMargin = lp7.bottomMargin;

        CircleImageView imageView = findViewById(R.id.avatar);
        LinearLayout.LayoutParams lp9 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp9.width = Math.round(1.f / 5 * height);
        lp9.height = lp9.width;
        lp9.topMargin = Math.round(23.f / 300 * height);
        String photo = employee.photo;
        if (!isEmpty(photo)) {
            byte[] bytes = Base64.decode(photo, Base64.DEFAULT);
            if (null != bytes && bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }

        TextView nameText = findViewById(R.id.name);
        nameText.setText(employee.username);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.f / 300 * height);
        ((LinearLayout.LayoutParams) nameText.getLayoutParams()).topMargin = Math.round(2.f / 75 * height);

        TextView idText = findViewById(R.id.user_id);
        idText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        idText.setText(String.format("User ID: %s", employee.userid));
        ((LinearLayout.LayoutParams) idText.getLayoutParams()).topMargin = Math.round(1.f / 150 * height);

        LinearLayout.LayoutParams lp10 = (LinearLayout.LayoutParams) mButton.getLayoutParams();
        mButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);
        lp10.width = Math.round(15.f / 64 * width);
        lp10.height = Math.round(9.f / 100 * height);
        lp10.topMargin = Math.round(61.f / 600 * height);
    }

    @Override
    public void onClick(View view) {
        FinishDialog dialog = new FinishDialog(this, this);
        dialog.show();
        saveRecord(false);
    }


    private  void  saveRecord(boolean finish){

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Access.getInstance().SAVE_RECORD);
        intent.putExtra("userid",employee.userid);
        PickerActivity.this.sendBroadcast(intent);

        if(finish){
            finish();
        }
    }



    @Override
    public void onBackHomeClicked() {
        finish();
    }


    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(null);
        mHandler.removeMessages(UPDATE_TIME);
        mHandler = null;

        if(ItemGridView.mBroadcastReceiver!=null) unregisterReceiver(ItemGridView.mBroadcastReceiver);

        super.onDestroy();
    }
}
