package zj.chat.com.imchat.xmpplistener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import zj.chat.com.imchat.cache.ben.FriendCliend;
import zj.chat.com.imchat.util.ChatUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class FriendPacketListener implements PacketListener {
    private Context mContext = null;
    private String LOG = "FriendPacketListener";

    public FriendPacketListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet.getFrom().equals(packet.getTo())) {
            return;
        }
        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            String from = ChatUtils.deletcD(presence.getFrom());//发送方
            String to = presence.getTo();//接收方
            if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请
                Log.e(LOG, "好友申请");
            } else if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友
                Log.e(LOG, "同意添加好友");
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//拒绝添加好友  和  删除好友
                Log.e(LOG, "拒绝添加好友");
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {

            } else if (presence.getType().equals(Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表
                FriendCliend fc = new FriendCliend();
                fc.setUser(from);
                fc.setAvailable(false);
                Intent intent = new Intent(ChatUtils.FRIEND_STATES_REC);
                intent.putExtra(ChatUtils.FRIEND_STATES, fc);
                mContext.sendBroadcast(intent);
                Log.e(LOG, "好友下线");
            } else if (presence.getType().equals(Presence.Type.available)) {//好友上线
                FriendCliend fc = new FriendCliend();
                fc.setUser(from);
                fc.setAvailable(true);
                Intent intent = new Intent(ChatUtils.FRIEND_STATES_REC);
                intent.putExtra(ChatUtils.FRIEND_STATES, fc);
                mContext.sendBroadcast(intent);
                Log.e(LOG, "好友上线");
            } else {
                Log.e(LOG, "error");
            }
        }
    }
}
