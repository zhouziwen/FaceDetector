package com.aiyouwei.drk.shelf;

import com.aiyouwei.drk.shelf.utils.Listener;
import com.aiyouwei.drk.shelf.utils.NetworkRequest;
import com.aiyouwei.drk.shelf.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class Access {

    public   String   ACCESS_URL = "http://drk-wired-face-recognition.herokuapp.com";

    public  String   ACCESS_TOKEN = "/api/oauth/token";

    public   String   GET_INVENTORY_RECORD = "/api/face-recognition/getInventoryRecord";

    public  String   SAVE_RECORD = "/api/face-recognition/saveRecord";

    public String  USER_NAME =  "face-recogintion";

    public String  PASSWORD =  "zpQ3u0KYX7";

    private  static   Access access;


    public static  Access  getInstance(){
        if(access == null){
            access = new Access();
        }
        return  access;
    }



    private void  AccessToken(boolean refresh){

        String  accessToken = Config.getAccessToken(AiYouWei.getInstance().getApplicationContext());

        if(isEmpty(accessToken) || refresh){
            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", "password");
            params.put("scope", "*");
            params.put("client_id","2");
            params.put("client_secret", "MNwTMvh8TPZwghwNmnSv88ddGwT4WNx40dweQuYi");
            params.put("username", this.USER_NAME);
            params.put("password", this.PASSWORD);
            NetworkRequest.getInstance().post(this.ACCESS_URL, this.ACCESS_TOKEN, params, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String token = jObj.optString("access_token", "");
                        if (!isEmpty(token)) {
                            Config.saveAccessToken(AiYouWei.getInstance().getApplicationContext(), token);
                        } else {
                            Utils.showToast(AiYouWei.getInstance().getApplicationContext(), "Failed to obtain an access token");
                        }
                    } catch (JSONException e) {
                        Utils.showToast(AiYouWei.getInstance().getApplicationContext(), R.string.json_invalid);
                    }
                }

                @Override
                public void onErrorResponse(String e) {
                    Utils.showToast(AiYouWei.getInstance().getApplicationContext(), "network invalid or server error, error: " + e);
                }
            });
        }

    }

    public  void  initAccessToken(){
        AccessToken(false);
    }

    public  void  refreshAccessToken(){
        AccessToken(true);
    }
}
