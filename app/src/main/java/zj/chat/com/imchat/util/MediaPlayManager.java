package zj.chat.com.imchat.util;

import android.media.MediaPlayer;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 声音播放类
 */
public class MediaPlayManager {
    private static MediaPlayManager mMediaPlayManager = null;
    private MediaPlayer mediaPlayer = null;

    private MediaPlayManager() {
        mediaPlayer = new MediaPlayer();
    }

    public static MediaPlayManager getmMediaPlayManager() {
        if (mMediaPlayManager == null) {
            mMediaPlayManager = new MediaPlayManager();
        }
        return mMediaPlayManager;
    }

    /**
     * 播放语音
     *
     * @param recPath
     */
    public void playMusic(String recPath) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(recPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setVolume((float) 0.81, (float) 0.82);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
