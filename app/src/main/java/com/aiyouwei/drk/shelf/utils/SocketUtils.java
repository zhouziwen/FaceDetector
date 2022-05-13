package com.aiyouwei.drk.shelf.utils;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.model.DeviceInfo;
import com.aiyouwei.drk.shelf.widget.ItemListView;
import com.alibaba.fastjson.JSONArray;
import com.zhangke.zlog.ZLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketUtils {

    private  final String TAG = SocketUtils.class.getSimpleName();

    private static SocketUtils socketUtils;

    private  final  String updateUser = "updateUserSession";

    private  final String menuViewUser = "menuViewUserSession";

    private  final String clearUserSession = "clearUserSession";

    public static   final  String MSG = "message";

    private final String path = "/broker-wired";


    public static SocketUtils getInstance(){

        if(socketUtils ==null){

            socketUtils = new SocketUtils();

        }

        return socketUtils;
    }

    /**
     * 建立 Socket 连接
     */
    @Nullable
    private Socket getSocket(final String domain){

        IO.Options options = new IO.Options();

        options.reconnection  = false;
//        options.reconnectionDelayMax = 2000;
//        options.reconnectionDelay = 1000;
//        options.reconnectionAttempts = 2000;
        options.forceNew = true;

        try {

            mSocket = IO.socket(domain,options);

        } catch (URISyntaxException e) {

            ZLog.e(TAG,e.getMessage(),e);

            e.printStackTrace();

            return null;
        }

        /*
            进行连接
         */
        mSocket.connect();

        return mSocket;
    }

   private Timer timer;
   private Socket mSocket;

    /**
     * 用户数据更新的socket连接
     */
    public void  getUserSessionSocket(){

        String domain = Config.getSocketDomain(AiYouWei.getInstance());

        if(TextUtils.isEmpty(domain)){

            Log.e(TAG,"Socket Domain Name can not be empty");

            return;
        }


        String port = Config.getSocketPort(AiYouWei.getInstance());

        domain += ":"+port;

        domain += path;

        IO.Options options = new IO.Options();
        options.reconnection  = false;
        options.forceNew = true;

        try {

            mSocket = IO.socket(domain,options);

        } catch (URISyntaxException e) {

            ZLog.e(TAG,e.getMessage(),e);

            e.printStackTrace();

            return;
        }

        /*
            进行连接
         */
        mSocket.connect();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("is_connect",true);

            jsonObject.put("shelf", Config.getShelf(AiYouWei.getInstance()));

        } catch (JSONException e) {

            e.printStackTrace();
        }

        if(mSocket == null){
            return;
        }

        mSocket.emit(menuViewUser,jsonObject);

        mSocket.on(clearUserSession, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                mHandler.sendEmptyMessage(1);
            }
        });

        mSocket.on(updateUser, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                if(args == null || args.length<=0){

                    return;

                }
                String  receiveData = Arrays.toString(args);

                com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(receiveData);

                for (Object data :
                        jsonArray) {

                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(data)) ;

                    String rs =   String.valueOf(jsonObject.get("rs"));

                    JSONArray jsonArrayRs = JSONArray.parseArray(rs);

                    if(jsonArrayRs!=null && jsonArrayRs.size()>0){

                        ZLog.i(TAG, "rs = "+rs);

                        final List<DeviceInfo> deviceInfoList = parseDeviceInfo(jsonArrayRs);

                        Message mMessage = mHandler.obtainMessage();
                        mMessage.what = 0;
                        mMessage.obj = deviceInfoList;
                        mHandler.removeMessages(mMessage.what);
                        mHandler.sendMessage(mMessage);

                    }
                }
            }
        });

        if(timer==null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!mSocket.connected()){
                        mSocket.close();
                        mSocket = null;
                        getUserSessionSocket();
                    }
                }
            },10000,10000);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                    List<DeviceInfo> itemInfoList = new ArrayList<>();
                    List<DeviceInfo> deviceInfoList = (List<DeviceInfo>)msg.obj;
                    for (DeviceInfo deviceInfo:
                    deviceInfoList) {
                        if(deviceInfo.getBinId() !=0 && deviceInfo.getLastPickQty()!=0){
                            itemInfoList.add(deviceInfo);
                        }
                    }
                    ItemListView.mAdapter.addData(itemInfoList);
                    break;
                case 1:
                    ItemListView.mAdapter.clear();
                    break;
            }

        }
    };





    @NotNull
    private  List<DeviceInfo> parseDeviceInfo(@NotNull JSONArray jsonArray) {

        List<DeviceInfo> list = new ArrayList<>();

        for (Object o : jsonArray) {

            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(o));

            String sessionId = jsonObject.getString("session-id");

            DeviceInfo deviceInfo = new DeviceInfo();

            deviceInfo.setFullName(jsonObject.getString("full-name"));
            deviceInfo.setSessionId(sessionId);
            deviceInfo.setItemName(jsonObject.getString("item-name"));
            deviceInfo.setPartNumber(jsonObject.getString("part-number"));
            deviceInfo.setRoom(jsonObject.getString("room"));
            deviceInfo.setShelf(jsonObject.getString("shelf"));
            deviceInfo.setBinName(jsonObject.getString("bin-name"));
            deviceInfo.setPersonId(jsonObject.getInteger("person-id"));
            deviceInfo.setBinId(jsonObject.getInteger("bin-id"));
            deviceInfo.setCalWeightQty(jsonObject.getInteger("cal-weight-qty"));
            deviceInfo.setPreQty(jsonObject.getInteger("pre-qty"));
            deviceInfo.setQty(jsonObject.getInteger("qty"));
            deviceInfo.setSessionQty(jsonObject.getInteger("session-qty"));
            deviceInfo.setLastPickQty(jsonObject.getInteger("last-pick-qty"));
            deviceInfo.setLastUpdated(jsonObject.getDate("last-updated"));

            list.add(deviceInfo);
        }
        return  list;
    }


    /**
     *  发送识别出的人脸信息
     */
    public Socket getSendFaceSocket(){

            String domain = Config.getSocketDomain(AiYouWei.getInstance());


            if(TextUtils.isEmpty(domain)){

                Log.e(TAG,"Socket Domain Name can not be empty");

                return null;
            }

            String port = Config.getSocketPort(AiYouWei.getInstance());

            domain += ":"+port;

        return  getSocket(domain);

    }


}
