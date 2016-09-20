package zj.chat.com.imchat.common;

import android.app.Application;
import android.content.Context;

import zj.chat.com.imchat.bin.UserInfo;
import zj.chat.com.imchat.util.MyPreferenceManager;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class MyApplication extends Application {

    private static MyApplication mMyApplication = null;
    private UserInfo mUserInfo = null;
    //共享首选项
    public MyPreferenceManager sharePreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharePreferences = MyPreferenceManager.getInstance(getApplicationContext());
        mMyApplication = this;
    }

    /**
     * 获取共享首选项
     *
     * @return
     */
    public MyPreferenceManager getMyPreferenceManager() {
        return sharePreferences;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public UserInfo getCurrUserInfo() {
        return mUserInfo;
    }

    /**
     * 设置当前用户
     *
     * @param mUserInfo
     */
    public void setCurrUserInfo(UserInfo mUserInfo) {
        this.mUserInfo = mUserInfo;
    }

    public Context getApplication() {
        return getApplicationContext();
    }

    /**
     * 返回一个MyApplication实例
     *
     * @return
     */
    public static MyApplication getMyApplication() {
        return mMyApplication;
    }

}
