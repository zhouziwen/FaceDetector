package com.aiyouwei.drk.shelf.admin;

import com.arcsoft.face.FaceFeature;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class FaceCollections {

    private final Map<String, FaceFeature> faceMap = new HashMap<>();

    private static final FaceCollections INSTANCE = new FaceCollections();

    public static FaceCollections getInstance() {
        return INSTANCE;
    }

    private FaceCollections() {
    }


    public void remove(String userId) {
        FaceFeature feature = faceMap.get(userId);
        if (null != feature) {
            faceMap.remove(feature);
        }
    }

    public void addFace(String userId, FaceFeature feature) {
        if (isEmpty(userId) || null == feature) return;

        faceMap.put(userId, feature);
    }

    public Map<String, FaceFeature> getFaceData() {
        return faceMap;
    }
}
