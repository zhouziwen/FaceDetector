package com.aiyouwei.drk.shelf.pick;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aiyouwei.drk.shelf.Access;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.home.ItemsViewHolder;
import com.aiyouwei.drk.shelf.model.DeviceInfo;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.Utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemGridView extends FrameLayout implements  Listener<String> {

    public static final long TIME_DELAY = 5000L;

    public static final int MSG_UPDATE = 344;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DisplayMetrics metrics;

    private float density;

    private int itemHeight;

    private ItemGridListAdapter adapter;

    private Gson gson;


    private  List<DeviceInfo>  originalList;

    private  Map<Integer,DeviceInfo>  originalMap;

    private  Map<Integer,DeviceInfo>  newMap;

    private  List<DeviceInfo>  showList;

    private  List<DeviceInfo>  takeList;

    private Date startTime;

    public static  BroadcastReceiver mBroadcastReceiver;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what != MSG_UPDATE) return;
//            request();
        }
    };

    public ItemGridView(Context context) {
        this(context, null);
    }

    public ItemGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        setWillNotDraw(false);
        paint.setColor(0x4F006CFF);
        paint.setStrokeWidth(density);
        paint.setStyle(Paint.Style.STROKE);

        adapter = new ItemGridListAdapter(context);
        gson = new GsonBuilder().create();
        newMap = new ConcurrentHashMap<>();
        originalMap = new ConcurrentHashMap<>();
        showList =  new ArrayList<>();
        takeList =  new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setBackgroundColor(0x4F006CFF);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        int margin = Math.round(density * 3);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.setMargins(margin, margin, margin, margin);
        addView(recyclerView, lp);

        originalList  = new ArrayList<>();
        startTime = new Date();

//        request();

         mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JSONArray jsonArray = new JSONArray();

                String userid = intent.getStringExtra("userid");

                for (DeviceInfo deviceInfo:
                        showList) {
//                    jsonArray.add(deviceInfo.toJsonObject());
                }

                Map<String,Object> sendInfo = new ConcurrentHashMap<>();

                sendInfo.put("userId",userid);

                sendInfo.put("shelfId", Config.getShelf(context));

                sendInfo.put("roomId", Config.getRoom(context));

                sendInfo.put("startTime", Utils.date2String(startTime));

                sendInfo.put("endTime",Utils.getStrTime());

                sendInfo.put("itemData",jsonArray);

                NetworkRequest.getInstance().post(Access.getInstance().ACCESS_URL, Access.getInstance().SAVE_RECORD, sendInfo, new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject  = JSONObject.parseObject(response);

                            String msg = jsonObject.getString("msg");

                            if(!TextUtils.isEmpty(msg)){

                                Log.e(Access.getInstance().SAVE_RECORD,msg);

                            }

                        }catch (Exception e){

                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onErrorResponse(String e) {

                        Log.e(Access.getInstance().SAVE_RECORD,e);

                    }
                });

            }
        };

        IntentFilter filter = new IntentFilter();

        filter.addAction(Access.getInstance().SAVE_RECORD);

        context.registerReceiver(mBroadcastReceiver, filter);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (itemHeight == 0) {
            itemHeight = Math.round((getHeight() * 1.f - density * 3 * 2) / 10);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        canvas.drawRect(density, density, w * 1.f - density, h * 1.f - density, paint);
    }

//    private void request() {
//        Map<String, String> params = new HashMap<>();
//        params.put("shelfId",Config.getShelf(c));
//        params.put("roomId",  Access.getInstance().roomId);
//        NetworkRequest.getInstance().get(Access.getInstance().ACCESS_URL,Access.getInstance().GET_INVENTORY_RECORD, params, this);
//    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeMessages(MSG_UPDATE);
        super.onDetachedFromWindow();
    }

    //TODO  ------------测试数据--------------
    private  int index = 1;
    @Override
    public void onResponse(String response) {
//
//        handler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_DELAY);
//
//        if (TextUtils.isEmpty(response)) return;
//
//        List<DeviceInfo> newList = gson.fromJson(response, new TypeToken<List<DeviceInfo>>(){}.getType());
//
//        if (null == newList || newList.size() == 0) return;
//
//        for (DeviceInfo deviceInfo:
//                newList) {
//
//            //TODO  ------------测试数据--------------
//            if(deviceInfo.itemId == 57){
//
//                if(index%2 == 0)
//                deviceInfo.number = index;
//
//            }else {
//
//                if(index%2 != 0)
//                    deviceInfo.number = index;
//
//            }
//            //TODO  ------------测试数据--------------
//
//            newMap.put(deviceInfo.itemId,deviceInfo);
//
//        }
//
//        showList.clear();
//
//        if(  originalList== null || originalList.size()==0){
//
//            originalList = newList;
//
//            for (DeviceInfo deviceInfo:
//                    newList) {
//
//                originalMap.put(deviceInfo.itemId,deviceInfo);
//
//            }
//
//        }else {
//
//            for (DeviceInfo originalDeviceInfo:
//                    originalList) {
//
//              int itemId =  originalDeviceInfo.itemId;
//
//              DeviceInfo newDeviceInfo = newMap.get(itemId);
//
//              if(newDeviceInfo ==null) return;
//
//              if(originalDeviceInfo.number !=  newDeviceInfo.number){
//
//                  originalDeviceInfo.number  =  originalDeviceInfo.number -  newDeviceInfo.number ;
//
//                  showList.add(originalDeviceInfo);
//              }
//
//            }
//
//        }
//
//        adapter.clear();
//
//        adapter.addData(showList);
//
//        if(showList!=null  && showList.size()>0){
//
//            takeList.clear();
//
//            takeList.addAll(showList);
//
//
//        }
//
//        //TODO  ------------测试数据--------------
//        index++;
    }


    @Override
    public void onErrorResponse(String e) {
        handler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_DELAY);
    }

    private class ItemGridListAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

        private final LayoutInflater inflater;

        private final List<DeviceInfo> dataList = new ArrayList<>();

        private float textSize;

        public ItemGridListAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
            textSize = 3.f / 100 * metrics.heightPixels;
        }

        public void addData(List<DeviceInfo> list) {
            dataList.addAll(list);
            notifyDataSetChanged();
        }

        public void clear() {
            dataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_layout, parent, false);
            return new ItemsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemsViewHolder holder, int position) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp.height != itemHeight) {
                lp.height = itemHeight;
            }
            holder.nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            holder.countText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            DeviceInfo info = dataList.get(position);
//            String name = String.format("%d.   %s", position + 1, info.itemName);
//            holder.nameText.setText(name);
//            holder.countText.setText(String.format("x%d", info.number));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }



}
