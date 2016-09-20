package zj.chat.com.imchat.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import zj.chat.com.imchat.cache.ben.RoomChatMsg;

/**
 * 群聊天记录操作类
 */

public class RoomChatMsgDao {
    private DBHelper helper;

    public RoomChatMsgDao(Context context) {
        helper = new DBHelper(context);
    }

    public RoomChatMsgDao(Context context, int version) {
        helper = new DBHelper(context, version);
    }

    /**
     * 添加新信息
     *
     * @param msg
     */
    public int insert(RoomChatMsg msg) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBcolumns.ROOM_DISPATCHER, msg.getFromUser());
        values.put(DBcolumns.ROOM_TYPE, msg.getType());
        values.put(DBcolumns.ROOM_CONTENT, msg.getContent());
        values.put(DBcolumns.ROOM_DATE, msg.getDate());
        values.put(DBcolumns.ROOM_CHAT_JID, msg.getRoomJid());
        values.put(DBcolumns.ROOM_IS_MY_SEND, msg.getIsMySend());
        values.put(DBcolumns.ROOM_IS_READ, msg.getIsReaded());
        values.put(DBcolumns.ROOM_CONTENT_PATH, msg.getContentPath());
        values.put(DBcolumns.ROOM_BAK1, msg.getBak1());
        values.put(DBcolumns.ROOM_BAK2, msg.getBak2());
        values.put(DBcolumns.ROOM_BAK3, msg.getBak3());
        values.put(DBcolumns.ROOM_BAK4, msg.getBak4());
        values.put(DBcolumns.ROOM_BAK5, msg.getBak5());
        values.put(DBcolumns.ROOM_BAK6, msg.getBak6());
        db.insert(DBcolumns.ROOM_TAB_NAME, null, values);
        db.close();
        int msgid = queryTheLastMsgId();//返回新插入记录的id
        return msgid;
    }


    /**
     * 清空所有聊天记录
     */
    public void deleteTableData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBcolumns.ROOM_TAB_NAME, null, null);
        db.close();
    }


    /**
     * 根据msgid，删除对应聊天记录
     *
     * @return
     */
    public long deleteMsgById(int msgid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(DBcolumns.ROOM_TAB_NAME, DBcolumns.ROOM_MSG_ID + " = ?", new String[]{"" + msgid});
        db.close();
        return row;
    }

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<RoomChatMsg> queryMsg(String roomJid, int offset) {
        ArrayList<RoomChatMsg> list = new ArrayList<RoomChatMsg>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.ROOM_TAB_NAME + " where " + DBcolumns.ROOM_CHAT_JID + "=? " + "order by " + DBcolumns.ROOM_MSG_ID + " desc limit ?,?";
        String[] args = new String[]{roomJid, String.valueOf(offset), "15"};
        Cursor cursor = db.rawQuery(sql, args);
        RoomChatMsg msg = null;
        while (cursor.moveToNext()) {
            msg = new RoomChatMsg();
            msg.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.ROOM_MSG_ID)));
            msg.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_DISPATCHER)));
            msg.setType(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_TYPE)));
            msg.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CONTENT)));
            msg.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_DATE)));
            msg.setIsMySend(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_IS_MY_SEND)));
            msg.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_IS_READ)));
            msg.setRoomJid(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CHAT_JID)));
            msg.setContentPath(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CONTENT_PATH)));
            msg.setBak1(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK1)));
            msg.setBak2(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK2)));
            msg.setBak3(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK3)));
            msg.setBak4(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK4)));
            msg.setBak5(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK5)));
            msg.setBak6(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK6)));
            list.add(0, msg);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 查询最新一条记录
     *
     * @return
     */
    public RoomChatMsg queryTheLastMsg() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.ROOM_TAB_NAME + " order by " + DBcolumns.ROOM_MSG_ID + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);

        RoomChatMsg msg = null;
        while (cursor.moveToNext()) {
            msg = new RoomChatMsg();
            msg.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.ROOM_MSG_ID)));
            msg.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_DISPATCHER)));
            msg.setType(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_TYPE)));
            msg.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CONTENT)));
            msg.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_DATE)));
            msg.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_IS_READ)));
            msg.setRoomJid(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CHAT_JID)));
            msg.setContentPath(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_CONTENT_PATH)));
            msg.setBak1(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK1)));
            msg.setBak2(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK2)));
            msg.setBak3(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK3)));
            msg.setBak4(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK4)));
            msg.setBak5(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK5)));
            msg.setBak6(cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_BAK6)));
        }
        cursor.close();
        db.close();
        return msg;
    }

    /**
     * 查询最新一条记录的id
     *
     * @return
     */
    public int queryTheLastMsgId() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select " + DBcolumns.ROOM_MSG_ID + " from " + DBcolumns.ROOM_TAB_NAME + " order by " + DBcolumns.ROOM_MSG_ID + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);
        int id = -1;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(DBcolumns.ROOM_MSG_ID));
        }
        cursor.close();
        db.close();
        return id;
    }

}
