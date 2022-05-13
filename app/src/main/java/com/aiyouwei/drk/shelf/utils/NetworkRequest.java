package com.aiyouwei.drk.shelf.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zhangke.zlog.ZLog;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

/**
 * Created By jishichen on 2019-07-27
 */
public class NetworkRequest {

    private static final int METHOD_POST = 1;

    private static final int METHOD_PUT = 2;

    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final NetworkRequest INSTANCE = new NetworkRequest();

    private OkHttpClient httpClient;

    private Gson gson;

    private Handler handler;

    private NetworkRequest() {
        httpClient = new OkHttpClient();
        gson = new GsonBuilder().create();
        handler = new Handler(Looper.getMainLooper());
    }

    public static NetworkRequest getInstance() {
        return INSTANCE;
    }

    public void get(String url,String method, Listener<String> listener) {
        get(url,method, Collections.EMPTY_MAP, listener);
    }

    public <T> void get(   String url,String method,
                        final Class<T> clazz,
                        final Listener<T> listener) {
        get(url,method, null, clazz, listener);
    }

    public <T> void get(
                        String url,
                        String method,
                        Map<String, String> params,
                        final Class<T> clazz,
                        final Listener<T> listener) {


        if(TextUtils.isEmpty(url)){
            return;
        }else if(!isHttpUrl(url)){
            Utils.showToast(AiYouWei.getInstance(), R.string.incorrect_url);
            return;
        }

        Request request = buildGetRequest(url,method, params);

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handleError(listener, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException, JsonSyntaxException {
                String s = response.body().string();
                if (isEmpty(s)) {
                    handleError(listener, "no data");
                    return;
                }

                final T obj;
                try {
                    obj = gson.fromJson(s, clazz);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponse(obj);
                        }
                    });
                } catch (JsonSyntaxException e) {
                    handleError(listener, e.toString());
                }
            }
        });
    }
    public <T> void get(
                        String url,
                        String method,
                        Map<String, String> params,
                        final Listener<String> listener) {

        if(TextUtils.isEmpty(url)){
            return;
        }else if(!isHttpUrl(url)){
            Utils.showToast(AiYouWei.getInstance(), R.string.incorrect_url);
            return;
        }

        Request request = buildGetRequest(url,method, params);

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handleError(listener, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException, JsonSyntaxException {
                final String s = response.body().string();
                if (isEmpty(s)) {
                    handleError(listener, "no data");
                    return;
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResponse(s);
                    }
                });
            }
        });
    }


    public void postJson(String url,String method, String json, final Listener<String> listener) {
        if (isEmpty(json)) return;

        ZLog.i("Request Data",json);

        if(TextUtils.isEmpty(url)){

            return;

        }else if(!isHttpUrl(url)){

            Utils.showToast(AiYouWei.getInstance(), R.string.incorrect_url);

            return;
        }

        Request request = getRequest(url,method, json, METHOD_POST);

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                handleError(listener, e.toString());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String s = response.body().string();

                ZLog.i("Response Data",s);


                if (isEmpty(s)) {

                    handleError(listener, "no data");

                    return;

                }

                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        listener.onResponse(s);

                    }
                });
            }
        });
    }

    public void postJson(String url,String method,String params, final Class clazz, final Listener listener) {


        if(TextUtils.isEmpty(url)){

            return;

        }else if(!isHttpUrl(url)){

            Utils.showToast(AiYouWei.getInstance(), R.string.incorrect_url);

            return;
        }

        Request request = getRequest(url,method, params,METHOD_POST);

        ZLog.e("request", request.body().toString());

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleError(listener, e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String s = response.body().string();

                ZLog.i("Response Data",s);


                if (isEmpty(s)) {
                    handleError(listener, "no data");
                    return;
                }

                final Object obj;
                try {
                    obj = gson.fromJson(s, clazz);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponse(s);
                        }
                    });

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponse(obj);
                        }
                    });
                } catch (JsonSyntaxException e) {
                    handleError(listener, e.toString());
                }
            }
        });
    }

    public void post(String url,String method, Map<String, String> params,  final Class clazz, final Listener listener) {
        if (null == params) params = Collections.emptyMap();
        String json = gson.toJson(params);
        postJson(url,method,json, clazz, listener);
    }

//    public void post(String url,String method, Map<String, String> params, Listener<String> listener) {
//        if (null == params) params = Collections.emptyMap();
//        String json = gson.toJson(params);
//        Log.e("postJSon",json);
//        postJson(url,method, json, listener);
//    }
    public void post(String url,String method, Map<String, Object> params, Listener<String> listener) {
        if (null == params) params = Collections.emptyMap();
        String json = gson.toJson(params);
        postJson(url,method, json, listener);
    }



    private String getUrl(String url,String method, Map<String, String> params) {
        url += method;
        if (null != params && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append('?');
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
            String s = sb.toString();
            url += s.substring(0, s.length() - 1);
        }

        return url;
    }

    private Request buildGetRequest(String url,String method, Map<String, String> params) {
         url = getUrl(url,method, params);
        String token = Config.getToken(AiYouWei.getInstance());

        if (!isEmpty(token)){

            if(url.indexOf("?")>0){

                url += "&token=" + token;

            }else {

                url += "?token=" + token;
            }
        }
//        String  accessToken = Config.getAccessToken(AiYouWei.getInstance());

        Request.Builder builder = new Request.Builder();

        builder.url(url);

//        if(!isEmpty(accessToken)){
//            builder.addHeader("Authorization", "Bearer "+accessToken);
//        }
        return builder.build();
    }

    private Request getRequest(String url, String method, String json, int type) {

         url += method;

        String token = Config.getToken(AiYouWei.getInstance());

        if (!isEmpty(token)){

            if(url.indexOf("?")>0){

                url += "&token=" + token;

            }else {

                url += "?token=" + token;
            }
        }


        RequestBody body = RequestBody.create(JSON, json);

        Request.Builder builder = new Request.Builder();

        builder.url(url);

        ZLog.i("Service Url",url);


        if (type == METHOD_POST) {

            builder.post(body);

        } else if (type == METHOD_PUT) {

            builder.put(body);

        } else {

            builder.delete(body);

        }

        builder.addHeader("Content-Type", "application/json");

        builder.addHeader("Accept", "application/json");

//        String  accessToken = Config.getAccessToken(AiYouWei.getInstance());
//        if(!isEmpty(accessToken)){
//            builder.addHeader("Authorization", "Bearer "+accessToken);
//        }

        return builder.build();
    }

    private void handleError(final Listener listener, final String e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkConnected(AiYouWei.getInstance())) {
                    Utils.showToast(AiYouWei.getInstance(), R.string.service_not_available);
                } else {
                    Utils.showToast(AiYouWei.getInstance(), R.string.network_not_available);
                }

                listener.onErrorResponse(e);
            }
        });
    }


//    /**
//     * 判断字符串是否为URL
//     * @param urls 需要判断的String类型url
//     * @return true:是URL；false:不是URL
//     */
//    public  boolean isHttpUrl(@NotNull String urls) {
//        boolean isurl = false;
//        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
//                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
//
//        Pattern pat = Pattern.compile(regex.trim());//对比
//        Matcher mat = pat.matcher(urls.trim());
//        isurl = mat.matches();//判断是否匹配
//        return isurl;
//    }
    /**
     * 判断字符串是否为URL
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public  boolean isHttpUrl(@NotNull String urls) {

        int index = urls.indexOf("http");

        return index >= 0;
    }

}
