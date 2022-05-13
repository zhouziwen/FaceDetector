package com.aiyouwei.drk.shelf.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.OnTabSelectedListener;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.admin.db.EmployeeList;
import com.aiyouwei.drk.shelf.admin.password.PasswordActivity;
import com.aiyouwei.drk.shelf.admin.password.SettingsActivity;
import com.aiyouwei.drk.shelf.admin.profile.ProfileActivity;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.widget.OnListScrollListener;
import com.aiyouwei.drk.shelf.widget.SplitItemDecoration;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends BaseActivity implements View.OnClickListener, OnListScrollListener.OnPageEndListener,
        Listener<EmployeeList>, OnTabSelectedListener {
    private static final int REQUEST_CODE_ADD = 342;
    private static final int REQUEST_CODE_UPDATE = 343;
    private static final int REQUEST_CODE_PWD = 344;
    private static final int REQUEST_CODE_SET = 345;

    private EmployeeAdapter mAdapter;

    private OnListScrollListener mScrollListener;

    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        findViewById(R.id.btn_add).setOnClickListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabClickedListener(this);

        mAdapter = new EmployeeAdapter(this);
        initTitle();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mScrollListener = new OnListScrollListener(layoutManager, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(mScrollListener);
        recyclerView.addItemDecoration(new SplitItemDecoration(this));
        recyclerView.setAdapter(mAdapter);

        showProgress();
        request();
    }

    private void initTitle() {
        LinearLayout line = (LinearLayout) findViewById(R.id.title_layout);
        line.setWeightSum(4);

        float density = getResources().getDisplayMetrics().density;

        String[] titles = getResources().getStringArray(R.array.titles);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1);
        lp.weight = 1;
        for (int i = 0; i < titles.length; i++) {
            TextView v = new TextView(this);
            v.setText(titles[i]);
            v.setTextColor(0xFF00FFDF);
            v.setTextSize(TypedValue.COMPLEX_UNIT_PX, density * 18);
            v.setGravity(Gravity.CENTER);
            line.addView(v, lp);
        }
    }

    @Override
    public void onPageEnd() {
        mPage++;
        request();
    }

    @Override
    public void onTabSelected(int idx) {
        if (idx == 1) {
            Intent intent = new Intent(this, PasswordActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PWD);
        }
        if (idx == 2) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SET);
        }

        if (idx == 3) {
            Config.saveToken(this, "");
            finish();
        }
    }

    private void request() {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(mPage));
        params.put("type", "simple");

        NetworkRequest.getInstance().get(AiYouWei.getInstance().getServiceUrl(),Constants.USER_LIST, params, EmployeeList.class, this);
    }

    @Override
    public void onResponse(EmployeeList response) {
        mScrollListener.setIsLoading(false);
        hideProgress();
        if (null == response || response.size() == 0) return;

        if (mPage == 1) mAdapter.clear();
        mAdapter.addData(response);
    }

    @Override
    public void onErrorResponse(String e) {
        mScrollListener.setIsLoading(false);
        hideProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("test", requestCode + " resul " + resultCode);
        if (requestCode == REQUEST_CODE_ADD && resultCode == ProfileActivity.RESULT_CODE_ADD) {
            mPage = 1;
            request();
        }

        if (requestCode == REQUEST_CODE_PWD && resultCode == PasswordActivity.RESULT_CODE_SUCCESS) {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                break;
        }
    }

    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> implements View.OnClickListener {

        private LayoutInflater inflater;

        private final List<Employee> dataList = new ArrayList<>();

        public EmployeeAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        public void addData(List<Employee> list) {
            dataList.addAll(list);
            notifyDataSetChanged();
        }

        public void clear() {
            dataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.cell_employee, parent, false);
            return new EmployeeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(EmployeeViewHolder holder, int position) {
            Employee e = dataList.get(position);

            holder.idText.setText(e.userid);
            holder.nameText.setText(e.username);
            holder.faceText.setText(e.isFaceData == 1 ? "Recorded" : "Unrecorded");
            holder.faceText.setCompoundDrawablesWithIntrinsicBounds(e.isFaceData == 1 ?
                    R.drawable.icon_recorded : R.drawable.icon_unrecorded, 0, 0, 0);
            holder.deleteBtn.setOnClickListener(this);
            holder.deleteBtn.setTag(e.id);
            holder.deleteBtn.setTag(R.id.user_id, e.userid);
            holder.editBtn.setOnClickListener(this);
            holder.editBtn.setTag(e.id);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public void onClick(View view) {
            if (null == view.getTag()) return;

            int id = (Integer) view.getTag();
            if (view.getId() == R.id.btn_delete) {
                String userId = (String) view.getTag(R.id.user_id);
                DatabaseHelper.getInstance(AdminActivity.this).deleteEmployee(userId);
                FaceCollections.getInstance().remove(userId);
                deleteUser(id);
            } else {
                Intent intent = new Intent(AdminActivity.this, ProfileActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        }
    }

    private void deleteUser(int id) {
        showProgress();
        Map<String, Object> params = new HashMap<>();
        params.put("action", "del");
        params.put("id", String.valueOf(id));

        NetworkRequest.getInstance().post(AiYouWei.getInstance().getServiceUrl(),Constants.EDIT_USER, params, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    JSONObject jObj = new JSONObject(response);
                    showToast(jObj.optString("info"));
                    boolean result = jObj.optBoolean("result");
                    if (result) {
                        mPage = 1;
                        request();
                    }
                } catch (JSONException e) {
                    showToast(R.string.json_invalid);
                }
            }

            @Override
            public void onErrorResponse(String e) {
                hideProgress();
                showToast("delete fail, check network or server fail");
            }
        });
    }
}
