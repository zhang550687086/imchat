package zj.chat.com.imchat.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

import zj.chat.com.imchat.util.TimeUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 本地文件路径
 */
public class FilePathUtiles {
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/ImChat/";
    public static final String CHAT_IMG_FILE = FILE_PATH + "chatImg";
    public static final String CHAT_IMG_FILE_CACHE = FILE_PATH + "/cache";
    //声音保存本地路径
    public static final String CHAT_VOICE_PATH = FILE_PATH + "voice";
    public static final String CHAT_FILE_PATH = FILE_PATH + "file";

    /**
     * 获取路径
     *
     * @param file
     */
    public static void makeDir(File file) {
        file.mkdirs();

        try {
            new File(file.getAbsolutePath(), ".nomedia").createNewFile();
            File nomedia = new File(file.getAbsolutePath(), ".nomedia");
            nomedia.createNewFile();
            nomedia.setReadOnly();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 用当前时间给取得的图片命名
     *
     * @return
     */
    public static String getPhotoFileName() {
        String st = "img_" + TimeUtils.getCurrentTimeInLong();
        return st.concat(".jpeg").replaceAll(" ", "_").replaceAll(":", "-");
    }

    /**
     * 创建一个声音保存路径
     *
     * @return
     */
    public static String createRecordPath() {
        File file = new File(CHAT_VOICE_PATH);
        if (!file.exists()) {
            makeDir(file);
        }
        String name = System.currentTimeMillis() + ".amr";
        File recordPath = new File(file, name);
        return recordPath.toString();
    }

    /**
     * 从图库选择图片路径不对时，转换
     *
     * @param intent
     * @param mContext
     * @return
     */
    public static Uri geturi(android.content.Intent intent, Context mContext) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = mContext.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}
