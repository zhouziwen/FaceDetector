package com.aiyouwei.drk.shelf;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import com.aiyouwei.drk.shelf.admin.FaceCollections;
import com.aiyouwei.drk.shelf.admin.db.DatabaseHelper;
import com.aiyouwei.drk.shelf.admin.db.Employee;
import com.aiyouwei.drk.shelf.utils.SocketUtils;
import com.aiyouwei.drk.shelf.widget.CameraPreview;
import com.aiyouwei.drk.shelf.widget.FaceRectView;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.zhangke.zlog.ZLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static android.text.TextUtils.isEmpty;
import static com.arcsoft.face.FaceEngine.*;
import static com.arcsoft.face.FaceEngine.ASF_IR_LIVENESS;

public abstract class FaceDetectActivity extends BaseActivity implements CameraPreview.CameraListener, View.OnLongClickListener {
    protected static final int MAX_THREAD_COUNT = 5;
    protected int previewWidth, previewHeight, viewWidth, viewHeight;
    protected FaceEngine faceEngine, irEngine;
    protected int afCode = -1, irCode = -1;
    protected FaceRectView faceRectView;
    private JSONObject jsonObject;
    protected CameraPreview rgbView, irView;

    private final List<FaceInfo> faceInfoList = new ArrayList<>();
    private final List<LivenessInfo> livenessList = new ArrayList<>();
    private final List<Rect> rectList = new ArrayList<>();

    private volatile boolean inProgress = false;

    protected ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    protected volatile byte[] irData;

    private final String personId = "person-id";
    private final String name = "name";
    private final String shelf = "shelf";
    private final String snapshot = "snapshot";

    private  volatile boolean isRecognitioned = false;


    protected final CameraPreview.CameraListener irCameraListener = new CameraPreview.CameraListener() {
        @Override
        public void onCameraOpened(Camera camera, int cameraId, int w, int h) {

        }

        @Override
        public void onPreview(byte[] data, Camera camera) {
            irData = data;
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraError(String msg) {

        }

        @Override
        public void onCameraConfigurationChanged(int cameraId, int orientation) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        jsonObject =new JSONObject();

        irView = (CameraPreview) findViewById(R.id.ir_preview);

        rgbView = (CameraPreview) findViewById(R.id.rgb_preview);

        rgbView.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK, this);

        faceRectView = findViewById(R.id.face_rect_view);

        setupView();

        initEngine();

        startService(new Intent(this, SyncService.class));

        irView.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT, irCameraListener);

    }

    protected abstract int getLayoutId();

    protected abstract void setupView();

    protected abstract void detectSuccess(String userId);


    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, 1, ASF_FACE_DETECT | ASF_FACE_RECOGNITION | ASF_LIVENESS);

        if (afCode != ErrorInfo.MOK) {
            showToast(getString(R.string.init_failed, afCode));
        }

        irEngine = new FaceEngine();
        irCode = irEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, 1, ASF_FACE_DETECT | ASF_FACE_RECOGNITION | ASF_IR_LIVENESS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rgbView.stop();
        irView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rgbView.start();
        irView.start();
    }

    private void unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine.unInit();
        }
        if (irCode == 0) {
            irCode = irEngine.unInit();
        }
    }

    @Override
    protected void onDestroy() {
        rgbView.release();
        irView.release();
        unInitEngine();
        super.onDestroy();
    }




    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public void onCameraOpened(Camera camera, int cameraId, int w, int h) {
        previewWidth = w;
        previewHeight = h;
    }

    @Override
    public void onPreview(byte[] data, Camera camera) {

        if(!isRecognitioned){

            faceRectView.clearRectInfo();

            faceInfoList.clear();

            int code = faceEngine.detectFaces(data, previewWidth, previewHeight, FaceEngine.CP_PAF_NV21, faceInfoList);

            if (code != ErrorInfo.MOK || faceInfoList.size() == 0) {
                return;
            }

            rectList.clear();

            for (FaceInfo info : faceInfoList) {

                Rect rect  =mirrorRectHorizontal(info.getRect());

                rectList.add(rect);
            }

            faceRectView.setRectInfo(rectList, previewWidth, previewHeight, viewWidth, viewHeight);

            processFaceInfo(data,camera);

        }

    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCameraError(String msg) {

    }

    @Override
    public void onCameraConfigurationChanged(int cameraId, int orientation) {

    }

    private void processFaceInfo(final byte[] data, final Camera camera) {

        if (inProgress) return;

        inProgress = true;

        //prevent cleared by other frame detect
        final List<FaceInfo> list = new ArrayList<>(faceInfoList);

        Runnable task = new Runnable() {
            @Override
            public void run() {

                String userId = findPerson(data, list);

                if (isEmpty(userId)) {

                    inProgress = false;

                    return;
                }

//                boolean isAlivePerson = isAlivePerson(data, list);
//                if (!isAlivePerson) {
//                    inProgress = false;
//                    return;
//                }
                showPerson(userId,camera);
            }
        };

        pool.execute(task);
    }

   private Timer timer = new Timer();

    private void showPerson(final String userId,Camera camera) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                rgbView.stop();
//                irView.stop();
                detectSuccess(userId);
                inProgress = false;
            }
        });

        isRecognitioned  = true;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isRecognitioned = false;
            }
        }, 2000);

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                ZLog.i("Socket","Take A Picture");

                byte[] encoded = Base64.encode(data, Base64.DEFAULT);

                String  faceData = new String(encoded);

                camera.startPreview();

                Employee employee = DatabaseHelper.getInstance(FaceDetectActivity.this).queryEmployee(userId);

                try {

                    jsonObject.put(personId,employee.userid);

                    jsonObject.put(name,employee.username);

                    jsonObject.put(shelf,Config.getShelf(FaceDetectActivity.this));

//                    jsonObject.put(snapshot,faceData);
                    jsonObject.put(snapshot,"");

                } catch (JSONException e) {

                    e.printStackTrace();

                }

                showToast("Hi  "+employee.username+",  your userId is  "+employee.userid);

                SocketUtils.getInstance().getSendFaceSocket().emit(SocketUtils.MSG,jsonObject);

            }
        });

    }


    /**
     * 人脸框水平镜像
     *
     * @param rect 人脸框
     * @return 水平镜像后的人脸框
     */
    @NotNull
    private Rect mirrorRectHorizontal(Rect rect) {

        Rect newRect = new Rect(rect);

        newRect.right = previewWidth - rect.left;

        newRect.left =  previewWidth - rect.right;

        return newRect;
    }

    @Nullable
    private String findPerson(byte[] data, @NotNull List<FaceInfo> list) {

        FaceFeature faceFeature = new FaceFeature();

        int code = faceEngine.extractFaceFeature(data, previewWidth, previewHeight, FaceEngine.CP_PAF_NV21, list.get(0), faceFeature);

        if (code != ErrorInfo.MOK) {

            return null;

        }

        Map<String, FaceFeature> map = FaceCollections.getInstance().getFaceData();

        if (null == map || map.size() == 0) {

            return null;

        }

        String userId = null;

        for (Map.Entry<String, FaceFeature> entry : map.entrySet()) {

            FaceFeature feature = entry.getValue();

            if (null != feature) {

                FaceSimilar faceSimilar = new FaceSimilar();

                code = faceEngine.compareFaceFeature(faceFeature, feature, faceSimilar);

                if (code == ErrorInfo.MOK) {

                    float score = faceSimilar.getScore();

                    if (Float.compare(score, 0.8f) >= 0) {

                        userId = entry.getKey();

                        break;

                    }
                }
            }
        }

        return userId;
    }

    private boolean isAlivePerson(byte[] data, List<FaceInfo> list) {
        int code = faceEngine.process(data, previewWidth, previewHeight, CP_PAF_NV21, list, FaceEngine.ASF_LIVENESS);
        if (code != ErrorInfo.MOK) {
            return false;
        }

        livenessList.clear();
        code = faceEngine.getLiveness(livenessList);
        int rgbAlive = -1;
        if (code == ErrorInfo.MOK && livenessList.size() > 0) {
            rgbAlive = livenessList.get(0).getLiveness();
        }

        int irAlive = -1;
        if (null != irData && irData.length > 0) {
            code = irEngine.processIr(irData, previewWidth, previewHeight, CP_PAF_NV21, list, FaceEngine.ASF_IR_LIVENESS);
            if (code == ErrorInfo.MOK) {
                livenessList.clear();
                code = irEngine.getIrLiveness(livenessList);
                if (code == ErrorInfo.MOK && livenessList.size() > 0) {
                    irAlive = livenessList.get(0).getLiveness();
                }
            }
        }

        return rgbAlive == LivenessInfo.ALIVE && irAlive == LivenessInfo.ALIVE;
    }


     /**
     * 图片转化成base64字符串
     * @param imgPath
     * @return
     */
     public  String ImageToBase64(String imgPath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
                 InputStream in = null;
                 byte[] data = null;
                 try {
                         // 读取图片字节数组
                         in = new FileInputStream(imgPath);
                         data = new byte[in.available()];
                         in.read(data);
                     } catch (IOException e) {
                         e.printStackTrace();
                     } finally {
                         try {
                                 in.close();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                     }
         // 对字节数组进行Base64编码，得到Base64编码的字符串
//            BASE64Encoder encoder = new BASE64Encoder();
//            String encodedString = encoder.encode(data);
         String encodedString =  Base64.encodeToString(data,Base64.DEFAULT);
//
         return  encodedString;
             }
}
