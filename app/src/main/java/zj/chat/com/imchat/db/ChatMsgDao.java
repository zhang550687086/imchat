package zj.chat.com.imchat.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import zj.chat.com.imchat.cache.ben.ChatMsg;

/**
 * 聊天记录操作类
 */

public class ChatMsgDao {
    private DBHelper helper;

    public ChatMsgDao(Context context) {
        helper = new DBHelper(context);
    }

    public ChatMsgDao(Context context, int version) {
        helper = new DBHelper(context, version);
    }

    /**
     * 添加新信息
     *
     * @param msg
     */
    public int insert(ChatMsg msg) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBcolumns.MSG_FROM, msg.getFromUser());
        values.put(DBcolumns.MSG_TO, msg.getToUser());
        values.put(DBcolumns.MSG_TYPE, msg.getType());
        values.put(DBcolumns.MSG_CONTENT, msg.getContent());
        values.put(DBcolumns.MSG_ISCOMING, msg.getIsComing());
        values.put(DBcolumns.MSG_DATE, msg.getDate());
        values.put(DBcolumns.MSG_ISREADED, msg.getIsReaded());
        values.put(DBcolumns.MSG_CHAT_TYPE, msg.getChatType());
        values.put(DBcolumns.MSG_ROOM_JID, msg.getRoomJid());
        values.put(DBcolumns.MSG_CONTENT_PATH, msg.getContentPath());
        values.put(DBcolumns.MSG_BAK1, msg.getBak1());
        values.put(DBcolumns.MSG_BAK2, msg.getBak2());
        values.put(DBcolumns.MSG_BAK3, msg.getBak3());
        values.put(DBcolumns.MSG_BAK4, msg.getBak4());
        values.put(DBcolumns.MSG_BAK5, msg.getBak5());
        values.put(DBcolumns.MSG_BAK6, msg.getBak6());
        db.insert(DBcolumns.TABLE_MSG, null, values);
        db.close();
        int msgid = queryTheLastMsgId();//返回新插入记录的id
        return msgid;
    }


    /**
     * 清空所有聊天记录
     */
    public void deleteTableData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBcolumns.TABLE_MSG, null, null);
        db.close();
    }


    /**
     * 根据msgid，删除对应聊天记录
     *
     * @return
     */
    public long deleteMsgById(int msgid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(DBcolumns.TABLE_MSG, DBcolumns.MSG_ID + " = ?", new String[]{"" + msgid});
        db.close();
        return row;
    }

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<ChatMsg> queryMsg(String from, String to, int offset) {
        ArrayList<ChatMsg> list = new ArrayList<ChatMsg>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.TABLE_MSG + " where " + DBcolumns.MSG_FROM + "=? and " + DBcolumns.MSG_TO + "=? order by " + DBcolumns.MSG_ID + " desc limit ?,?";
        String[] args = new String[]{from, to, String.valueOf(offset), "15"};
        Cursor cursor = db.rawQuery(sql, args);
        ChatMsg msg = null;
        while (cursor.moveToNext()) {
            msg = new ChatMsg();
            msg.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID)));
            msg.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_FROM)));
            msg.setToUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TO)));
            msg.setType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TYPE)));
            msg.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT)));
            msg.setIsComing(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ISCOMING)));
            msg.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_DATE)));
            msg.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ISREADED)));
            msg.setChatType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CHAT_TYPE)));
            msg.setRoomJid(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ROOM_JID)));
            msg.setContentPath(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT_PATH)));
            msg.setBak1(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK1)));
            msg.setBak2(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK2)));
            msg.setBak3(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK3)));
            msg.setBak4(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK4)));
            msg.setBak5(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK5)));
            msg.setBak6(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK6)));
            list.add(0, msg);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<ChatMsg> queryRoomMsg(String roomJid, int offset) {
        ArrayList<ChatMsg> list = new ArrayList<ChatMsg>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.TABLE_MSG + " where " + DBcolumns.MSG_ROOM_JID + "=? order by " + DBcolumns.MSG_ID + " desc limit ?,?";
        String[] args = new String[]{roomJid, String.valueOf(offset), "15"};
        Cursor cursor = db.rawQuery(sql, args);
        ChatMsg msg = null;
        while (cursor.moveToNext()) {
            msg = new ChatMsg();
            msg.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID)));
            msg.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_FROM)));
            msg.setToUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TO)));
            msg.setType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TYPE)));
            msg.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT)));
            msg.setIsComing(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ISCOMING)));
            msg.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_DATE)));
            msg.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ISREADED)));
            msg.setChatType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CHAT_TYPE)));
            msg.setRoomJid(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ROOM_JID)));
            msg.setContentPath(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT_PATH)));
            msg.setBak1(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK1)));
            msg.setBak2(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK2)));
            msg.setBak3(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK3)));
            msg.setBak4(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK4)));
            msg.setBak5(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK5)));
            msg.setBak6(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK6)));
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
    public ChatMsg queryTheLastMsg() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);

        ChatMsg msg = null;
        while (cursor.moveToNext()) {
            msg = new ChatMsg();
            msg.setMsgId(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID)));
            msg.setFromUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_FROM)));
            msg.setToUser(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TO)));
            msg.setType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_TYPE)));
            msg.setContent(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CONTENT)));
            msg.setIsComing(cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ISCOMING)));
            msg.setDate(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_DATE)));
            msg.setIsReaded(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ISREADED)));
            msg.setChatType(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_CHAT_TYPE)));
            msg.setRoomJid(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ROOM_JID)));
            msg.setBak1(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK1)));
            msg.setBak2(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK2)));
            msg.setBak3(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK3)));
            msg.setBak4(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK4)));
            msg.setBak5(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK5)));
            msg.setBak6(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_BAK6)));
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
        String sql = "select " + DBcolumns.MSG_ID + " from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);
        int id = -1;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID));
        }
        cursor.close();
        db.close();
        return id;
    }

}
