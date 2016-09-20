package zj.chat.com.imchat.common;

import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.PushManager;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;

import java.io.File;

import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.xmpplistener.MyFileTransferListener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 文件发送管理类
 */
public class FileSendManager {
    private String LOG = "FileSendManager";
    private static FileSendManager mFileSendManager = null;
    private Context mContext;

    private FileSendManager(Context mContext) {
        this.mContext = mContext;
    }

    public static FileSendManager getFileSendManager(Context mContext) {
        if (mFileSendManager == null) {

            mFileSendManager = new FileSendManager(mContext);
        }
        return mFileSendManager;
    }

    public void sendFile(final String formJid, final String filePath) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File files = new File(filePath);
                if (!files.exists()) {
                    Log.i(LOG, "路径不存在：");
                    return;
                }
                FileTransferManager fileTransferManager = new FileTransferManager(XmppConnect.getXmppConnectInstance());
                OutgoingFileTransfer fileTransfer = fileTransferManager
                        .createOutgoingFileTransfer(formJid + "/android");
                Log.i(LOG, "userid::" + formJid);
                Log.i(LOG, "路径：" + files.toString());
                try {
                    fileTransfer.sendFile(files, "Sending");
                    while (!fileTransfer.isDone()) {
                        {
                            if (fileTransfer.getStatus().equals(FileTransfer.Status.error)) {
                                Log.e(LOG, "send failed");
                            } else {
                                Log.i(LOG, "status:" + fileTransfer.getStatus() + "|progress:" + fileTransfer.getProgress());
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    Log.i(LOG, "状态:" + fileTransfer.getStatus().toString());
                    Log.i(LOG, "进度:" + fileTransfer.getProgress());
                    Log.i(LOG, "获取图片成功");
                } catch (XMPPException e) {
                    Log.i(LOG, "发送异常:" + e.toString());
                    e.printStackTrace();
                }
            }
        }
        ).

                start();

    }

    public void FileTransFerListener(XMPPConnection xmppConnection) {
        // FileTransfer
        Log.i(LOG, "文件监听创建");
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager
                .getInstanceFor(xmppConnection);
        if (serviceDiscoveryManager == null) {
            serviceDiscoveryManager = new ServiceDiscoveryManager(
                    xmppConnection);
        }
        serviceDiscoveryManager
                .addFeature("http://jabber.org/protocol/disco#info");
        serviceDiscoveryManager.addFeature("jabber:iq:privacy");
        FileTransferNegotiator.setServiceEnabled(xmppConnection, true);
        FileTransferManager managerListner = new FileTransferManager(
                xmppConnection);
        managerListner.addFileTransferListener(new MyFileTransferListener(MyApplication.getMyApplication().getApplication()));
    }
}
