package zj.chat.com.imchat.db;

/**
 * @author zj
 */
public class DBcolumns {


    /**
     * 聊天消息信息表
     */
    public static final String TABLE_MSG = "table_msg";
    public static final String MSG_ID = "msg_id";
    public static final String MSG_FROM = "msg_from";
    public static final String MSG_TO = "msg_to";
    public static final String MSG_TYPE = "msg_type";
    public static final String MSG_CONTENT = "msg_content";
    public static final String MSG_ISCOMING = "msg_iscoming";
    public static final String MSG_DATE = "msg_date";
    public static final String MSG_ISREADED = "msg_isreaded";
    public static final String MSG_CHAT_TYPE = "msg_chat_type";
    public static final String MSG_ROOM_JID = "msg_room_jid";
    public static final String MSG_CONTENT_PATH="msg_content_path";
    public static final String MSG_BAK1 = "msg_bak1";
    public static final String MSG_BAK2 = "msg_bak2";
    public static final String MSG_BAK3 = "msg_bak3";
    public static final String MSG_BAK4 = "msg_bak4";
    public static final String MSG_BAK5 = "msg_bak5";
    public static final String MSG_BAK6 = "msg_bak6";
    /**
     * 群聊消息表
     */

    public static final String ROOM_TAB_NAME = "room_tab_name";
    public static final String ROOM_MSG_ID = "room_id";
    public static final String ROOM_CHAT_JID = "room_chat_jid";
    public static final String ROOM_DISPATCHER = "room_dispatcher";
    public static final String ROOM_TYPE = "room_type";
    public static final String ROOM_CONTENT = "room_content";
    public static final String ROOM_IS_MY_SEND = "room_is_my_send";
    public static final String ROOM_IS_READ = "room_is_read";
    public static final String ROOM_CONTENT_PATH="room_content_path";
    public static final String ROOM_DATE = "room_date";
    public static final String ROOM_BAK1 = "room_bak1";
    public static final String ROOM_BAK2 = "room_bak2";
    public static final String ROOM_BAK3 = "room_bak3";
    public static final String ROOM_BAK4 = "room_bak4";
    public static final String ROOM_BAK5 = "room_bak5";
    public static final String ROOM_BAK6 = "room_bak6";

    /**
     * 聊天会话表
     */
    public static final String TABLE_SESSION = "table_session";
    public static final String SESSION_id = "session_id";
    public static final String SESSION_FROM = "session_from";
    public static final String SESSION_TYPE = "session_type";
    public static final String SESSION_TIME = "session_time";
    public static final String SESSION_CONTENT = "session_content";
    public static final String SESSION_TO = "session_to";// 登录人id
    public static final String SESSION_ISDISPOSE = "session_isdispose";
    public static final String CHAT_TYPE = "session_chat_type";
    public static final String ROOM_JID = "session_room_jid";

    /**
     * 好友消息通知表
     */
    public static final String TABLE_SYS_NOTICE = "table_sys_notice";
    public static final String SYS_NOTICE_ID = "sys_notice_id";
    public static final String SYS_NOTICE_TYPE = "sys_notice_type";
    public static final String SYS_NOTICE_FROM = "sys_notice_from";
    public static final String SYS_NOTICE_FROM_HEAD = "sys_notice_from_head";
    public static final String SYS_NOTICE_CONTENT = "sys_notice_content";
    public static final String SYS_NOTICE_ISDISPOSE = "sys_notice_isdispose";

}
