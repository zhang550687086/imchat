package zj.chat.com.imchat.cache.ben;

import java.io.Serializable;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 群聊消息
 */
public class RoomChatMsg implements Serializable {
    private int msgId;//id
    private String roomJid;//群聊天ID
    private String fromUser;//发送者
    private String type;//信息类型
    private String content;//信息内容
    private String date;//时间
    private String isReaded;//是否已读
    private String isMySend;//0自己发送  1别人发送
    private String contentPath;//路径。图片或者声音路径

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public void setRoomJid(String roomJid) {
        this.roomJid = roomJid;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public void setIsMySend(String isMySend) {
        this.isMySend = isMySend;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getRoomJid() {
        return roomJid;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }


    public String getDate() {
        return date;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public String getIsMySend() {
        return isMySend;
    }

    private String bak1;//扩展1
    private String bak2;//扩展2
    private String bak3;//扩展3
    private String bak4;//扩展4
    private String bak5;//扩展5
    private String bak6;//扩展6

    public String getBak1() {
        return bak1;
    }

    public String getBak2() {
        return bak2;
    }

    public String getBak3() {
        return bak3;
    }

    public String getBak4() {
        return bak4;
    }

    public String getBak5() {
        return bak5;
    }

    public String getBak6() {
        return bak6;
    }

    public void setBak1(String bak1) {
        this.bak1 = bak1;
    }

    public void setBak2(String bak2) {
        this.bak2 = bak2;
    }

    public void setBak3(String bak3) {
        this.bak3 = bak3;
    }

    public void setBak4(String bak4) {
        this.bak4 = bak4;
    }

    public void setBak5(String bak5) {
        this.bak5 = bak5;
    }

    public void setBak6(String bak6) {
        this.bak6 = bak6;
    }
}
