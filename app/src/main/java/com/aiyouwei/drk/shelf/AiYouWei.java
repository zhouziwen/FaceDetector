package com.aiyouwei.drk.shelf;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.utils.DirectoryUtils;
import com.zhangke.zlog.ZLog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AiYouWei extends Application {

    public static AiYouWei INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        DirectoryUtils.init(this);
        DatabaseHelper.getInstance(this);
        Access.getInstance().initAccessToken();
        //日志系统
        ZLog.Init( String.format( "%s/ZLog/", INSTANCE.getExternalFilesDir( null ).getPath() ) );

        ZLog.openSaveToFile();
    }

    public static AiYouWei getInstance() {
        return INSTANCE;
    }



    public String  getServiceUrl(){

        String serviceUrl =  Config.getServiceDomain(this);


        String servicePort =  Config.getServicePort(this);


        if(!TextUtils.isEmpty(servicePort)){

            serviceUrl += ":"+servicePort;

        }

        return  serviceUrl;

    }

}
