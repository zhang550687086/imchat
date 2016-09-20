package zj.chat.com.imchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "chat_by";//数据库名称
    private static final int DB_VERSION = 1;//版本ID
    private static DBHelper mDBHelper;

    public DBHelper(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context mContext, int version) {
        super(mContext, DB_NAME, null, version);
    }

    /**
     * 获取实例
     *
     * @param mContext
     * @return
     */
    public synchronized static DBHelper getDBHelperInstance(Context mContext) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(mContext);
        }

        return mDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * 聊天记录
         */
        String sql_msg = "Create table IF NOT EXISTS " + DBcolumns.TABLE_MSG
                + "(" + DBcolumns.MSG_ID + " integer primary key autoincrement,"
                + DBcolumns.MSG_FROM + " text,"
                + DBcolumns.MSG_TO + " text,"
                + DBcolumns.MSG_TYPE + " text,"
                + DBcolumns.MSG_CONTENT + " text,"
                + DBcolumns.MSG_ISCOMING + " integer,"
                + DBcolumns.MSG_DATE + " text,"
                + DBcolumns.MSG_ISREADED + " text,"
                + DBcolumns.MSG_CHAT_TYPE + " text,"
                + DBcolumns.MSG_ROOM_JID + " text,"
                + DBcolumns.MSG_CONTENT_PATH + " text,"
                + DBcolumns.MSG_BAK1 + " text,"
                + DBcolumns.MSG_BAK2 + " text,"
                + DBcolumns.MSG_BAK3 + " text,"
                + DBcolumns.MSG_BAK4 + " text,"
                + DBcolumns.MSG_BAK5 + " text,"
                + DBcolumns.MSG_BAK6 + " text);";
        /**
         * 群聊天记录
         */
        String sql_room_msg = "Create table IF NOT EXISTS " + DBcolumns.ROOM_TAB_NAME
                + "(" + DBcolumns.ROOM_MSG_ID + " integer primary key autoincrement,"
                + DBcolumns.ROOM_CHAT_JID + " text,"
                + DBcolumns.ROOM_DISPATCHER + " text,"
                + DBcolumns.ROOM_TYPE + " text,"
                + DBcolumns.ROOM_CONTENT + " text,"
                + DBcolumns.ROOM_DATE + " text,"
                + DBcolumns.ROOM_IS_MY_SEND + " text,"
                + DBcolumns.ROOM_IS_READ + " text,"
                + DBcolumns.ROOM_CONTENT_PATH + " text,"
                + DBcolumns.ROOM_BAK1 + " text,"
                + DBcolumns.ROOM_BAK2 + " text,"
                + DBcolumns.ROOM_BAK3 + " text,"
                + DBcolumns.ROOM_BAK4 + " text,"
                + DBcolumns.ROOM_BAK5 + " text,"
                + DBcolumns.ROOM_BAK6 + " text);";

        /**
         * 最近联系人
         */
        String sql_session = "Create table IF NOT EXISTS "
                + DBcolumns.TABLE_SESSION + "(" + DBcolumns.SESSION_id + " integer primary key AUTOINCREMENT,"
                + DBcolumns.SESSION_FROM + " text,"
                + DBcolumns.SESSION_TYPE + " text,"
                + DBcolumns.SESSION_TIME + " text,"
                + DBcolumns.SESSION_TO + " text,"
                + DBcolumns.SESSION_CONTENT + " text,"
                + DBcolumns.SESSION_ISDISPOSE + " text,"
                + DBcolumns.CHAT_TYPE + " text,"
                + DBcolumns.ROOM_JID + " text);";

        String sql_notice = "Create table IF NOT EXISTS "
                + DBcolumns.TABLE_SYS_NOTICE + "(" + DBcolumns.SYS_NOTICE_ID + " integer primary key AUTOINCREMENT,"
                + DBcolumns.SYS_NOTICE_TYPE + " text,"
                + DBcolumns.SYS_NOTICE_FROM + " text,"
                + DBcolumns.SYS_NOTICE_FROM_HEAD + " text,"
                + DBcolumns.SYS_NOTICE_CONTENT + " text,"
                + DBcolumns.SYS_NOTICE_ISDISPOSE + " text);";


        db.execSQL(sql_msg);
        db.execSQL(sql_session);
        db.execSQL(sql_notice);
        db.execSQL(sql_room_msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
