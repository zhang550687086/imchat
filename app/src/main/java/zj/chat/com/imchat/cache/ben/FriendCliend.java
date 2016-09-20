package zj.chat.com.imchat.cache.ben;

import java.io.Serializable;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class FriendCliend implements Serializable{
    private String name;//用户名称
    private String user;//用户账号

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean isAvailable;//是否在线

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


}
