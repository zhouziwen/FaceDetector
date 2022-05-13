package com.aiyouwei.drk.shelf.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.Config;
import com.aiyouwei.drk.shelf.R;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class Utils {


    public static boolean isURL(String str){
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return  str.matches(regex);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info) {
                return info.isConnected();
            }
        }

        return false;
    }

    /**
     *转换内存类型
     */
    @Contract(pure = true)
    public static float convertSize(float size) {

        int GB = 1024 * 1024 * 1024;//定义GB的计算常量

        int MB = 1024 * 1024;//定义MB的计算常量

        int KB = 1024;//定义KB的计算常量

        if (size / GB >= 1) {

            //如果当前Byte的值大于等于1GB

            return (size / (float) GB);

        } else if (size / MB >= 1) {

            //如果当前Byte的值大于等于1MB

            return (size / (float) MB);

        } else if (size / KB >= 1) {

            //如果当前Byte的值大于等于1KB

            return (size / (float) KB);

        } else {
            return size;
        }
    }

    public static Bitmap getScaledBitmap(String filePath, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        int w = options.outWidth;
        int h = options.outHeight;
        int finalW = 0;
        int finalH = 0;
        if (w >= h) {
            finalH = width;
            finalW = (int) (finalH * w * 1.f / h);
        } else {
            finalW = width;
            finalH = (int) (width * h * 1.0f / w);
        }

        options.inSampleSize = calculateInSampleSize(options, finalW, finalH);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static byte[] bitmap2bytes(Bitmap bitmap) {
        if (null == bitmap) return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        return byteArray;
    }
    public static  Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;//inJustDecodeBounds 需要设置为false，如果设置为true，那么将返回null
            opts.inSampleSize = 500 ;
            return BitmapFactory.decodeByteArray(b, 0, b.length,opts);
        } else {
            return null;
        }
    }


    public static int setPreview(String filePath, ImageView imageView, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        int w = options.outWidth;
        int h = options.outHeight;
        int height = (int) (width * h * 1.0f / w);

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setImageBitmap(bitmap);

        return height;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public  static  String  getStrTime(){
        return  date2String(new Date());
    }
    public  static  String  date2String(Date date){
        //时间
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  dateFormat.format(date);
    }

}
