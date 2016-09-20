package zj.chat.com.imchat.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zj.chat.com.imchat.ChatActivity;
import zj.chat.com.imchat.FrientAddActivity;
import zj.chat.com.imchat.R;
import zj.chat.com.imchat.cache.ben.FriendCliend;
import zj.chat.com.imchat.cache.ben.FriendGroup;
import zj.chat.com.imchat.common.BaseFragment;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.custom.view.CircleImageView;
import zj.chat.com.imchat.custom.view.TitleBarView;
import zj.chat.com.imchat.util.ChatUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 好友列表
 */
public class FrientFragment extends BaseFragment {
    private ExpandableListView ex_list = null;
    private List<FriendGroup> grupLists = new ArrayList<FriendGroup>();//组集合
    private View view = null;
    private TitleBarView mTitleBarView;
    private FriendAdapter mFriendAdapter = null;//数据列表适配器
    private FriendStateRecver mFriendStateRecver = null;//监听好友状态广播

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initFriendData() {//获取数据
        //异步去获取数据
        new AsyncTask<Void, Void, List<FriendGroup>>() {
            @Override
            protected List<FriendGroup> doInBackground(Void... params) {
                ArrayList<FriendGroup> groups = new ArrayList<FriendGroup>();
                Roster roster = XmppConnect.getXmppConnectInstance().getRoster();
                Collection<RosterGroup> entriesGroup = roster.getGroups();
                for (RosterGroup rg : entriesGroup) {
                    FriendGroup mFriendGroup = new FriendGroup();
                    mFriendGroup.setGroupName(rg.getName());
                    Collection<RosterEntry> entries = rg.getEntries();
                    List<FriendCliend> childList = new ArrayList<FriendCliend>();
                    childList.clear();
                    for (RosterEntry entry : entries) {
                        Presence presence = roster.getPresence(entry.getUser());
                        FriendCliend cl = new FriendCliend();
                        cl.setUser(entry.getUser());
                        cl.setName(entry.getName());
                        cl.setAvailable(presence.isAvailable());
                        childList.add(cl);
                        mFriendGroup.setCliends(childList);
                    }
                    groups.add(mFriendGroup);

                }
                return groups;
            }

            @Override
            protected void onPostExecute(List<FriendGroup> friendGroups) {
                super.onPostExecute(friendGroups);
                grupLists = friendGroups;
                initFrientAdapter();
            }
        }.execute();

    }

    private void initFrientAdapter() {
        if (grupLists == null) {
            return;
        }
        if (mFriendAdapter == null) {
            mFriendAdapter = new FriendAdapter();
            ex_list.setAdapter(mFriendAdapter);
        } else {
            mFriendAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResumePage() {

    }

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        return view;
    }

    @Override
    protected void initPageView() {
        ex_list = (ExpandableListView) view.findViewById(R.id.ex_list);
        mTitleBarView = (TitleBarView) view.findViewById(R.id.title_bar);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
        mTitleBarView.setTitleText(R.string.frien);
        mTitleBarView.setTitleRighe("添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FrientAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initPageViewListener() {
        ex_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userId", grupLists.get(groupPosition).getCliends().get(childPosition).getUser());
                mContext.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected View loadTopLayout() {

        return null;
    }

    @Override
    protected View loadContentLayout() {
        view = View.inflate(mContext, R.layout.fragment_friend_layout, null);
        return view;
    }

    @Override
    protected void process(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        initFriendData();
        initFrientAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatUtils.FRIEND_STATES_REC);
        mFriendStateRecver = new FriendStateRecver();

        getActivity().registerReceiver(mFriendStateRecver, intentFilter);
    }

    @Override
    protected void onMessageHandle(Object param1, Object param2, String respond) {

    }

    private class FriendAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return grupLists.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return grupLists.get(groupPosition).getCliends().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return grupLists.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return grupLists.get(groupPosition).getCliends().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.view_friend_group_layout, null);
                holder = new GroupHolder();
                holder.nameView = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }
            holder.nameView.setText(grupLists.get(groupPosition).getGroupName());//添加数据
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder = null;
            if (convertView == null) {
                convertView = view = View.inflate(mContext, R.layout.view_friend_child_layout, null);
                holder = new ChildHolder();
                holder.nameView = (TextView) view.findViewById(R.id.tv_name);
                holder.feelView = (TextView) view.findViewById(R.id.tv_states);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            FriendCliend friendCliend = grupLists.get(groupPosition).getCliends().get(childPosition);
            holder.nameView.setText(friendCliend.getName());//添加数据
            if (friendCliend.isAvailable()) {
                holder.feelView.setText("在线");
            } else {
                holder.feelView.setText("离线");
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class GroupHolder {
            TextView nameView;
            TextView onLineView;
            ImageView iconView;
        }

        class ChildHolder {
            TextView nameView;
            TextView feelView;
            CircleImageView iconView;
        }
    }

    /**
     * 监听好友状态广播
     */
    private class FriendStateRecver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                FriendCliend fcs = (FriendCliend) intent.getSerializableExtra(ChatUtils.FRIEND_STATES);
                int i = 0, j = 0;
                boolean isZD = false;//是否已经找到位置
                for (FriendGroup gr : grupLists) {//找到状态改变好友的位置
                    for (FriendCliend fc : gr.getCliends()) {
                        if (fcs.getUser().equals(fc.getUser())) {//如果这个用户跟上线下用户是同一人的时候

                            isZD = true;
                            break;
                        }
                        j++;
                    }
                    if (isZD) {
                        break;
                    }
                    j = 0;
                    i++;
                }
                if (isZD) {//只有查询到的时候才去更新
                    grupLists.get(i).getCliends().get(j).setAvailable(fcs.isAvailable());
                    initFrientAdapter();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFriendStateRecver != null) {
            getActivity().unregisterReceiver(mFriendStateRecver);
        }
    }
}
