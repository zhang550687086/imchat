package zj.chat.com.imchat.cache.ben;

import java.util.List;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 好友列表
 */
public class FriendGroup {
    private String groupName;
    private List<FriendCliend> cliends;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCliends(List<FriendCliend> cliends) {
        this.cliends = cliends;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<FriendCliend> getCliends() {
        return cliends;
    }
}
