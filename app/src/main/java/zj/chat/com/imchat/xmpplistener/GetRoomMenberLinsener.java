package zj.chat.com.imchat.xmpplistener;

import java.util.ArrayList;
import java.util.List;

import zj.chat.com.imchat.cache.ben.RoomInfo;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 获取成员列表回调
 */
public interface GetRoomMenberLinsener {
    void onGetRoomMenberSucuss(ArrayList<RoomInfo> rs);

    void onGetRommMenberError();

}
