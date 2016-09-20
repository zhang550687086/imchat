package zj.chat.com.imchat.util;

import zj.chat.com.imchat.cache.ben.ChatMsg;
import zj.chat.com.imchat.cache.ben.RoomChatMsg;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 聊天常量保存
 */
public class ChatUtils {
    //聊天内容标志
    public static final String MSG_SPLIT = "卍";
    public static final String FRIEND_ADD = "friend_add";//添加好友
    public static final String MSG_LOCAL = "msg_local";//位置
    public static final String MSG_TYPE_TEXT = "msg_text";//文本
    public static final String MSG_TYPE_IMG = "msg_img";//图片
    public static final String MSG_TYPE_VOICE = "msg_voice";//声音
    public static final int SEND_FROM = 0x00;//发送消息
    public static final int SEDN_RECEIVE = 0x01;//接收消息
    public static final String IS_MY_SEND = "0";//自己发送的消息
    public static final String IS_NO_SEND = "1";//不是自己发送的消息

    public static final String GROUP_CHAT = "group_chat";//群聊
    public static final String D_CHAT = "chat";//单聊
    //聊天消息广播
    public static final String MSG_FRIEN_RECVER = "zj.chat.com.imchat.msg.rec";
    //群聊天消息广播
    public static final String ROOM_MSG_FRIEN_RECVER = "zj.chat.com.imchat.room.msg.rec";
    //接受消息KEY
    public static final String MSG_KEY = "msg_key";
    public static final String FRIEND_STATES_REC = "zj.chat.com.imchat.msg.states";
    //状态消息key
    public static final String FRIEND_STATES = "friend_states";
    //默认获取的房间列表
    public static final String DEFAULT_ROOM = "conference.www.myzhangjun.com";
    //进入群聊列表
    public static final String ROOM_ID_KEY = "room_id_key";
    public static final String ROOM_NAME_KEY = "room_name_key";

    /**
     * 根据USERID获取名称
     *
     * @param str
     * @return
     */
    public static String getUserName(String str) {
        if (StringUtils.stringIsEmp(str)) {//如果是无效字符
            return "";
        }
        if (str.contains("@")) {
            String[] split = str.split("@");
            return split[0];
        } else {
            return str;
        }
    }

    /**
     * 去掉/后面的
     *
     * @param str
     * @return
     */
    public static String deletcD(String str) {
        if (StringUtils.stringIsEmp(str)) {
            return "";
        }
        if (str.contains("/")) {
            String[] st = str.split("/");
            return st[0];
        } else {
            return str;
        }

    }

    public static String appenSendMsg(ChatMsg chatMsg) {
        StringBuffer sb = new StringBuffer();
        //这里为了聊天记录缓存，所以fromUser其实是发送者
        sb.append(chatMsg.getToUser());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getFromUser());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getType());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getContent());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getDate());
        return sb.toString();
    }

    public static String appenSendMsg(RoomChatMsg chatMsg) {
        StringBuffer sb = new StringBuffer();
        sb.append(chatMsg.getRoomJid());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getFromUser());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getType());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getContent());
        sb.append(ChatUtils.MSG_SPLIT);
        sb.append(chatMsg.getDate());
        return sb.toString();
    }

}
