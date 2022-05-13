package com.aiyouwei.drk.shelf;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Debug;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.admin.AdminActivity;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.admin.password.LoginDialog;
import com.aiyouwei.drk.shelf.admin.password.SettingsActivity;
import com.aiyouwei.drk.shelf.home.RightPaneLayout;
import com.aiyouwei.drk.shelf.home.TipsLayout;
import com.aiyouwei.drk.shelf.pick.PickerDialog;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.ThreadPoolUtil;
import com.aiyouwei.drk.shelf.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import static android.text.TextUtils.isEmpty;
import static android.widget.FrameLayout.LayoutParams;

public class MainActivity extends FaceDetectActivity implements LoginDialog.OnLoginListener, DialogInterface.OnDismissListener {

    public  static  final  String CLEAR_SHELF = "clearShelfData";

    private Future future;

    private    int times = 0;

    private  final  int cleanTimes = 30;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private  TextView showUserId;
    @Override
    protected void setupView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ImageView titleBg = (ImageView) findViewById(R.id.iv_title_bg);
        int h = Math.round(79.0f / 600 * height);
        int w = Math.round(25.f / 32 * width);
        LayoutParams lp = (LayoutParams) titleBg.getLayoutParams();
        lp.width = w;
        lp.height = h;

        LayoutParams layoutParams = (LayoutParams) rgbView.getLayoutParams();
        h = Math.round(0.45f * height);
        w = Math.round(0.3734375f* width);
        layoutParams.width = w;
        layoutParams.height = h;
        viewWidth = w;
        viewHeight = h;
        layoutParams.leftMargin = Math.round(0.03984375f * width);
        layoutParams.topMargin = Math.round(0.27f * height);

        LayoutParams irlp = (LayoutParams) irView.getLayoutParams();
        irlp.width = layoutParams.width;
        irlp.height = layoutParams.height;
        irlp.leftMargin = layoutParams.leftMargin;
        irlp.topMargin = layoutParams.topMargin;

        LayoutParams fl = (LayoutParams) faceRectView.getLayoutParams();
        fl.width = w;
        fl.height = h;
        fl.leftMargin = Math.round(0.03984375f * width);
        fl.topMargin = Math.round(0.2575f * height);

        ImageView titleView = (ImageView) findViewById(R.id.iv_title);
        titleView.setOnLongClickListener(this);
        h = Math.round(3.f / 50 * height);
        w = Math.round(181.f / 512 * width);
        LayoutParams lp1 = (LayoutParams) titleView.getLayoutParams();
        lp1.width = w;
        lp1.height = h;
        lp1.topMargin = Math.round(17.f / 600 * height);

        TipsLayout tipLayout = (TipsLayout) findViewById(R.id.tip_layout);
        LayoutParams lp3 = (LayoutParams) tipLayout.getLayoutParams();
//        w = Math.round(75.f / 128 * width);
//        h = Math.round(1.f / 5 * height);
        h = Math.round(31.f / 77 * height);
        w = Math.round(600.f * h / 380);
        lp3.width = w;
        lp3.height = h;
        lp3.leftMargin = Math.round(5.f / 128 * width);
        lp3.topMargin = 10;
        lp3.bottomMargin = Math.round(1.f / 60 * height);

        ImageView logo = (ImageView) findViewById(R.id.iv_logo);
        logo.setOnLongClickListener(this);
        LayoutParams lp5 = (LayoutParams) logo.getLayoutParams();
        h = Math.round(47.f / 600 * height);
        lp5.width = Math.round(41.f / 256 * width);
        lp5.height = h;
        lp5.topMargin = Math.round(31.f / 600 * height);
        lp5.leftMargin = Math.round(10.f / 1024 * width);

        RightPaneLayout rightPane =findViewById(R.id.right_pane);
        LayoutParams lp6 = (LayoutParams) rightPane.getLayoutParams();
        lp6.rightMargin = Math.round(5.f / 128 * width);
        lp6.topMargin = Math.round(23.f / 150 * height);

         showUserId = findViewById(R.id.show_user_id);
        LayoutParams lp7 = (LayoutParams) showUserId.getLayoutParams();
        lp7.topMargin = Math.round(31.f / 182 * height);
        lp7.leftMargin =  Math.round(31.f / 720 * width);
        this.showUserId.setTextSize(0.06f* height);
        this.showUserId.setTextColor(Color.BLACK);
        this.showUserId.bringToFront();
        chickService();
        detectTimer();
    }

    private void chickService(){

        String service =  Config.getServiceDomain(MainActivity.this);

        String socket =  Config.getSocketDomain(MainActivity.this);

        String code =  Config.getActiveKey(MainActivity.this);

        if(TextUtils.isEmpty(service)  || TextUtils.isEmpty(socket)|| TextUtils.isEmpty(code)){

            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void detectSuccess(String userId) {
        //      showPickerDialog(userId);

        detectTimer();

        Employee employee = DatabaseHelper.getInstance(MainActivity.this).queryEmployee(userId);

        this.showUserId.setText("User : "+employee.username);

    }


    private  void   detectTimer(){

        this.times = 0;

        if( this.future == null ){

            MainActivity.this.future =  ThreadPoolUtil.callBackFailThreadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    if(MainActivity.this.times >= MainActivity.this.cleanTimes){

                        cancelFuture();
                    }

                    MainActivity.this.times++;

                    Log.e("times","mem = "+ getRunningAppProcessInfo() +"m");

                }


            },0,1, TimeUnit.SECONDS);
        }
    }


    /**
     * 获取app运行占用内存
     */
    @SuppressLint("DefaultLocale")
    public String getRunningAppProcessInfo() {

        int totalPss = 0;

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        //获得系统里正在运行的所有进程  

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {

            // 进程ID号         
            int pid = runningAppProcessInfo.pid;

            // 进程名          
            String processName = runningAppProcessInfo.processName;

            if( BuildConfig.APPLICATION_ID.equals(processName)){

                // 占用dao的内存         
                int[] pids = new int[]{pid};

                Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pids);

                 totalPss = memoryInfo[0].getTotalPss();

            }

        }

        return String.format("%.2f",Utils.convertSize(totalPss));
    }


    private void cancelFuture(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showUserId.setText(R.string.user);
            }
        });

        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(CLEAR_SHELF));

        times = 0;
    }

    private void openAdmin() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(@NotNull View view) {
        if (view.getId() == R.id.iv_title) {
            showToast("Reloading face data");
            Intent intent = new Intent(this, SyncService.class);
            startService(intent);
        } else {
            if (Config.isLogin(this)) {
                openAdmin();
            } else {
                LoginDialog dialog = new LoginDialog(this, this);
                dialog.show();
            }
        }

        return true;
    }

    @Override
    public void onConfirm(final String pwd) {
        showProgress();
        Map<String, String> params = new HashMap<>();
        params.put("password", pwd);
        NetworkRequest.getInstance().get(AiYouWei.getInstance().getServiceUrl(),Constants.GET_TOKEN, params, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                Context ctx = MainActivity.this;
                try {
                    org.json.JSONObject jObj = new org.json.JSONObject(response);
                    String token = jObj.optString("token", "");
                    if (!isEmpty(token)) {
                        Config.saveToken(ctx, token);
                        Config.savePassword(ctx, pwd);
                        openAdmin();
                    } else {
                        Utils.showToast(ctx, "password not correct");
                    }
                } catch (JSONException e) {
                    Utils.showToast(ctx, R.string.json_invalid);
                }
                hideProgress();
            }

            @Override
            public void onErrorResponse(String e) {
                hideProgress();
                Utils.showToast(MainActivity.this, "network invalid or server error, error: " + e);
            }
        });
    }

    private void showPickerDialog(String userId) {
        PickerDialog dialog = new PickerDialog(this, userId);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        rgbView.start();
        irView.start();
    }

}
