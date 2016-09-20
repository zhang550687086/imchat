package zj.chat.com.imchat.xmpplistener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 创建房间回调接口
 */
public interface CreateRommLinsener {
    void onCreateSuceed();

    void onCreateFailure(Exception e);
}
