package com.aiyouwei.drk.shelf.admin.password;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.utils.Constants;
import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.UIUtils;
import com.aiyouwei.drk.shelf.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private String mCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        ((EditText) findViewById(R.id.shelf)).setText(Config.getShelf(this));
        ((EditText) findViewById(R.id.room)).setText(Config.getRoom(this));
        ((EditText) findViewById(R.id.socketDomainName)).setText(Config.getSocketDomain(this));
        ((EditText) findViewById(R.id.socketPort)).setText(Config.getSocketPort(this));
        ((EditText) findViewById(R.id.serviceDomainName)).setText(Config.getServiceDomain(this));
        ((EditText) findViewById(R.id.servicePort)).setText(Config.getServicePort(this));
        mCode = Config.getActiveKey(this);
        ((EditText) findViewById(R.id.activation_code)).setText(mCode);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        } else {
            save();
        }
    }


    private void save() {
        String shelf = ((EditText) findViewById(R.id.shelf)).getText().toString();

        if (isEmpty(shelf)) {
            showToast("Shelf can not be empty");
            return;
        }

        String room = ((EditText) findViewById(R.id.room)).getText().toString();
        if (isEmpty(room)) {
            showToast("Room can not be empty");
            return;
        }



        String socketDomainName = ((EditText) findViewById(R.id.socketDomainName)).getText().toString();
        if (isEmpty(socketDomainName)) {
            showToast("Socket Domain Name can not be empty");
            return;
        }else if(!Utils.isURL(socketDomainName)){
            showToast("Incorrect URL");
            return;
        }


        String socketPort = ((EditText) findViewById(R.id.socketPort)).getText().toString();
        if (isEmpty(socketPort)) {
            showToast("Socket Port can not be empty");
            return;
        }



        String serviceDomainName = ((EditText) findViewById(R.id.serviceDomainName)).getText().toString();
        if (isEmpty(serviceDomainName)) {
            showToast("Service Domain Name can not be empty");
            return;
        }else if(!Utils.isURL(serviceDomainName)){
            showToast("Incorrect URL");
            return;
        }
        String code = ((EditText) findViewById(R.id.activation_code)).getText().toString();
        if (isEmpty(code)) {
            showToast("Face Activation Code can not be empty");
            return;
        }
        String servicePort = ((EditText) findViewById(R.id.servicePort)).getText().toString();
        if (!isEmpty(servicePort)) {
            Config.saveServicePort(SettingsActivity.this,servicePort);
        }


        Config.saveShelf(SettingsActivity.this,shelf);
        Config.saveActiveKey(SettingsActivity.this,code);
        Config.saveRoom(SettingsActivity.this,room);
        Config.saveSocketDomain(SettingsActivity.this,socketDomainName);
        Config.saveServiceDomain(SettingsActivity.this,serviceDomainName);
        Config.saveSocketPort(SettingsActivity.this,socketPort);
        if (code.equals(mCode)) {
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIUtils.changeAppLanguage(SettingsActivity.this);
                }
            }, 1000L);
        }


    }


}
