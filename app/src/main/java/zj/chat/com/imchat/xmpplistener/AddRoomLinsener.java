package zj.chat.com.imchat.xmpplistener;

import java.util.ArrayList;

import zj.chat.com.imchat.cache.ben.RoomInfo;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 加入房間監聽
 */
public interface AddRoomLinsener {
    void onAddRoomSucess();

    void onAddRoomError(Exception e);
}
