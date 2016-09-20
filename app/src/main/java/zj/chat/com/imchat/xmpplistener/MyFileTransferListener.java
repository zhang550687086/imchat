package zj.chat.com.imchat.xmpplistener;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;

import zj.chat.com.imchat.util.FilePathUtiles;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 文件传输监听
 */
public class MyFileTransferListener implements FileTransferListener {
    private String LOG = "MyFileTransferListener";
    private Context mContext = null;

    public MyFileTransferListener(Context mContex) {
        this.mContext = mContex;
    }

    @Override
    public void fileTransferRequest(final FileTransferRequest fileTransferRequest) {
        Log.i(LOG, "收到文件");
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(FilePathUtiles.CHAT_FILE_PATH);
                if (!file.exists()) {
                    FilePathUtiles.makeDir(file);
                }
                File fileName = new File(file, fileTransferRequest.getFileName());
                IncomingFileTransfer accept = fileTransferRequest.accept();
                try {
                    //接收文件
                    accept.recieveFile(fileName);
                    while (!accept.isDone()) {
                        if (accept.getStatus().equals(FileTransfer.Status.in_progress)) {
                            Log.i(LOG, "进度:" + accept.getProgress());
                        }

                    }
                } catch (Exception e) {
                    Log.i(LOG, "错误信息:" + e.toString());
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
