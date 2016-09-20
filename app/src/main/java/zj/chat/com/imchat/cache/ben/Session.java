package zj.chat.com.imchat.cache.ben;

import java.io.Serializable;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class Session implements Serializable {
    private String id;
    private String from;        //发送人
    private String type;        //消息类型
    private String time;        //接收时间
    private String content;        //发送内容
    private String notReadCount;//未读记录
    private String to;        //接收人
    private String isdispose;//是否已处理 0未处理，1已处理
    private String chatType;//聊天类型  0单人聊天  1 群聊
    private String roomJid;//房间ID
    public String getRoomJid() {
        return roomJid;
    }

    public void setRoomJid(String roomJid) {
        this.roomJid = roomJid;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id:" + id);
        sb.append("_");
        sb.append("from:" + from);
        sb.append("_");
        sb.append("type:" + type);
        sb.append("_");
        sb.append("time:" + time);
        sb.append("_");
        sb.append("content:" + content);
        sb.append("_");
        sb.append("notReadCount:" + notReadCount);
        sb.append("_");
        sb.append("to:" + to);
        sb.append("_");
        sb.append("isdispose:" + isdispose);
        sb.append("_");
        sb.append("chatType:" + chatType);
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(String notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getIsdispose() {
        return isdispose;
    }

    public void setIsdispose(String isdispose) {
        this.isdispose = isdispose;
    }


}
