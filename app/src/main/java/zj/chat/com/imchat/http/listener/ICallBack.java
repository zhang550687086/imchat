package zj.chat.com.imchat.http.listener;

public interface ICallBack {
    /**
     * 获取数据成功
     */
    void onSuccess(String response);

    /**
     * 获取数据失败调接口
     */
    void onFailed(String error);
}
