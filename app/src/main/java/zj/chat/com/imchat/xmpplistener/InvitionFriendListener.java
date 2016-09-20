package zj.chat.com.imchat.xmpplistener;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 好友要请监听
 */
public class InvitionFriendListener implements InvitationListener {
    private String LOG = "InvitionFriendListener";
    private Context mContext = null;

    public InvitionFriendListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void invitationReceived(Connection connection, String room, String inviter, String reason, String password, Message message) {
        Log.i(LOG, "收到来自 " + inviter + " 的聊天室邀请。邀请附带内容："
                + reason);
    }
}
