package com.aiyouwei.drk.shelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.aiyouwei.drk.shelf.model.DeviceInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class Config {

    private static final String CONFIG_FILE_NAME = "AiYouWei";

    private static final String PASSWORD = "password";

    private static final String SHELF = "shelf";

    private static final String ROOM = "room";

    private static final String SOCKET = "socketDomainName";

    private static final String SOCKET_PORT = "socketPort";

    private static final String SERVICE = "serviceDomainName";

    private static final String SERVICE_PORT = "servicePort";

    private static final String TOKEN = "token";

    private static final String ACCESS_TOKEN = "accessToken";


    private static final String ACTIVE_KEY = "active_key";



    public static boolean isLogin(Context ctx) {
        return !isEmpty(getToken(ctx));
    }

    public static String getToken(Context ctx) {
        return getString(ctx, TOKEN, "");
    }

    public static void saveToken(Context ctx, String s) {
        putString(ctx, TOKEN, s);
    }
    public static String getAccessToken(Context ctx) {
        return getString(ctx, ACCESS_TOKEN, "");
    }
    public static String getActiveKey(Context ctx) {
        return getString(ctx, ACTIVE_KEY, "");
    }
    public static void saveAccessToken(Context ctx, String s) {
        putString(ctx, ACCESS_TOKEN, s);
    }
    public static void saveActiveKey(Context ctx, String s) {
        putString(ctx, ACTIVE_KEY, s);
    }
    public static void savePassword(Context ctx, String s) {
        putString(ctx, PASSWORD, s);
    }

    public static void saveShelf(Context ctx, String s) {
        putString(ctx, SHELF, s);
    }
    public static void saveRoom(Context ctx, String s) {
        putString(ctx, ROOM, s);
    }

    public static void saveSocketDomain(Context ctx, String s) {
        putString(ctx, SOCKET, s);
    }
    public static void saveSocketPort(Context ctx, String s) {
        putString(ctx, SOCKET_PORT, s);
    }
    public static void saveServiceDomain(Context ctx, String s) {
        putString(ctx, SERVICE, s);
    }

    public static void saveServicePort(Context ctx, String s) {
        putString(ctx, SERVICE_PORT, s);
    }

    public static String getPassword(Context ctx) {
        return getString(ctx, PASSWORD, "123456");
    }
    public static String getShelf(Context ctx) {
        return getString(ctx, SHELF, "");
    }
    public static String getRoom(Context ctx) {
        return getString(ctx, ROOM, "");
    }
    public static String getSocketDomain(Context ctx) {
        return getString(ctx, SOCKET, "");
    }
    public static String getSocketPort(Context ctx) {
        return getString(ctx, SOCKET_PORT, "");
    }
    public static String getServiceDomain(Context ctx) {
        return getString(ctx, SERVICE, "");
    }
    public static String getServicePort(Context ctx) {
        return getString(ctx, SERVICE_PORT, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getPreferences(context).getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.apply();
    }
//
//    public static  void putObject(String key,  Object data, Class<?>  dataClass) {
//        String  json = null;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        JsonWriter writer;
//        try {
//            writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
////            writer.setIndent("  ");
////            writer.beginArray();
//            if(dataClass ==null){
//                dataClass = String.class;
//            }
//             Gson gson = new Gson();
//            gson.toJson(data, dataClass, writer);
////            writer.endArray();
//            writer.close();
//            json= out.toString("UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(!TextUtils.isEmpty(json)){
//            putString(AiYouWei.getInstance().getApplicationContext(),key,json);
//        }
//    }
//
//    public  static List<DeviceInfo> getListDeviceInfo(String key) {
//
//        String json = getString( AiYouWei.getInstance().getApplicationContext(),key, "" );
//
//        if(TextUtils.isEmpty(json)){
//            return null;
//        }
//        Type type = new TypeToken<List<DeviceInfo>>() {}.getType();
//        try {
//             Gson gson = new Gson();
//            return gson.fromJson( json, type );
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static void  cleanObject(String key) {
//        SharedPreferences.Editor editor = getPreferences(AiYouWei.getInstance().getApplicationContext()).edit();
//        editor.putString(key,"");
//        editor.remove(key);
//        editor.apply();
//    }


    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
    }
}
