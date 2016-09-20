package zj.chat.com.imchat.room;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import zj.chat.com.imchat.cache.ben.ChatMsg;
import zj.chat.com.imchat.cache.ben.RoomChatMsg;
import zj.chat.com.imchat.cache.ben.RoomInfo;
import zj.chat.com.imchat.common.CommonUtils;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.util.ApplictionUtils;
import zj.chat.com.imchat.util.ChatUtils;
import zj.chat.com.imchat.util.ImgBitmatUtils;
import zj.chat.com.imchat.xmpplistener.AddRoomLinsener;
import zj.chat.com.imchat.xmpplistener.CreateRommLinsener;
import zj.chat.com.imchat.xmpplistener.GetRoomMenberLinsener;
import zj.chat.com.imchat.xmpplistener.MyParticipantStatusListener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 群聊管理类
 */
public class RoomManager {
    private String LOG = "RoomManager";
    private static RoomManager mRoomManager = null;
    private Context mContext = null;
    //群成员状态监听
    private MyParticipantStatusListener mMyParticipantStatusListener = null;
    private MultiUserChat muc = null;

    private RoomManager(Context mContext) {
        this.mContext = mContext;
    }

    public static RoomManager getRommManager(Context mContext) {
        if (mRoomManager == null) {
            mRoomManager = new RoomManager(mContext);
        }
        return mRoomManager;
    }

    /**
     * 初始化聊服务会议列表
     */
    public void initHostRoom() {
        Collection<HostedRoom> hostrooms;
        try {
            hostrooms = MultiUserChat.getHostedRooms(XmppConnect.getXmppConnectInstance(),
                    XmppConnect.getXmppConnectInstance().getServiceName());
            for (HostedRoom entry : hostrooms) {
                Log.i(LOG, "名字：" + entry.getName() + " - ID:" + entry.getJid());
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化房间列表
     */
    public ArrayList<DiscoverItems.Item> initRoom(String jid) {
        ArrayList<DiscoverItems.Item> listDiscoverItems = new ArrayList<DiscoverItems.Item>();
        // 获得与XMPPConnection相关的ServiceDiscoveryManager
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
                .getInstanceFor(XmppConnect.getXmppConnectInstance());
        // 获得指定XMPP实体的项目
        // 这个例子获得与在线目录服务相关的项目
        DiscoverItems discoItems;
        try {
            discoItems = discoManager.discoverItems(jid);
            // 获得被查询的XMPP实体的要查看的项目
            Iterator it = discoItems.getItems();
            // 显示远端XMPP实体的项目
            while (it.hasNext()) {
                DiscoverItems.Item item = (DiscoverItems.Item) it.next();
                Log.i(LOG, "房间名：" + item.getName() + " - ID:" + item.getEntityID());
                listDiscoverItems.add(item);
            }
        } catch (XMPPException e) {
            Log.i(LOG, e.toString());
            e.printStackTrace();
        }
        return listDiscoverItems;
    }

    /**
     * 创建聊天室
     */
    public void createRoom(String roomName, final CreateRommLinsener mCreateRommLinsener) {
        new AsyncTask<String, Void, Exception>() {
            @Override
            protected Exception doInBackground(String... params) {
                String rm = params[0];
                MultiUserChat muc = new MultiUserChat(XmppConnect.getXmppConnectInstance(), rm + "@conference." + XmppUtils.serverIp);

                try {
                    // 创建聊天室 设置进入房间昵称
                    muc.create("test");
                    // 获得聊天室的配置表单
                    Form form = muc.getConfigurationForm();
                    // 根据原始表单创建一个要提交的新表单。
                    Form submitForm = form.createAnswerForm();
                    // 向要提交的表单添加默认答复
                    for (Iterator fields = form.getFields(); fields.hasNext(); ) {
                        FormField field = (FormField) fields.next();
                        if (!FormField.TYPE_HIDDEN.equals(field.getType())
                                && field.getVariable() != null) {
                            // 设置默认值作为答复
                            submitForm.setDefaultAnswer(field.getVariable());
                        }
                    }
                    // 设置聊天室的新拥有者
                    List<String> owners = new ArrayList<String>();
                    owners.add(XmppConnect.getXmppConnectInstance().getUser());
                    submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                    // 设置聊天室是持久聊天室，即将要被保存下来
                    submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                    // 房间仅对成员开放
                    submitForm.setAnswer("muc#roomconfig_membersonly", false);
                    // 允许占有者邀请其他人
                    submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                    // 能够发现占有者真实 JID 的角色
                    submitForm.setAnswer("muc#roomconfig_whois", "anyone");
                    // 进入是否需要密码
                    submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
                            false);
                    // 登录房间对话
                    submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                    // 仅允许注册的昵称登录
                    submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                    // 允许使用者修改昵称
                    submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
                    // 允许用户注册房间
                    submitForm.setAnswer("x-muc#roomconfig_registration", false);
                    // 发送已完成的表单（有默认值）到服务器来配置聊天室
                    muc.sendConfigurationForm(submitForm);
                    return null;
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.i(LOG, e.toString());
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                if (e == null) {//没有异常说明创建成功
                    mCreateRommLinsener.onCreateSuceed();
                } else {
                    mCreateRommLinsener.onCreateFailure(e);
                }

            }
        }.execute(roomName);
    }

    /**
     * 获取一个群聊
     *
     * @param groupJid
     * @return
     */
    public MultiUserChat getChatRoomManager(String groupJid) {
        MultiUserChat muc = new MultiUserChat(XmppConnect.getXmppConnectInstance(), groupJid);
        return muc;
    }

    /**
     * 返回一个房间管理对象
     *
     * @return
     */
    public MultiUserChat getMultiUserChat() {
        return muc;
    }

    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    public boolean sendRoomMessage(String msg) {

        try {
            muc.sendMessage(msg);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送声音
     *
     * @param cm
     */
    public void sendVoice(RoomChatMsg cm) throws XMPPException {
        //这里为了聊天记录缓存，所以fromUser其实是发送者
        String from = cm.getFromUser();
        //把声音转换成字符串
        String content = CommonUtils.GetVoiceStr(cm.getContentPath());
        cm.setContent(content);
        String msg = ChatUtils.appenSendMsg(cm);

        try {
            muc.sendMessage(msg);

        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送群图片
     *
     * @param
     * @return
     */
    public boolean sendRoomImgMessage(RoomChatMsg cm, Activity activity) throws XMPPException {
        //先把图片压缩
        ImgBitmatUtils.compressImgSize(cm.getContentPath(), activity);
        //把图片转换成字符串
        String content = CommonUtils.getImageBase64(cm.getContentPath());
        cm.setContent(content);
        String msg = ChatUtils.appenSendMsg(cm);

        try {
            muc.sendMessage(msg);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取聊天室的所有成员
     * qId  群ID
     */
    public ArrayList<RoomInfo> getAllMember(MultiUserChat muc) {
        ArrayList<RoomInfo> ris = new ArrayList<RoomInfo>();
        try {
            Iterator<String> it = muc.getOccupants();
            while (it.hasNext()) {
                String name = it.next();
                name = name.substring(name.indexOf("/") + 1);
                RoomInfo ri = new RoomInfo();
                ri.setRoomJid(it.next());
                ri.setRoomName(name);
                ris.add(ri);
                Log.i(LOG, "成员名字;" + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ris;

    }

    /**
     * 获取群成员
     *
     * @param
     * @return
     */
    public void getAllMember(final GetRoomMenberLinsener mGetRoomMenberLinsener) {
        new AsyncTask<Void, Void, ArrayList<RoomInfo>>() {
            @Override
            protected ArrayList<RoomInfo> doInBackground(Void... params) {
                ArrayList<RoomInfo> ris = new ArrayList<RoomInfo>();
                try {
                    Iterator<String> it = muc.getOccupants();
                    while (it.hasNext()) {
                        String name = it.next();
                        name = name.substring(name.indexOf("/") + 1);
                        RoomInfo ri = new RoomInfo();
                        ri.setRoomJid(it.next());
                        ri.setRoomName(name);
                        ris.add(ri);
                        Log.i(LOG, "成员名字;" + name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ris;
            }

            @Override
            protected void onPostExecute(ArrayList<RoomInfo> roomInfos) {
                super.onPostExecute(roomInfos);
                mGetRoomMenberLinsener.onGetRoomMenberSucuss(roomInfos);

            }
        }.execute();
    }

    /**
     * 要求人的ID
     *
     * @param userJid  邀请时说的话
     * @param initeMsg
     */
    public void inviteFriend(String userJid, String initeMsg) {
        muc.invite(userJid, initeMsg);
    }

    /**
     * 进入房间
     *
     * @param jid 房间的JID
     */
    public void intoRoom(final String jid, final AddRoomLinsener mAddRoomLinsener) {
        new AsyncTask<String, Void, Exception>() {
            @Override
            protected Exception doInBackground(String... params) {
                String mid = params[0];
                try {
                    muc = new MultiUserChat(XmppConnect.getXmppConnectInstance(), mid);
                    //设置进入后获取多少条历史记录
                    DiscussionHistory history = new DiscussionHistory();
                    history.setMaxChars(0);
                    Log.i(LOG, "id:" + mid);
                    // 创建聊天室,进入房间后的nickname(昵称)
                    String nNmae = MyApplication.getMyApplication().getMyPreferenceManager().readString(ApplictionUtils.USER_NAME_NET, "");
                    Log.i(LOG, "nanem:" + nNmae);
                    muc.join(nNmae, "", history, SmackConfiguration.getPacketReplyTimeout());
                    if (mMyParticipantStatusListener == null) {
                        initLinsener();
                    }
                    muc.addParticipantStatusListener(mMyParticipantStatusListener);
                    //获取成员名称
                    return null;
                } catch (XMPPException e) {
                    e.printStackTrace();
                    return e;
                }

            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                if (e == null) {
                    mAddRoomLinsener.onAddRoomSucess();
                } else {
                    mAddRoomLinsener.onAddRoomError(e);
                }
            }
        }.execute(jid);
    }

    /**
     * 初始化监听
     */
    public void initLinsener() {
        mMyParticipantStatusListener = new MyParticipantStatusListener(mContext);

    }

    public void closeLinsener() {
        if (muc != null && mMyParticipantStatusListener != null) {
            muc.removeParticipantStatusListener(mMyParticipantStatusListener);
            mMyParticipantStatusListener = null;
        }
    }

}
