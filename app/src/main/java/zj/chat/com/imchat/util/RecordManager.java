package zj.chat.com.imchat.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.io.PipedReader;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 录取声音类
 */
public class RecordManager {
    private Context mContext = null;
    private String recPath = "";//声音保存路径
    private MediaRecorder mMediaRecorder = null;//录音类
    private String LOG = "RecordManager";

    public RecordManager(Context mContext, String recPath) {
        this.mContext = mContext;
        this.recPath = recPath;
        initRecord();
    }

    private void initRecord() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setOutputFile(recPath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG, "prepare() failed");
        }
        mMediaRecorder.start();
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * 获取音量
     *
     * @return
     */
    public double getVolume() {
        return mMediaRecorder == null ? 0.0 : mMediaRecorder.getMaxAmplitude() / 2700.0;
    }

}
