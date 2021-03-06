package com.aiyouwei.drk.shelf.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by chenjishi on 14/10/27.
 */
public class DirectoryUtils {

    public static void init(Context context) {
        mkDirs(getImageCacheDir(context));
        mkDirs(getDataCacheDir(context));
        mkDirs(getTempCacheDir());
    }

    public static void mkDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) file.mkdirs();
    }

    public static String getImageCacheDir(Context context) {
        return getRootDirectory(context) + "/image/";
    }

    public static String getDataCacheDir(Context context) {
        return getRootDirectory(context) + "/data/";
    }

    public static String getTempCacheDir() {
        return getSDCardDirectory() + "/AiYouWei/";
    }

    public static String getSDCardDirectory() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getPath();
        }
        return null;
    }

    public static String getRootDirectory(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final String cacheDir = "/Android/data/" + context.getPackageName();
            return Environment.getExternalStorageDirectory() + cacheDir;
        } else {
            String path = null;
            File cacheDir = context.getCacheDir();
            if (cacheDir.exists()) path = cacheDir.getAbsolutePath();
            return path;
        }
    }
}
