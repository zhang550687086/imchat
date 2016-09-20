package zj.chat.com.imchat.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import zj.chat.com.imchat.cache.ben.Session;


/**
 * 聊天回话列表的管理
 *
 * @author Administrator
 */
public class SessionDao {
    private SQLiteDatabase db;

    public SessionDao(Context context) {
        db = DBHelper.getDBHelperInstance(context).getWritableDatabase();
    }

    public SessionDao(Context context, int version) {
        db = DBHelper.getDBHelperInstance(context).getWritableDatabase();
    }

    // 判断是否包含
    public boolean isContent(String belong, String userid) {

        Cursor cursor = db.query(DBcolumns.TABLE_SESSION, new String[]{"*"}, DBcolumns.SESSION_FROM + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{belong, userid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    // 判断是否包含
    public boolean isRoomContent(String roomjid) {
        Cursor cursor = db.query(DBcolumns.TABLE_SESSION, new String[]{"*"}, DBcolumns.ROOM_JID + " = ? " + " ", new String[]{roomjid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    // 添加一个会话
    public long insertSession(Session session) {
        if (session.getTo().equals(session.getFrom())) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_FROM, session.getFrom());
        values.put(DBcolumns.SESSION_TIME, session.getTime());
        values.put(DBcolumns.SESSION_CONTENT, session.getContent());
        values.put(DBcolumns.SESSION_TO, session.getTo());
        values.put(DBcolumns.SESSION_TYPE, session.getType());
        values.put(DBcolumns.SESSION_ISDISPOSE, session.getIsdispose());
        values.put(DBcolumns.CHAT_TYPE, session.getChatType());
        values.put(DBcolumns.ROOM_JID, session.getRoomJid());
        long row = db.insert(DBcolumns.TABLE_SESSION, null, values);
        return row;
    }

    // 返回全部列表
    public List<Session> queryAllSessions(String user_id) {
        List<Session> list = new ArrayList<Session>();
        Cursor cursor = db.query(DBcolumns.TABLE_SESSION, new String[]{"*"}, DBcolumns.SESSION_TO + " = ? order by session_time desc", new String[]{user_id}, null, null, null);
        Session session = null;
        while (cursor.moveToNext()) {
            session = new Session();
            String id = "" + cursor.getInt(cursor.getColumnIndex(DBcolumns.SESSION_id));
            String from = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_FROM));
            String time = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TIME));
            String content = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_CONTENT));
            String type = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TYPE));
            String to = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TO));
            String isdispose = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_ISDISPOSE));
            String chattype = cursor.getString(cursor.getColumnIndex(DBcolumns.CHAT_TYPE));
            String roomJid = cursor.getString(cursor.getColumnIndex(DBcolumns.ROOM_JID));
            int unreadCount = 0;
            Cursor countcursor = db.rawQuery("select count(*) from " + DBcolumns.TABLE_MSG + " where " + DBcolumns.MSG_FROM + " = ? and " + DBcolumns.MSG_ISREADED + " = 0" + " AND " + DBcolumns.MSG_TO + " = ?", new String[]{from, user_id});
            if (countcursor.moveToFirst()) {
                unreadCount = countcursor.getInt(0);
            }
            countcursor.close();
            session.setId(id);
            session.setNotReadCount("" + unreadCount);
            session.setFrom(from);
            session.setTime(time);
            session.setContent(content);
            session.setTo(to);
            session.setType(type);
            session.setIsdispose(isdispose);
            session.setChatType(chattype);
            session.setRoomJid(roomJid);
            list.add(session);
        }
        return list;
    }

    // 修改一个回话
    public long updateSession(Session session) {
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_TYPE, session.getType());
        values.put(DBcolumns.SESSION_TIME, session.getTime());
        values.put(DBcolumns.SESSION_CONTENT, session.getContent());
        values.put(DBcolumns.ROOM_JID, session.getRoomJid());
        long row = db.update(DBcolumns.TABLE_SESSION, values, DBcolumns.SESSION_FROM + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{session.getFrom(), session.getTo()});
        return row;
    }

    /**
     * 群聊天更新session
     *
     * @param session
     * @return
     */
    public long upRoomDateSession(Session session) {
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_TYPE, session.getType());
        values.put(DBcolumns.SESSION_TIME, session.getTime());
        values.put(DBcolumns.SESSION_CONTENT, session.getContent());
        values.put(DBcolumns.ROOM_JID, session.getRoomJid());
        long row = db.update(DBcolumns.TABLE_SESSION, values, DBcolumns.ROOM_JID + " = ? ", new String[]{session.getRoomJid()});
        return row;
    }

    public void updateSessionToDisPose(String sessionid) {
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_ISDISPOSE, "1");
        db.update(DBcolumns.TABLE_SESSION, values, DBcolumns.SESSION_id + " = ? ", new String[]{sessionid});
    }


    // 删除一个回话
    public long deleteSession(Session session) {
        long row = db.delete(DBcolumns.TABLE_SESSION, DBcolumns.SESSION_FROM + "=? and " + DBcolumns.SESSION_TO + "=?", new String[]{session.getFrom(), session.getTo()});
        return row;
    }

}
