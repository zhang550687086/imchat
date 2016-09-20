package zj.chat.com.imchat.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import zj.chat.com.imchat.util.ImgBitmatUtils;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final int DEGRESS = 90; // 0度

    // 参照： http://blog.sina.com.cn/s/blog_5a48dd2d0100tmcn.html
    private SurfaceHolder holder = null;
    private Camera camera = null;
    private String mPhotoPath = null; // 图片路径
    private CameraListener mListener = null;
    private int mCameraIndex = -1; // 摄像头前后

    // 摄像头监听
    public interface CameraListener {
        public void onTakePhotoSucc(String path);
    }

    // 构造函数
    public CameraView(Context context) {
        super(context);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    // 设置监听
    public void setListener(CameraListener listener) {
        mListener = listener;
    }

    // 初始化视图
    @SuppressWarnings("deprecation")
    private void initView() {
        holder = getHolder();// 生成Surface Holder
        holder.addCallback(this);
        // 指定Push Buffer
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraIndex = FindBackCamera();
        if (mCameraIndex == -1) {
            mCameraIndex = FindFrontCamera();
        }
    }

    // 设置图片存储路径
    public void setPhotoPath(String path) {
        mPhotoPath = path;
    }

    // 获取图片存储路径
    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void surfaceCreated(SurfaceHolder holder) {// Surface生成事件的处理
        try {
            holder.setFixedSize(this.getWidth(), this.getHeight());
            // 摄像头的初始化
            camera = Camera.open(mCameraIndex);
            if (camera != null) {
                camera.setDisplayOrientation(DEGRESS);
                camera.setPreviewDisplay(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Surface改变事件的处理
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setCameraPreview(holder);
    }

    // Surface销毁时的处理
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    // 屏幕触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 按下时自动对焦
        if (camera != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            //camera.autoFocus(null);
        }
        return true;
    }

    // 拍照
    public void onTakenPicture() {
        if (camera != null) {
            camera.takePicture(null, null, new TakePictureCallback());
        }
    }

    // 开始预览
    public void onStartPreview() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    // 暂停拍照
    public void onPausePreview() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    // 前后摄像头转换
    public void onSwitchCamera() {
        int cameraIndex = mCameraIndex;
        if (mCameraIndex == FindBackCamera()) {
            mCameraIndex = FindFrontCamera();
        } else {
            mCameraIndex = FindBackCamera();
        }

        if (mCameraIndex == -1) {
            mCameraIndex = cameraIndex;
            return;
        }

        surfaceDestroyed(holder);
        surfaceCreated(holder);
        setCameraPreview(holder);
    }

    // 查找是否有前置摄像头
    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    // 查找是否有后置摄像头
    private int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    // 设置摄像头开始预览
    private void setCameraPreview(SurfaceHolder holder) {
        if (camera == null || holder == null)
            return;

        Camera.Parameters parameters = camera.getParameters();
        try {
            camera.setParameters(parameters);// 设置参数
            // 通过SurfaceView显示取景画面
            camera.setPreviewDisplay(holder);
            camera.startPreview();// 开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TakePictureCallback implements PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                return;
            }

            File file = new File(mPhotoPath);
            if (file.isFile()) {
                file.delete();
            }

            // 判断是否为前置
            if (mCameraIndex != -1 && mCameraIndex == FindFrontCamera()) {
                Bitmap bitmap = ImgBitmatUtils.bytes2Bimap(data);
                if (bitmap != null) {
                    Bitmap tmpBmp = ImgBitmatUtils.convertBmp(bitmap);
                    bitmap.recycle();
                    bitmap = tmpBmp;
                }

                ImgBitmatUtils.saveBitmapToFile(mPhotoPath, ImgBitmatUtils.rotateBitmap(bitmap, 90),
                        CompressFormat.JPEG, 100);

                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } else {
                data2file(data, mPhotoPath);
                Bitmap bitmap = ImgBitmatUtils.bytes2Bimap(data);
                // 把图片旋转90度
                ImgBitmatUtils.saveBitmapToFile(mPhotoPath, ImgBitmatUtils.rotateBitmap(bitmap, 90),
                        CompressFormat.JPEG, 100);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

            try {
                if (mListener != null) {
                    mListener.onTakePhotoSucc(mPhotoPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ;

    // 将二进制数据转换为文件的函数
    private void data2file(byte[] w, String fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.flush();
            mPhotoPath = fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }
}
