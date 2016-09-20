package zj.chat.com.imchat.xmpplistener;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;

import java.util.Timer;
import java.util.TimerTask;

import zj.chat.com.imchat.common.LoginManager;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppConnect;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * XMPP连接监听器
 */
public class XmppConnectionListener implements ConnectionListener {
    private String LOG = "XmppConnectionListener";
    private int logintime = 2000;
    private Timer timeTask = null;//

    @Override
    public void connectionClosed() {
        Log.i(LOG, "connectionClosed()");
        //关闭连接
        XmppConnect.closeXmppConnect();
        timeTask = new Timer();
        timeTask.schedule(new MyTimeTask(), logintime);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i(LOG, " connectionClosedOnError(Exception e)");
        // 盘点账号已经被登录
        boolean error = e.getMessage().equals("stream:error (conflict)");
        if (!error) {
            // 關閉連接
            XmppConnect.closeXmppConnect();
            // 重连服务器
            timeTask = new Timer();
            timeTask.schedule(new MyTimeTask(), logintime);
        }
    }

    @Override
    public void reconnectingIn(int i) {
        Log.i(LOG, " reconnectingIn(int i)");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i(LOG, " reconnectionSuccessful()");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.i(LOG, "reconnectionFailed(Exception e)");
    }

    private class MyTimeTask extends TimerTask {

        @Override
        public void run() {
            //去从新登录
            LoginManager.getInstance().LoginXmpp(MyApplication.getMyApplication().getCurrUserInfo(), new LoginManager.OnLoginListener() {
                @Override
                public void onLoginSuccess() {

                }

                @Override
                public void onLoginErrer(Exception e) {
                    timeTask.schedule(new XmppConnectionListener.MyTimeTask(), logintime);
                }
            });
        }
    }
}
