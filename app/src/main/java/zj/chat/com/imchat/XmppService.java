package zj.chat.com.imchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import zj.chat.com.imchat.bin.UserInfo;
import zj.chat.com.imchat.cache.ben.ChatMsg;
import zj.chat.com.imchat.cache.ben.RoomChatMsg;
import zj.chat.com.imchat.cache.ben.Session;
import zj.chat.com.imchat.common.CommonUtils;
import zj.chat.com.imchat.common.LoginManager;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.db.ChatMsgDao;
import zj.chat.com.imchat.db.RoomChatMsgDao;
import zj.chat.com.imchat.db.SessionDao;
import zj.chat.com.imchat.util.ApplictionUtils;
import zj.chat.com.imchat.util.ChatUtils;
import zj.chat.com.imchat.util.MyPreferenceManager;
import zj.chat.com.imchat.util.StringUtils;
import zj.chat.com.imchat.util.TimeUtils;


/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * xmpp消息接收
 */
public class XmppService extends Service {
    private String LOG = "XmppService:";
    private PacketListener myListener = null;
    private MyPreferenceManager myPreferenceManager;
    private String userName;//用户名称
    private String pass;//用户密码
    private ChatMsgDao msgDao;
    private SessionDao sessionDao;
    private RoomChatMsgDao roomChatMsgDao;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG, "服务启动");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG, "onStartCommand()");
        myPreferenceManager = MyApplication.getMyApplication().getMyPreferenceManager();
        userName = myPreferenceManager.readString(ApplictionUtils.USER_NAME, "");
        pass = myPreferenceManager.readString(ApplictionUtils.USER_PASS, "");
        //先去取消监听
        init();
        return super.onStartCommand(intent, flags, startId);

    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unInitLisenter();
                LoginManager.getInstance().LoginXmpp(new UserInfo(userName, pass), new LoginManager.OnLoginListener() {
                    @Override
                    public void onLoginSuccess() {
                        initLinener();
                        Intent i = new Intent();
                        i.putExtra(ApplictionUtils.IS_LOGIN, true);
                        i.setAction(ApplictionUtils.LOG_RECVER);
                        sendBroadcast(i);

                    }

                    @Override
                    public void onLoginErrer(Exception e) {
                        Intent i = new Intent();
                        i.putExtra(ApplictionUtils.IS_LOGIN, false);
                        i.putExtra(ApplictionUtils.LOGIN_ERROR_MSG, e.toString());
                        i.setAction(ApplictionUtils.LOG_RECVER);
                        sendBroadcast(i);
                    }
                });

            }
        }).start();
    }

    private void initLinener() {
        sessionDao = new SessionDao(this);
        msgDao = new ChatMsgDao(this);
        roomChatMsgDao = new RoomChatMsgDao(this);
        PacketFilter filter = new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class);
        myListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) packet;
                // Log.i(LOG, "s:" + mes.getBody());
                Message msg = mHandler.obtainMessage();
                msg.obj = mes;
                msg.arg1 = 1;
                mHandler.sendMessage(msg);

            }
        };
        XmppConnect.getXmppConnectInstance().addPacketListener(myListener, filter);
    }

    private void unInitLisenter() {
        if (myListener != null) {
            XmppConnect.getXmppConnectInstance().removePacketListener(myListener);
            myListener = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ((Integer) msg.arg1) {
                case 1:
                    org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) msg.obj;
                    if (StringUtils.stringIsEmp(mes.getBody())) {//过滤掉空消息
                        return;
                    }
                    switch (mes.getType().ordinal()) {// normal,      chat, groupchat,  headline, error;
                        case 0:// normal只有推送才去发送notify
                            sendNotifyMsg(mes.getBody());
                            break;
                        case 1://聊天
                            String to;
                            String from;
                            String msgtype;
                            String msgcontent;
                            String msgtime;
                            if (mes.getBody().contains(ChatUtils.MSG_SPLIT)) {
                                //接收者卍发送者卍消息类型卍消息内容卍发送时间
                                String[] msgs = mes.getBody().split(ChatUtils.MSG_SPLIT);
                                from = msgs[0];//发送者，谁给你发的消息
                                to = msgs[1];//接收者,当然是自己
                                msgtype = msgs[2];//消息类型
                                msgcontent = msgs[3];//消息内容
                                msgtime = msgs[4];//消息时间
                            } else {
                                to = ChatUtils.deletcD(XmppConnect.getXmppConnectInstance().getUser());
                                from = ChatUtils.deletcD(mes.getFrom());
                                msgtype = ChatUtils.MSG_TYPE_TEXT;
                                msgcontent = mes.getBody();
                                msgtime = TimeUtils.getCurrentTimeAllString();
                            }
                            Session session = new Session();
                            session.setFrom(from);
                            session.setTo(to);
                            session.setNotReadCount("");//未读消息数量
                            session.setTime(msgtime);
                            session.setChatType(ChatUtils.D_CHAT);
                            if (msgtype.equals(ChatUtils.FRIEND_ADD)) {//添加好友的请求
                            } else if (msgtype.equals(ChatUtils.FRIEND_ADD)) {//对方同意添加好友的请求
                            } else if (msgtype.equals(ChatUtils.MSG_TYPE_TEXT)) {//文本类型
                                ChatMsg msg_text = new ChatMsg();
                                msg_text.setToUser(to);
                                msg_text.setFromUser(from);
                                msg_text.setIsComing(ChatUtils.SEDN_RECEIVE);
                                msg_text.setContent(msgcontent);
                                msg_text.setDate(msgtime);
                                msg_text.setIsReaded("1");//暂且默认为已读
                                msg_text.setType(msgtype);
                                msgDao.insert(msg_text);
                                session.setType(ChatUtils.MSG_TYPE_TEXT);
                                session.setContent(msgcontent);
                                if (sessionDao.isContent(from, to)) {//判断最近联系人列表是否已存在记录
                                    sessionDao.updateSession(session);
                                } else {
                                    sessionDao.insertSession(session);
                                }
                                //如果是当前正在聊天，发广播去显示
                                if (!StringUtils.stringIsEmp(ChatActivity.fromUserId) && ChatUtils.getUserName(from).equals(ChatUtils.getUserName(ChatActivity.fromUserId))) {
                                    Intent msgInten = new Intent();
                                    msgInten.putExtra(ChatUtils.MSG_KEY, msg_text);
                                    msgInten.setAction(ChatUtils.MSG_FRIEN_RECVER);
                                    sendBroadcast(msgInten);
                                }

                            } else if (msgtype.equals(ChatUtils.MSG_TYPE_IMG)) {
                                ChatMsg msg_img = new ChatMsg();
                                msg_img.setToUser(to);
                                msg_img.setFromUser(from);
                                msg_img.setIsComing(ChatUtils.SEDN_RECEIVE);
                                msg_img.setIsReaded("1");//暂且默认为已读
                                msg_img.setType(msgtype);
                                msg_img.setDate(msgtime);
                                //把字符串转换成图片。并保存在本地，返回一个图片路径
                                String imgPath = CommonUtils.GenerateImage(msgcontent, System.currentTimeMillis() + "");
                                msg_img.setContentPath(imgPath);
                                //插入数据库，并保存ID
                                msg_img.setMsgId(msgDao.insert(msg_img));
                                session.setType(ChatUtils.MSG_TYPE_IMG);
                                session.setContent("[图片]");
                                if (sessionDao.isContent(from, to)) {
                                    sessionDao.updateSession(session);
                                } else {
                                    sessionDao.insertSession(session);
                                }
                                if (!StringUtils.stringIsEmp(ChatActivity.fromUserId) && ChatUtils.getUserName(from).equals(ChatUtils.getUserName(ChatActivity.fromUserId))) {
                                    Intent msgInten = new Intent();
                                    msgInten.putExtra(ChatUtils.MSG_KEY, msg_img);
                                    msgInten.setAction(ChatUtils.MSG_FRIEN_RECVER);
                                    sendBroadcast(msgInten);
                                }
                            } else if (msgtype.equals(ChatUtils.MSG_TYPE_VOICE)) {//声音
                                ChatMsg voice_img = new ChatMsg();
                                voice_img.setToUser(to);
                                voice_img.setFromUser(from);
                                voice_img.setIsComing(ChatUtils.SEDN_RECEIVE);
                                voice_img.setIsReaded("1");//暂且默认为已读
                                voice_img.setType(msgtype);
                                voice_img.setDate(msgtime);
                                //把字符串转换成声音。并保存在本地，返回一个图片路径
                                String imgPath = CommonUtils.GenerateVoic(msgcontent, System.currentTimeMillis() + "");
                                voice_img.setContentPath(imgPath);
                                //插入数据库，并保存ID
                                voice_img.setMsgId(msgDao.insert(voice_img));
                                session.setType(ChatUtils.MSG_TYPE_VOICE);
                                session.setContent("[声音]");
                                if (sessionDao.isContent(from, to)) {
                                    sessionDao.updateSession(session);
                                } else {
                                    sessionDao.insertSession(session);
                                }
                                if (!StringUtils.stringIsEmp(ChatActivity.fromUserId) && ChatUtils.getUserName(from).equals(ChatUtils.getUserName(ChatActivity.fromUserId))) {
                                    Intent msgInten = new Intent();
                                    msgInten.putExtra(ChatUtils.MSG_KEY, voice_img);
                                    msgInten.setAction(ChatUtils.MSG_FRIEN_RECVER);
                                    sendBroadcast(msgInten);
                                }
                            }
                            Intent updatainten = new Intent();
                            updatainten.setAction(ApplictionUtils.FREND_UPDATA);
                            sendBroadcast(updatainten);

                            break;
                        case 2://接收群聊消息
                            String room_jid = "";
                            String room_to = "";//接收者默认为自己
                            String room_from;
                            String room_msgtype;
                            String room_msgcontent;
                            String room_msgtime;
                            if (mes.getBody().contains(ChatUtils.MSG_SPLIT)) {
                                //接收者卍发送者卍消息类型卍消息内容卍发送时间
                                String[] msgs = mes.getBody().split(ChatUtils.MSG_SPLIT);
                                room_jid = msgs[0];//房间jid
                                room_from = msgs[1];//发送者，谁给你发的消息
                                room_msgtype = msgs[2];//消息类型
                                room_msgcontent = msgs[3];//消息内容
                                room_msgtime = msgs[4];//消息时间
                                room_to = ChatUtils.deletcD(XmppConnect.getXmppConnectInstance().getUser());
                            } else {
                                room_from = ChatUtils.deletcD(mes.getFrom());
                                room_msgtype = ChatUtils.MSG_TYPE_TEXT;
                                room_msgcontent = mes.getBody();
                                room_msgtime = TimeUtils.getCurrentTimeAllString();
                            }
                            //如果接收到的消息和当前发送消息的身份一致，就不去通知更新了，
                            if (!StringUtils.stringIsEmp(room_from) && ChatUtils.getUserName(room_from).equals(ChatUtils.getUserName(MyApplication.getMyApplication().getMyPreferenceManager().readString(ApplictionUtils.USER_NAME_NET, "")))) {
                                return;
                            }
                            Session roomsession = new Session();
                            roomsession.setTo(room_to);
                            roomsession.setChatType(ChatUtils.GROUP_CHAT);
                            roomsession.setFrom(room_from);
                            roomsession.setNotReadCount("");//未读消息数量
                            roomsession.setTime(room_msgtime);
                            roomsession.setRoomJid(room_jid);
                            RoomChatMsg rcmmsg = null;
                            if (room_msgtype.equals(ChatUtils.FRIEND_ADD)) {//添加好友的请求
                            } else if (room_msgtype.equals(ChatUtils.FRIEND_ADD)) {//对方同意添加好友的请求
                            } else if (room_msgtype.equals(ChatUtils.MSG_TYPE_TEXT)) {//文本类型
                                rcmmsg = new RoomChatMsg();
                                rcmmsg.setRoomJid(room_jid);
                                rcmmsg.setFromUser(room_from);
                                rcmmsg.setContent(room_msgcontent);
                                rcmmsg.setDate(room_msgtime);
                                rcmmsg.setIsReaded("1");//暂且默认为已读
                                rcmmsg.setType(room_msgtype);
                                rcmmsg.setIsMySend(ChatUtils.IS_NO_SEND);
                                roomChatMsgDao.insert(rcmmsg);
                                roomsession.setType(ChatUtils.MSG_TYPE_TEXT);
                                roomsession.setContent(room_msgcontent);

                            } else if (room_msgtype.equals(ChatUtils.MSG_TYPE_IMG)) {
                                rcmmsg = new RoomChatMsg();
                                rcmmsg.setRoomJid(room_jid);
                                rcmmsg.setFromUser(room_from);
                                rcmmsg.setContent(room_msgcontent);
                                rcmmsg.setDate(room_msgtime);
                                rcmmsg.setIsReaded("1");//暂且默认为已读
                                rcmmsg.setType(room_msgtype);
                                rcmmsg.setIsMySend(ChatUtils.IS_NO_SEND);
                                String imgPath = CommonUtils.GenerateImage(room_msgcontent, System.currentTimeMillis() + "");
                                rcmmsg.setContentPath(imgPath);
                                //插入数据库，并保存ID
                                rcmmsg.setMsgId(roomChatMsgDao.insert(rcmmsg));
                                roomsession.setType(ChatUtils.MSG_TYPE_IMG);
                                roomsession.setContent("[图片]");

                            } else if (room_msgtype.equals(ChatUtils.MSG_TYPE_VOICE)) {//声音
                                rcmmsg = new RoomChatMsg();
                                rcmmsg.setRoomJid(room_jid);
                                rcmmsg.setFromUser(room_from);
                                rcmmsg.setContent(room_msgcontent);
                                rcmmsg.setDate(room_msgtime);
                                rcmmsg.setIsReaded("1");//暂且默认为已读
                                rcmmsg.setType(room_msgtype);
                                rcmmsg.setIsMySend(ChatUtils.IS_NO_SEND);
                                String imgPath = CommonUtils.GenerateVoic(room_msgcontent, System.currentTimeMillis() + "");
                                rcmmsg.setContentPath(imgPath);
                                //插入数据库，并保存ID
                                rcmmsg.setMsgId(roomChatMsgDao.insert(rcmmsg));
                                roomsession.setType(ChatUtils.MSG_TYPE_VOICE);
                                roomsession.setContent("[声音]");
                            }
                            if (sessionDao.isRoomContent(room_jid)) {//判断最近联系人列表是否已存在记录
                                sessionDao.upRoomDateSession(roomsession);
                            } else {
                                sessionDao.insertSession(roomsession);
                            }
                            //发送广播去聊天界面
                            Intent msgInten = new Intent();
                            msgInten.putExtra(ChatUtils.MSG_KEY, rcmmsg);
                            msgInten.setAction(ChatUtils.ROOM_MSG_FRIEN_RECVER);
                            sendBroadcast(msgInten);
                            Intent intent = new Intent();//发送广播，通知消息界面更新
                            intent.setAction(ApplictionUtils.FREND_UPDATA);
                            sendBroadcast(intent);
                            break;
                    }
            }
        }
    };

    private void sendNotifyMsg(String msg) {
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //2.初始化一个notification的对象
        long when = System.currentTimeMillis();
        // 创建一个Notification
        Notification notification = new Notification(R.mipmap.ic_launcher, "通知", when);
        // 设置发出通知的时间

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.view_notify_layou);
        rv.setTextViewText(R.id.tv_conent, msg);
        rv.setImageViewResource(R.id.iv_icon, R.mipmap.btn_setting_checked);
        Intent intent = new Intent(this, MainManagerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(XmppService.this, 0, intent, 0);
        notification.contentView = rv;
        notification.when = when;
        notification.contentIntent = contentIntent;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "退出");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
