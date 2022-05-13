package com.aiyouwei.drk.shelf.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.MainActivity;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.home.ItemsViewHolder;
import com.aiyouwei.drk.shelf.home.SubheadLayout;
import com.aiyouwei.drk.shelf.model.DeviceDoc;
import com.aiyouwei.drk.shelf.model.DeviceInfo;
import com.aiyouwei.drk.shelf.pick.ItemGridView;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.SocketUtils;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemListView extends FrameLayout implements Listener<String>{

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DisplayMetrics metrics;

    public   static ItemListAdapter mAdapter;

    private float density;

    private int mPage = 1;

    LocalBroadcastManager broadcastManager;

    IntentFilter intentFilter;

    BroadcastReceiver mReceiver;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what != ItemGridView.MSG_UPDATE) return;
              mPage++;
//            request();
        }
    };

    public ItemListView(Context context) {
        this(context, null);
    }

    public ItemListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemListView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        density = metrics.density;
        setWillNotDraw(false);
        paint.setColor(0x4F006CFF);
        paint.setStrokeWidth(density);
        paint.setStyle(Paint.Style.STROKE);

        SubheadLayout subheadLayout  =new SubheadLayout(context);
        LayoutParams lp1 = new LayoutParams(-1, -1);
        lp1.width = Math.round(39.f / 75.4f * width);
        lp1.height = Math.round(0.078f * height);
        lp1.topMargin = Math.round(0.01f*height);
        lp1.leftMargin = Math.round(0.0078125f*width);
        subheadLayout.setBackgroundColor(0x4F006CFF);
        subheadLayout.setGravity(Gravity.TOP);
        addView(subheadLayout,lp1);

        mAdapter = new ItemListAdapter();

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setBackgroundColor(0x4F006CFF);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.height = Math.round(0.6f *height);
        lp.topMargin = Math.round(0.01f*height)+Math.round(0.078f * height);
        lp.width = Math.round(39.f / 75.4f * width);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        addView(recyclerView, lp);
//        request();
        broadcastManager = LocalBroadcastManager.getInstance(context);

        intentFilter = new IntentFilter();

        intentFilter.addAction(MainActivity.CLEAR_SHELF);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //收到广播后所作的操作
                mAdapter.clear();
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);

        /*
            用户信息更新显示
         */
        SocketUtils.getInstance().getUserSessionSocket();

    }

//    private void request() {
//        Map<String, String> params = new HashMap<>();
//        params.put("shelfId", Access.getInstance().shelfId);
//        params.put("roomId", Access.getInstance().roomId);
//        NetworkRequest.getInstance().get(Access.getInstance().ACCESS_URL,Access.getInstance().GET_INVENTORY_RECORD, params, this);
//    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeMessages(ItemGridView.MSG_UPDATE);
        super.onDetachedFromWindow();
    }

    @Override
    public void onResponse(String response) {

//        handler.sendEmptyMessageDelayed(ItemGridView.MSG_UPDATE, ItemGridView.TIME_DELAY);
//
//        if (TextUtils.isEmpty(response)) return;
//
//        List<DeviceInfo> list = gson.fromJson(response, new TypeToken<List<DeviceInfo>>(){}.getType());
//
//        if (null == list || list.size() == 0) return;
//
//        mAdapter.clear();
//
//        mAdapter.addData(list);

    }

    @Override
    public void onErrorResponse(String e) {
        handler.removeMessages(ItemGridView.MSG_UPDATE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        canvas.drawRect(density, density, w * 1.f - density, h * 1.f - density, paint);
    }


    public class ItemListAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

        private final LayoutInflater inflater;

        private int height;

        private    List<DeviceInfo> dataList = new ArrayList<>();
        private  final  Map<String,String> map = new ConcurrentHashMap<>();

        public ItemListAdapter() {


            inflater = LayoutInflater.from(getContext());

            height = Math.round(41.f / 60 * metrics.heightPixels / 10);
        }

        public void addData(List<DeviceInfo> list) {
            dataList=list;
            notifyDataSetChanged();
        }

        public  void clear() {
            dataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_layout, parent, false);
            return new ItemsViewHolder(v);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NotNull ItemsViewHolder holder, int position) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp.height != height) {
                lp.height = height;
            }
            DeviceInfo info = dataList.get(position);
            @SuppressLint("DefaultLocale")
            String name = String.format("%d.   %s", position + 1, info.getItemName());
            holder.nameText.setText(name);
            holder.nameText.setTextColor(getResources().getColor(R.color.blue));
            holder.pnText.setText(info.getPartNumber());
            holder.pnText.setTextColor(getResources().getColor(R.color.blue));
            holder.binText.setText(info.getBinName());
            holder.binText.setTextColor(getResources().getColor(R.color.blue));
            Integer qty = info.getLastPickQty();
            if(qty>=0){
                holder.countText.setTextColor(getResources().getColor(R.color.blue));
            }else {
                holder.countText.setTextColor(Color.RED);
            }

            holder.countText.setText(String.valueOf(qty));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}
