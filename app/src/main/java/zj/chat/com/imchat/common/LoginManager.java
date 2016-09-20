package zj.chat.com.imchat.common;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import zj.chat.com.imchat.bin.UserInfo;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.xmpplistener.FriendPacketListener;
import zj.chat.com.imchat.xmpplistener.InvitionFriendListener;
import zj.chat.com.imchat.xmpplistener.MyFileTransferListener;
import zj.chat.com.imchat.xmpplistener.XmppConnectionListener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class LoginManager {
    private static LoginManager mLoginManager = null;
    //连接监听器
    private XmppConnectionListener mXmppConnectionListener = null;
    private XMPPConnection xmppConnectInstance = null;
    private String LOG = "LoginManager:";
    //好友状态监听器
    private FriendPacketListener mFriendPacketListener = null;
    //房间邀请好友监听
    private InvitionFriendListener mInvitionFriendListener = null;
    //文件发送监听
    private MyFileTransferListener mMyFileTransferListener = null;

    private LoginManager() {//设置为单例
        initXmppListener();
    }

    /**
     * 返回一个实例
     */
    public static LoginManager getInstance() {
        if (mLoginManager == null) {
            mLoginManager = new LoginManager();
        }

        return mLoginManager;
    }


    /**
     * 实例化监听
     */
    private void initXmppListener() {
        mXmppConnectionListener = new XmppConnectionListener();
        mFriendPacketListener = new FriendPacketListener(MyApplication.getMyApplication().getApplicationContext());
        mInvitionFriendListener = new InvitionFriendListener(MyApplication.getMyApplication().getApplicationContext());
        mMyFileTransferListener = new MyFileTransferListener(MyApplication.getMyApplication().getApplicationContext());
    }

    /**
     * 获取连接监听
     *
     * @return
     */
    public XmppConnectionListener getXmppConnectionListener() {
        return mXmppConnectionListener;
    }

    /**
     * 登录回调接口
     */
    public interface OnLoginListener {
        void onLoginSuccess();

        void onLoginErrer(Exception e);

    }

    /**
     * 用户登录
     *
     * @param userInfo
     * @param mLoginListener
     */
    public void LoginXmpp(final UserInfo userInfo, final OnLoginListener mLoginListener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (userInfo == null) {
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mLoginListener.onLoginErrer(new Exception("userName or passwor for null"));
                        }
                    });
                }

                String oldUser = "";
                if (xmppConnectInstance != null) {
                    oldUser = XmppUtils.getUserName(xmppConnectInstance.getUser());
                }


                if (oldUser.equals(userInfo.getUserName())) {//如果当前用户和已经登录相同
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //登录成功
                            mLoginListener.onLoginSuccess();
                        }
                    });
                    return;
                } else if (!"".equals(oldUser)) {//如果当前登录用户和已经登录不一致
                    deleteCurrUser();
                }
                if (xmppConnectInstance == null) {
                    xmppConnectInstance = XmppConnect.getXmppConnectInstance();
                }
                if (!XmppConnect.isConnected()) {
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //登录失败
                            mLoginListener.onLoginErrer(new Exception("连接服务器失败!"));
                        }
                    });
                    return;
                }
                try {//登录成功就去好友列表界面
                    xmppConnectInstance.login(userInfo.getUserName(), userInfo.getPassword(), "android");
                    // 配置在线状态
                    Presence presence = new Presence(Presence.Type.available);
                    xmppConnectInstance.sendPacket(presence);
                    if (getXmppConnectionListener() == null || mFriendPacketListener == null) {
                        initXmppListener();
                    }
                    //设置成功设置连接监听器
                    xmppConnectInstance.addConnectionListener(getXmppConnectionListener());
                    PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
                    xmppConnectInstance.addPacketListener(mFriendPacketListener, filter);
                    /**
                     * 添加一个房间邀请的监听
                     */
                    MultiUserChat.addInvitationListener(XmppConnect.getXmppConnectInstance(), mInvitionFriendListener);
                    /**
                     * 添加一个文件传输监听
                     */
                    //  FileSendManager.getFileSendManager(MyApplication.getMyApplication().getApplicationContext()).FileTransFerListener(XmppConnect.getXmppConnectInstance());
                    FileTransferManager ftm = new FileTransferManager(XmppConnect.getXmppConnectInstance());
                    ftm.addFileTransferListener(mMyFileTransferListener);
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //登录成功
                            mLoginListener.onLoginSuccess();

                        }
                    });
                } catch (final XMPPException e) {//失败就提示失败
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //登录失败要去关闭连接从新获取新的连接
                            deleteCurrUser();
                            //登录失败
                            mLoginListener.onLoginErrer(e);
                        }
                    });
                    // e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    /**
     * 删除当前登录用户
     *
     * @return
     */
    private void deleteCurrUser() {
        Log.i(LOG, "关闭连接");
        if (mFriendPacketListener != null) {
            xmppConnectInstance.removePacketListener(mFriendPacketListener);
            mFriendPacketListener = null;
        }
        if (mXmppConnectionListener != null) {
            xmppConnectInstance.removeConnectionListener(mXmppConnectionListener);
        }

        xmppConnectInstance = null;
        XmppConnect.closeXmppConnect();

    }
}
