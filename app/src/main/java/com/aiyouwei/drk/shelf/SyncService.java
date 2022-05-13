package com.aiyouwei.drk.shelf;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import androidx.annotation.Nullable;
import com.aiyouwei.drk.shelf.admin.FaceCollections;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.admin.db.EmployeeList;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.arcsoft.face.FaceFeature;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class SyncService extends Service implements Listener<EmployeeList> {

    private final static int MSG_CHECK = 233;

    private final static long TIME_DELAY = 2 * 60 * 1000L;

    private final DelayHandler mHandler = new DelayHandler(this);

    private int mPage = 1;

    private DatabaseHelper mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = DatabaseHelper.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sync();
        return START_STICKY;
    }

    public void sync() {
        mPage = 1;
        request();
    }

    private void request() {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage));
        NetworkRequest.getInstance().get(AiYouWei.getInstance().getServiceUrl(),Constants.USER_LIST, params, EmployeeList.class, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResponse(final EmployeeList response) {
        if (mPage == 1) {
            mHandler.sendEmptyMessageDelayed(MSG_CHECK, TIME_DELAY);
        }

        if (null == response || response.size() == 0) return;

        new Thread() {
            @Override
            public void run() {
                saveData(response);
            }
        }.start();
        mPage++;
        request();
    }

    private void saveData(EmployeeList list) {
        mDatabase.batchInsert(list);
        for (Employee e : list) {
            String data = e.facedata;
            if (!isEmpty(data)) {
                byte[] decoded = Base64.decode(data, Base64.DEFAULT);
                if (null != decoded && decoded.length > 0) {
                    FaceCollections.getInstance().addFace(e.userid, new FaceFeature(decoded));
                }
            }
        }
    }

    @Override
    public void onErrorResponse(String e) {
        if (mPage == 1) mHandler.sendEmptyMessageDelayed(MSG_CHECK, TIME_DELAY);
    }

    private static class DelayHandler extends Handler {

        private final WeakReference<SyncService> mService;

        public DelayHandler(SyncService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SyncService service = mService.get();
            if (null == service || MSG_CHECK != msg.what) return;

            service.sync();
        }
    }
}
