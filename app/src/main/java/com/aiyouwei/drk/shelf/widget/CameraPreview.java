package com.aiyouwei.drk.shelf.widget;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "CameraPreview";

    private     Camera  camera;

    private CameraListener listener;

    private int cameraId = -1;
    private int previewWidth = -1, previewHeight = -1;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void setCameraId(int cameraId, CameraListener listener) {
        this.cameraId = cameraId;
        this.listener = listener;
    }

    public void start() {
        if (-1 == cameraId || -1 == previewWidth) return;

        synchronized (this) {
            stop();
            boolean success = safeCameraOpen(cameraId);
            if (!success) {
                listener.onCameraError(String.format("camera id:%d open failed", cameraId));
                return;
            }

            try {
                camera.setPreviewDisplay(getHolder());
            } catch (IOException e) {
                listener.onCameraError("camera set preview failed: " + e.toString());
            }

            Camera.Parameters params = camera.getParameters();
            params.setPreviewFormat(ImageFormat.NV21);
            Camera.Size previewSize = params.getPreviewSize();
            List<Camera.Size> list = params.getSupportedPreviewSizes();
            if (null != list && list.size() > 0) {
                previewSize = getBestSupportedSize(list, previewWidth, previewHeight);
            }
            params.setPreviewSize(previewSize.width, previewSize.height);

            List<String> supportedFocusModes = params.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            }
            //设置相机预览照片帧数
            params.setPreviewFpsRange(4, 10);
            //设置图片格式
            params.setPictureFormat(ImageFormat.JPEG);
            //设置图片的质量
            params.set("jpeg-quality", 90);
            //设置照片的大小
            params.setPictureSize(previewSize.width, previewSize.height);
            camera.setParameters(params);
            camera.setDisplayOrientation(0);
            camera.startPreview();
            camera.setPreviewCallback(this);
            if (null != listener) {
                listener.onCameraOpened(camera, cameraId, previewSize.width, previewSize.height);
            }
        }
    }



    public void stop() {
        synchronized (this) {
            if (null == camera) return;
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            if (null != listener) listener.onCameraClosed();
        }
    }

    public void release() {
        synchronized (this) {
            stop();
            cameraId = -1;
            listener = null;
        }
    }

    private boolean safeCameraOpen(int id) {
        boolean opened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            opened = (null != camera);
        } catch (Exception e) {
            Log.i(TAG, "camera id " + id + " open failed" + e.toString());
        }

        return opened;
    }

    private void releaseCameraAndPreview() {
        if (null == camera) return;

        camera.release();
        camera = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        previewWidth = i1;
        previewHeight = i2;
        start();
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (null == camera) return;
        stop();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (null == listener) return;

        listener.onPreview(bytes, camera);

    }

    private Camera.Size getBestSupportedSize(@NotNull List<Camera.Size> list, int w, int h) {
        SparseArray<Camera.Size> array = new SparseArray<>(list.size());

        int min = Integer.MAX_VALUE;
        for (Camera.Size size : list) {
            int diff = Math.abs(size.width - w);
            min = Math.min(diff, min);
            array.put(diff, size);
        }

        return array.get(min);
    }

    public interface CameraListener {

        void onCameraOpened(Camera camera, int cameraId, int w, int h);

        void onPreview(byte[] data, Camera camera);

        void onCameraClosed();

        void onCameraError(String msg);

        void onCameraConfigurationChanged(int cameraId, int orientation);

    }


}
