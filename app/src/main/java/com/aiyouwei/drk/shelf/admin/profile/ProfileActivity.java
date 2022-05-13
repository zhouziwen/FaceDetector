package com.aiyouwei.drk.shelf.admin.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;
import androidx.core.content.FileProvider;

import com.aiyouwei.drk.shelf.AiYouWei;
import com.aiyouwei.drk.shelf.BaseActivity;
import com.aiyouwei.drk.shelf.BuildConfig;
import com.aiyouwei.drk.shelf.R;
import com.aiyouwei.drk.shelf.admin.FaceCollections;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.admin.db.EmployeeList;
import com.aiyouwei.drk.shelf.utils.*;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectModel;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.zhangke.zlog.ZLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class ProfileActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, Listener<String> {
    public static final int RESULT_CODE_ADD = 543;

    private static final String TAG = "ProfileActivity";

    private static int REQUEST_CODE_CAMERA = 2;

    protected Uri mImageUri;

    private int mImageWidth;

    private String rectInfo;

    private FaceEngine faceEngine;

    private int faceEngineCode = -1;

    private PhotoView mPhotoView;

    private RadioGroup mPickGroup, mOpenGroup;

    private boolean mPickAllow = true, mOpenAllow = true;

    private byte[] imageInfo, faceInfo;

    private EditText mIdEdit, mNameEdit;

    private int mId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
        lp.width = -1;
        lp.height = Math.round(51.f / 300 * height);
        int p = Math.round(45.f / 1024 * width);
        titleLayout.setPadding(p, 0, p, 0);

        ((TextView) findViewById(R.id.title)).setTextSize(TypedValue.COMPLEX_UNIT_PX,
                1.f / 30 * height);

        ImageView backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) backBtn.getLayoutParams();
        lp1.width = Math.round(4.f / 75 * height);
        lp1.height = lp1.width;

        Button btn = (Button) findViewById(R.id.btn_save);
        btn.setOnClickListener(this);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        lp2.width = Math.round(15.f / 128 * width);
        lp2.height = Math.round(7.f / 100 * height);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2.f / 75 * height);

        int margin = Math.round(3.f / 40 * height);
        LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) findViewById(R.id.pane).getLayoutParams();
        lp3.width = -1;
        lp3.height = -1;
        lp3.setMargins(margin, 0, margin, margin);

        mPhotoView = (PhotoView) findViewById(R.id.avatar_frame);
        mPhotoView.setOnClickListener(this);
        RelativeLayout.LayoutParams lp4 = (RelativeLayout.LayoutParams) mPhotoView.getLayoutParams();
        lp4.width = Math.round(101.f / 300 * height);
        lp4.height = lp4.width;
        lp4.topMargin = Math.round(41.f / 300 * height);
        lp4.rightMargin = Math.round(73.f / 1024 * width);

        Button setBtn = (Button) findViewById(R.id.btn_set);
        setBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.f / 100 * height);
        setBtn.setOnClickListener(this);
        RelativeLayout.LayoutParams lp5 = (RelativeLayout.LayoutParams) setBtn.getLayoutParams();
        lp5.width = lp4.width;
        lp5.height = Math.round(11.f / 150 * height);
        lp5.topMargin = Math.round(3.f / 120 * height);
        lp5.rightMargin = lp4.rightMargin;

        int leftP = Math.round(55.f / 1024 * width);
        int rightP = Math.round(95.f / 1024 * width);
        LinearLayout leftPane = (LinearLayout) findViewById(R.id.left_pane);
        RelativeLayout.LayoutParams lp6 = (RelativeLayout.LayoutParams) leftPane.getLayoutParams();
        lp6.width = -1;
        lp6.height = -1;
        leftPane.setPadding(leftP, 0, rightP, 0);

        mIdEdit = (EditText) findViewById(R.id.edit_text);
        mNameEdit = (EditText) findViewById(R.id.edit_username);

        mPickGroup = (RadioGroup) findViewById(R.id.group_pick);
        mPickGroup.check(R.id.rb_allow);
        mPickGroup.setOnCheckedChangeListener(this);

        mOpenGroup = (RadioGroup) findViewById(R.id.open_group);
        mOpenGroup.check(R.id.rb_open_allow);
        mOpenGroup.setOnCheckedChangeListener(this);

        mImageWidth = Math.round(101.f / 512 * width);

        initEngine();
        Bundle args = getIntent().getExtras();
        if (null != args && args.containsKey("id")) {
            mId = args.getInt("id");
            showProgress();
            request();
        }
    }

    private void request() {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(mId));

        NetworkRequest.getInstance().get(AiYouWei.getInstance().getServiceUrl(),Constants.USER_LIST, params, EmployeeList.class, new Listener<EmployeeList>() {
            @Override
            public void onResponse(EmployeeList response) {
                hideProgress();
                if (null == response || response.size() == 0) return;
                setupInfo(response.get(0));
            }

            @Override
            public void onErrorResponse(String e) {
                hideProgress();
            }
        });
    }

    @Override
    public void onResponse(String response) {
        hideProgress();
        try {
            JSONObject jObj = new JSONObject(response);
            boolean result = jObj.optBoolean("result", false);
            Utils.showToast(this, jObj.optString("info"));
            if (result) {
                if (null != faceInfo && mId == -1) {

                    String userId = mIdEdit.getText().toString();

                    FaceCollections.getInstance().addFace(userId, new FaceFeature(faceInfo));

                    ZLog.i("Add User Response","addFace");
                }
                if (mParams.size() > 0) {
                    Employee e = new Employee();
                    e.userid =String.valueOf(mParams.get("userid")) ;
                    e.username =String.valueOf( mParams.get("username"));
                    e.door = Integer.parseInt(String.valueOf(mParams.get("door")));
                    e.facedata = String.valueOf(mParams.get("facedata"));
                    e.photo = String.valueOf(mParams.get("photo"));
                    e.rect =String.valueOf( mParams.get("rect"));
                    ZLog.i("Employee Insert","insert = "+DatabaseHelper.getInstance(this).insert(e));
                }
                faceInfo = null;
                imageInfo = null;
                setResult(RESULT_CODE_ADD);
                finish();
            }
        } catch (JSONException e) {
            ZLog.e("JSONException",e.getMessage());
            Utils.showToast(this, R.string.json_invalid);
        }

        ZLog.i("Add User","-------------------- end -----------------------");
    }

    @Override
    public void onErrorResponse(String e) {
        hideProgress();
    }

    private void setupInfo(Employee e) {
        mIdEdit.setText(e.userid);
        mIdEdit.setFocusable(false);

        mNameEdit.setText(e.username);

        mPickGroup.check(e.pickup == 1 ? R.id.rb_allow : R.id.rb_disallow);
        mOpenGroup.check(e.door == 1 ? R.id.rb_open_allow : R.id.rb_open_disallow);

        if (!isEmpty(e.photo)) {
            byte[] decoded = Base64.decode(e.photo, Base64.DEFAULT);
            if (null != decoded && decoded.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                mPhotoView.fillBitmap(bitmap);
            }
        }
        if (!isEmpty(e.rect)) {
            String[] arr = e.rect.split(",");
            int len = arr.length;
            int[] dimens = new int[arr.length];
            for (int i = 0; i < len; i++) {
                dimens[i] = Integer.parseInt(arr[i]);
            }
            mPhotoView.setOriginSize(dimens[0], dimens[1]);
            Rect rect = new Rect(dimens[2], dimens[3], dimens[4], dimens[5]);
            mPhotoView.setFaceRect(rect);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getId() == R.id.group_pick) {
            mPickAllow = i == R.id.rb_allow;
        } else {
            mOpenAllow = i == R.id.rb_open_allow;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                if (-1 != mId) {
                    update();
                } else {
                    insert();
                }
                break;
            case R.id.avatar_frame:
            case R.id.btn_set:
                startCamera();
                break;
        }
    }

    private void update() {
        String userName = mNameEdit.getText().toString();
        if (isEmpty(userName)) {
            showToast(R.string.name_hint);
            return;
        }

        showProgress();
        Map<String, Object> params = new HashMap<>();
        params.put("action", "edit");
        params.put("id", String.valueOf(mId));
        params.put("username", userName);
        params.put("userid", mIdEdit.getText().toString());
        params.put("door", String.valueOf(mOpenAllow ? 1 : 0));
        params.put("pickup", String.valueOf(mPickAllow ? 1 : 0));

        if (null != faceInfo && faceInfo.length > 0) {
            byte[] encoded = Base64.encode(faceInfo, Base64.DEFAULT);
            params.put("facedata", new String(encoded));
        }

        if (null != imageInfo && imageInfo.length > 0) {
            byte[] data = Base64.encode(imageInfo, Base64.DEFAULT);
            params.put("photo", new String(data));
        }

        if (!isEmpty(rectInfo)) {
            params.put("rect", rectInfo);
        }
        NetworkRequest.getInstance().post(AiYouWei.getInstance().getServiceUrl(),Constants.EDIT_USER, params, this);
    }

    private final HashMap<String, Object> mParams = new HashMap<>();

    private void insert() {
        ZLog.i("Add User","-------------------- start -----------------------");
        mParams.clear();
        String userId = mIdEdit.getText().toString();
        if (isEmpty(userId)) {
            showToast(R.string.id_hint);
            return;
        }

        String userName = mNameEdit.getText().toString();
        if (isEmpty(userName)) {
            showToast(R.string.name_hint);
            return;
        }

        if (null == faceInfo || faceInfo.length == 0) {
            showToast(R.string.photo_empty);
            return;
        }

        showProgress();

        mParams.put("action", "add");
        mParams.put("userid", userId);
        mParams.put("username", userName);
        mParams.put("door", String.valueOf(mOpenAllow ? 1 : 0));
        mParams.put("pickup", String.valueOf(mPickAllow ? 1 : 0));

        byte[] encoded = Base64.encode(faceInfo, Base64.DEFAULT);
        mParams.put("facedata", new String(encoded));

        byte[] data = Base64.encode(imageInfo, Base64.DEFAULT);
        mParams.put("photo", new String(data));
        if (!isEmpty(rectInfo)) {
            mParams.put("rect", rectInfo);
        }

        NetworkRequest.getInstance().post(AiYouWei.getInstance().getServiceUrl(),Constants.EDIT_USER, mParams, this);

    }

    private void initEngine() {
        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        if (faceEngineCode != ErrorInfo.MOK) {
            showToast(getString(R.string.init_failed, faceEngineCode));
        }
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            faceEngine = null;
        }
    }

    @Override
    protected void onDestroy() {
        unInitEngine();
        super.onDestroy();
    }

    private void startCamera() {
        File file = new File(DirectoryUtils.getTempCacheDir(), "Pic.jpg");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",
                    file);
        } else {
            mImageUri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private String getFilePath() {
        File file = new File(DirectoryUtils.getTempCacheDir(), "Pic.jpg");
        return file.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode || requestCode != REQUEST_CODE_CAMERA) return;
        mPhotoView.clearRect();

        showProgress();
        new Thread() {
            @Override
            public void run() {
                String filePath = getFilePath();
                final Bitmap scaledBitmap = Utils.getScaledBitmap(filePath, mImageWidth);
                processImage(filePath);
                if (null != scaledBitmap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoView.fillBitmap(scaledBitmap);
                        }
                    });
                }
                dismiss();
            }
        }.start();
    }

    private void dismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        });
    }

    private void showThreadToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(resId);
            }
        });
    }

    private void processImage(String filePath) {
        final Bitmap originBitmap = BitmapFactory.decodeFile(filePath);
        if (null == originBitmap) return;

        Bitmap bitmap = ArcSoftImageUtil.getAlignedBitmap(originBitmap, true);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] bgr24 = ArcSoftImageUtil.createImageData(width, height, ArcSoftImageFormat.BGR24);
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            Log.i(TAG, "transform failed, code is " + transformCode);
            return;
        }

        List<FaceInfo> faceInfoList = new ArrayList<>();
        int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, DetectModel.RGB, faceInfoList);

        if (faceInfoList.size() == 0) {
            showThreadToast(R.string.recognition_fail);
            Log.i(TAG, "no face detected.");
            return;
        }

        int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        if (faceProcessCode != ErrorInfo.MOK) {
            showThreadToast(R.string.recognition_fail);
            Log.i(TAG, "process failed! code is " + faceProcessCode);
            return;
        }

        FaceFeature faceFeature = new FaceFeature();
        int extractCode = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24,
                faceInfoList.get(0), faceFeature);
        if (extractCode != ErrorInfo.MOK) {
            showThreadToast(R.string.recognition_fail);
            Log.i(TAG, "no face feature extracted, please try again.");
            return;
        }

        faceInfo = faceFeature.getFeatureData();
        Bitmap scaledBitmap = Utils.getScaledBitmap(getFilePath(), mImageWidth);
        imageInfo = Utils.bitmap2bytes(scaledBitmap);

        final Rect rect = faceInfoList.get(0).getRect();
        rectInfo = originBitmap.getWidth() + "," + originBitmap.getHeight()
                + "," + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoView.setOriginSize(originBitmap.getWidth(), originBitmap.getHeight());
                mPhotoView.setFaceRect(rect);
                showToast(R.string.recognition_success);
            }
        });
    }
}
