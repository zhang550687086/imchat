package zj.chat.com.imchat.custom.view;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.common.BaseActivity;

/**
 * @author zj
 */
public class TakePhotoActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_TAKE_PHOTO_PATH = "path"; // 图片路径

    private CameraView mCameraView; // 摄像头头像
    private TextView mRemakePhoto; // 重拍
    private TextView mCancel; // 取消
    private TextView mUsePhoto; // 使用图片
    private ImageView mCameraSwitch; // 前后切换
    private ImageView mTakePhoto; // 拍照
    private boolean isCheckPhtot = false;//是否点击了拍照按钮

    private String mPhotoPath = null; // 图片路径
    private MediaPlayer shootMP = null;// 拍照声音

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remake_photo: { // 重拍
                isCheckPhtot = false;
                File file = new File(mPhotoPath);
                if (file.isFile()) {
                    file.delete();
                }
                mCameraView.onStartPreview();
                showTakePhotoView(true);
                break;
            }
            case R.id.tv_cancel: // 取消
                isCheckPhtot = false;
                finish();
                break;
            case R.id.tv_use_photo: // 使用图片
                isCheckPhtot = false;
                File file = new File(mPhotoPath);
                Intent intent = new Intent();
                intent.setData(Uri.fromFile(file));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_camera_switch: // 前后切换
                if (isCheckPhtot) {
                    return;
                }
                mCameraView.onSwitchCamera();
                break;
            case R.id.iv_take_photo: // 拍照
                isCheckPhtot = true;
                mCameraView.onTakenPicture();
                shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                shootMP.start();// 拍照声音
                break;
        }
    }

    /**
     * 显示拍照视图
     *
     * @param bTake : 拍照
     */
    private void showTakePhotoView(boolean bTake) {
        mRemakePhoto.setVisibility(bTake ? View.GONE : View.VISIBLE);
        mUsePhoto.setVisibility(bTake ? View.GONE : View.VISIBLE);
        mTakePhoto.setVisibility(bTake ? View.VISIBLE : View.INVISIBLE);
        mCameraSwitch.setVisibility(bTake ? View.VISIBLE : View.GONE);
        mCancel.setVisibility(bTake ? View.VISIBLE : View.GONE);
    }

    private CameraView.CameraListener mCameraListener = new CameraView.CameraListener() {
        @Override
        public void onTakePhotoSucc(String path) {
            mCameraView.onPausePreview();
            showTakePhotoView(false);
        }
    };


    @Override
    protected void initPageView() {
        // TODO Auto-generated method stub
        mCameraView = (CameraView) findViewById(R.id.cv_camera);
        mRemakePhoto = (TextView) findViewById(R.id.tv_remake_photo);
        mCancel = (TextView) findViewById(R.id.tv_cancel);
        mUsePhoto = (TextView) findViewById(R.id.tv_use_photo);
        mCameraSwitch = (ImageView) findViewById(R.id.iv_camera_switch);
        mTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);
    }


    @Override
    protected void initPageViewListener() {
        // TODO Auto-generated method stub

        mCameraView.setListener(mCameraListener);
        mRemakePhoto.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mUsePhoto.setOnClickListener(this);
        mCameraSwitch.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);
    }

    @Override
    protected int initContentLayoutID() {
        return R.layout.activity_whiteboard_take_photo;
    }

    @Override
    protected int initTitleLayoutID() {
        return 0;
    }


    @Override
    protected void getIntentData() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoPath = intent.getStringExtra(KEY_TAKE_PHOTO_PATH);
        }
        mCameraView.setPhotoPath(mPhotoPath);
        // 判断图片路径是否为空
        if (TextUtils.isEmpty(mPhotoPath)) {
        }

        File file = new File(mPhotoPath);
        if (file.isFile()) {
            file.delete();
        }
    }

}
