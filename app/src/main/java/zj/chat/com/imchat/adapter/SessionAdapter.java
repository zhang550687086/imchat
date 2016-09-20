package zj.chat.com.imchat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.cache.ben.Session;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.custom.view.CircleImageView;
import zj.chat.com.imchat.util.ChatUtils;
import zj.chat.com.imchat.util.ExpressionUtil;

public class SessionAdapter extends BaseAdapter {
    private Context mContext;
    private List<Session> lists;

    public SessionAdapter(Context context, List<Session> lists) {
        this.mContext = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        if (lists != null) {
            return lists.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.fragment_news_item, null);
            holder = new Holder();
            holder.iv = (CircleImageView) convertView.findViewById(R.id.user_head);
            holder.tv_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tips);
            holder.tv_content = (TextView) convertView.findViewById(R.id.content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Session session = lists.get(position);
        if (session.getType().equals(XmppUtils.MSG_TYPE_ADD_FRIEND)) {
            holder.tv_tips.setVisibility(View.VISIBLE);
            holder.iv.setImageResource(R.mipmap.ibl);
        } else {
            holder.tv_tips.setVisibility(View.GONE);
            holder.iv.setImageResource(R.mipmap.ic_launcher);
        }
        if (session.getChatType().equals(ChatUtils.D_CHAT)) {//如果是单人聊天就显示发送者的名称，否则显示房间名称
            holder.tv_name.setText(session.getFrom());
        } else {
            holder.tv_name.setText(session.getRoomJid());
        }


        holder.tv_content.setText(ExpressionUtil.prase(mContext, holder.tv_content, session.getContent() == null ? "" : session.getContent()));
        holder.tv_time.setText(session.getTime());
        return convertView;
    }

    class Holder {
        CircleImageView iv;
        TextView tv_name, tv_tips;
        TextView tv_content;
        TextView tv_time;
    }

}
