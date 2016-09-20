package zj.chat.com.imchat.connect;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import zj.chat.com.imchat.cache.ben.ChatMsg;
import zj.chat.com.imchat.common.CommonUtils;
import zj.chat.com.imchat.util.ChatUtils;
import zj.chat.com.imchat.util.ImgBitmatUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class XmppUtils {
    private static String LOG = "XmppUtils";
    //服务器IP
    public static final String serverIp = "www.myzhangjun.com";
    //服务器端口
    public static final int port = 5222;
    public static final String MSG_TYPE_ADD_FRIEND = "msg_type_add_friend";//添加好友
    public static final String MSG_TYPE_ADD_FRIEND_SUCCESS = "msg_type_add_friend_success";//同意添加好友

    /**
     * 获取用户名称
     *
     * @param fullUserName : 全用户名(用户名@服务器IP)
     */
    public static String getUserName(String fullUserName) {
        if (TextUtils.isEmpty(fullUserName))
            return "";
        return fullUserName.split("@")[0];
    }

    public static String getQdF(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        return str.split(":")[0];
    }

    /**
     * 发送消息
     *
     * @param content
     * @param from
     * @throws XMPPException
     */
    public static void sendMessage(XMPPConnection mXMPPConnection, String content, String from) throws XMPPException {
        if (mXMPPConnection == null || !mXMPPConnection.isConnected()) {
            throw new XMPPException();
        }
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        if (!from.contains("@")) {//如果不是全名称
            from = from + "@" + serverIp;
        }
        Chat chat = chatmanager.createChat(from, null);
        if (chat != null) {
            chat.sendMessage(content);
        }
    }
    public static  void sendCustomMessage(String msg){

    }
    /**
     * 发送图片
     */
    public static void sendImg(ChatMsg cm, Activity activity) throws XMPPException {
        //这里为了聊天记录缓存，所以fromUser其实是发送者
        String from = cm.getFromUser();
        //先把图片压缩
        ImgBitmatUtils.compressImgSize(cm.getContentPath(), activity);
        //把图片转换成字符串
        String content = CommonUtils.getImageBase64(cm.getContentPath());
        cm.setContent(content);
        String msg = ChatUtils.appenSendMsg(cm);
        XMPPConnection mXMPPConnection = XmppConnect.getXmppConnectInstance();
        if (mXMPPConnection == null || !mXMPPConnection.isConnected()) {
            throw new XMPPException();
        }
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        if (!from.contains("@")) {//如果不是全名称
            from = from + "@" + serverIp;
        }
        Chat chat = chatmanager.createChat(from, null);
        if (chat != null) {
            chat.sendMessage(msg);
        }
    }

    /**
     * 发送声音
     *
     * @param cm
     */
    public static void sendVoice(ChatMsg cm) throws XMPPException {
        //这里为了聊天记录缓存，所以fromUser其实是发送者
        String from = cm.getFromUser();
        //把声音转换成字符串
        String content = CommonUtils.GetVoiceStr(cm.getContentPath());
        cm.setContent(content);
        String msg = ChatUtils.appenSendMsg(cm);
        XMPPConnection mXMPPConnection = XmppConnect.getXmppConnectInstance();
        if (mXMPPConnection == null || !mXMPPConnection.isConnected()) {
            throw new XMPPException();
        }
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        if (!from.contains("@")) {//如果不是全名称
            from = from + "@" + serverIp;
        }
        Chat chat = chatmanager.createChat(from, null);
        if (chat != null) {
            chat.sendMessage(msg);
        }
    }

    /**
     * 注册
     *
     * @param account  注册帐号
     * @param password 注册密码
     * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
     */
    public static int register(XMPPConnection mXMPPConnection, String account, String password) {
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(mXMPPConnection.getServiceName());
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
        reg.setUsername(account);
        reg.setPassword(password);
        // 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
        reg.addAttribute("android", "geolo_createUser_android");
        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = mXMPPConnection.createPacketCollector(filter);
        mXMPPConnection.sendPacket(reg);
        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        // Stop queuing results停止请求results（是否成功的结果）
        collector.cancel();
        if (result == null) {
            return 0;
        } else if (result.getType() == IQ.Type.RESULT) {
            return 1;
        } else {
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    /**
     * 查询用户
     *
     * @param userName
     * @return
     * @throws XMPPException
     */
    public static List<String> searchUsers(String userName) {
        XMPPConnection mXMPPConnection = XmppConnect.getXmppConnectInstance();
        List<String> listUser = new ArrayList<String>();
        try {
            UserSearchManager search = new UserSearchManager(mXMPPConnection);
            //此处一定要加上 search.
            Form searchForm = search.getSearchForm("search." + mXMPPConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = search.getSearchResults(answerForm, "search." + mXMPPConnection.getServiceName());
            Iterator<ReportedData.Row> it = data.getRows();
            ReportedData.Row row = null;
            while (it.hasNext()) {
                row = it.next();
                listUser.add(row.getValues("Username").next().toString());
            }
        } catch (Exception e) {

        }
        return listUser;
    }

    /**
     * 更改用户状态
     */
    public static void setPresence(XMPPConnection con, int code) {
        if (con == null)
            return;
        Presence presence;
        switch (code) {
            case 0:
                presence = new Presence(Presence.Type.available);  //在线
                con.sendPacket(presence);
                break;
            case 1:
                presence = new Presence(Presence.Type.available);  //设置Q我吧
                presence.setMode(Presence.Mode.chat);
                con.sendPacket(presence);
                break;
            case 2:
                presence = new Presence(Presence.Type.available);  //设置忙碌
                presence.setMode(Presence.Mode.dnd);
                con.sendPacket(presence);
                break;
            case 3:
                presence = new Presence(Presence.Type.available);  //设置离开
                presence.setMode(Presence.Mode.away);
                con.sendPacket(presence);
                break;
            case 4:                                                                                      //隐身
                Roster roster = con.getRoster();
                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {
                    presence = new Presence(Presence.Type.unavailable);
                    presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                    presence.setFrom(con.getUser());
                    presence.setTo(entry.getUser());
                    con.sendPacket(presence);
                    Log.v("jj", presence.toXML());
                }
                // 向同一用户的其他客户端发送隐身状态
                presence = new Presence(Presence.Type.unavailable);
                presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                presence.setFrom(con.getUser());
                presence.setTo(StringUtils.parseBareAddress(con.getUser()));
                con.sendPacket(presence);
                Log.v("jj", "设置隐身");
                break;
            case 5:
                presence = new Presence(Presence.Type.unavailable);  //离线
                con.sendPacket(presence);
                break;
            default:
                break;
        }
    }

    /**
     * 删除当前用户
     *
     * @param connection
     * @return
     */
    public static boolean deleteAccount(XMPPConnection connection) {
        try {
            connection.getAccountManager().deleteAccount();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回所有组信息 <RosterGroup>
     *
     * @return List(RosterGroup)
     */
    public static List<RosterGroup> getGroups(Roster roster) {
        List<RosterGroup> groupsList = new ArrayList<RosterGroup>();
        Collection<RosterGroup> rosterGroup = roster.getGroups();
        Iterator<RosterGroup> i = rosterGroup.iterator();
        while (i.hasNext())
            groupsList.add(i.next());
        return groupsList;
    }

    /**
     * 返回相应(groupName)组里的所有用户<RosterEntry>
     *
     * @return List(RosterEntry)
     */
    public static List<RosterEntry> getEntriesByGroup(Roster roster,
                                                      String groupName) {
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
        RosterGroup rosterGroup = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext())
            EntriesList.add(i.next());
        return EntriesList;
    }

    /**
     * 返回所有用户信息 <RosterEntry>
     *
     * @return List(RosterEntry)
     */
    public static List<RosterEntry> getAllEntries(Roster roster) {
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            RosterEntry rosterentry = (RosterEntry) i.next();
            Log.e("jj", "好友：" + rosterentry.getUser() + "," + rosterentry.getName() + "," + rosterentry.getType().name());
            EntriesList.add(rosterentry);
        }
        return EntriesList;
    }


    /**
     * 创建一个组
     */
    public static boolean addGroup(Roster roster, String groupName) {
        try {
            roster.createGroup(groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("jj", "创建分组异常：" + e.getMessage());
            return false;
        }
    }

    /**
     * 删除一个组
     */
    public static boolean removeGroup(Roster roster, String groupName) {
        return false;
    }

    /**
     * 添加一个好友  无分组
     */
    public static boolean addUser(Roster roster, String userName, String name) {
        try {
            roster.createEntry(userName, name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 添加一个好友到分组
     *
     * @param roster
     * @param userName
     * @param name
     * @return
     */
    public static boolean addUsers(Roster roster, String userName, String name, String groupName) {
        try {
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("jj", "添加好友异常：" + e.getMessage());
            return false;
        }

    }

    /**
     * 删除一个好友
     *
     * @param roster
     * @param userJid
     * @return
     */
    public static boolean removeUser(Roster roster, String userJid) {
        try {
            RosterEntry entry = roster.getEntry(userJid);
            roster.removeEntry(entry);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 把一个好友添加到一个组中
     *
     * @param userJid
     * @param groupName
     */
    public static void addUserToGroup(final String userJid, final String groupName,
                                      final XMPPConnection connection) {
        RosterGroup group = connection.getRoster().getGroup(groupName);
        // 这个组已经存在就添加到这个组，不存在创建一个组
        RosterEntry entry = connection.getRoster().getEntry(userJid);
        try {
            if (group != null) {
                if (entry != null)
                    group.addEntry(entry);
            } else {
                RosterGroup newGroup = connection.getRoster().createGroup("我的好友");
                if (entry != null)
                    newGroup.addEntry(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把一个好友从组中删除
     *
     * @param userJid
     * @param groupName
     */
    public static void removeUserFromGroup(final String userJid, final String groupName, final XMPPConnection connection) {
        RosterGroup group = connection.getRoster().getGroup(groupName);
        if (group != null) {
            try {
                RosterEntry entry = connection.getRoster().getEntry(userJid);
                if (entry != null)
                    group.removeEntry(entry);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改心情
     *
     * @param connection
     * @param status
     */
    public static void changeStateMessage(final XMPPConnection connection, final String status) {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(status);
        connection.sendPacket(presence);
    }
}
