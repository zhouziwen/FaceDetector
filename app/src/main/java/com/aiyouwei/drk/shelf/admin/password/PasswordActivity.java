package com.aiyouwei.drk.shelf.admin.password;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.FaceDetectActivity;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class PasswordActivity extends BaseActivity implements View.OnClickListener, Listener<String> {

    public static final int RESULT_CODE_SUCCESS = 342;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        ((TextView) findViewById(R.id.password)).setText(Config.getPassword(this));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        } else {
            changePassword();
        }
    }

    @Override
    public void onResponse(String response) {
        hideProgress();
        try {
            JSONObject jObj = new JSONObject(response);
            boolean result = jObj.optBoolean("result", false);
            String info = jObj.optString("info", "fail");
            Utils.showToast(this, info);
            if (result) {
                //clear token
                Config.saveToken(this, "");
                setResult(RESULT_CODE_SUCCESS);
                finish();
            }
        } catch (JSONException e) {
            Utils.showToast(this, R.string.json_invalid);
        }
    }

    @Override
    public void onErrorResponse(String e) {
        hideProgress();
    }

    private void changePassword() {
        String pwd = ((EditText) findViewById(R.id.new_pwd_edit)).getText().toString();
        if (isEmpty(pwd)) {
            showToast("password can not be empty");
            return;
        }

        String confirm = ((EditText) findViewById(R.id.confirm_edit)).getText().toString();
        if (isEmpty(confirm)) {
            showToast("password confirm can not be empty");
            return;
        }

        if (!pwd.equals(confirm)) {
            showToast("confirm password must be same with new password");
            return;
        }

        showProgress();
        Map<String, String> params = new HashMap<>();
        params.put("adminpass", Config.getPassword(this));
        params.put("newpass", pwd);
        params.put("newpass2", confirm);

        NetworkRequest.getInstance().get(AiYouWei.getInstance().getServiceUrl(),Constants.MODIFY_PWD, params, this);
    }
}
