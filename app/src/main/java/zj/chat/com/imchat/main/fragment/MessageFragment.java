package zj.chat.com.imchat.main.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import zj.chat.com.imchat.ChatActivity;
import zj.chat.com.imchat.R;
import zj.chat.com.imchat.adapter.SessionAdapter;
import zj.chat.com.imchat.cache.ben.Session;
import zj.chat.com.imchat.common.BaseFragment;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.custom.view.CustomListView;
import zj.chat.com.imchat.custom.view.TitleBarView;
import zj.chat.com.imchat.db.SessionDao;
import zj.chat.com.imchat.dialog.ToastUtils;
import zj.chat.com.imchat.room.RoomChatActivity;
import zj.chat.com.imchat.util.ApplictionUtils;
import zj.chat.com.imchat.util.ChatUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 聊天列表
 */
public class MessageFragment extends BaseFragment implements CustomListView.OnRefreshListener {
    private View view = null;

    private CustomListView mCustomListView;
    private SessionAdapter adapter;
    private List<Session> sessionList = new ArrayList<Session>();
    private TitleBarView mTitleBarView;
    private SessionDao sessionDao;
    private String userid;
    private MyUIUpBroadcastReceiver mMyUIUpBroadcastReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ApplictionUtils.FREND_UPDATA);
        mMyUIUpBroadcastReceiver = new MyUIUpBroadcastReceiver();
        getActivity().registerReceiver(mMyUIUpBroadcastReceiver, intentFilter);
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
    }

    @Override
    protected void initPageViewListener() {
    }

    @Override
    protected View loadTopLayout() {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMyUIUpBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mMyUIUpBroadcastReceiver);
        }
    }

    @Override
    protected View loadContentLayout() {
        view = View.inflate(getActivity(), R.layout.fragment_message_layout, null);
        return view;
    }

    private void findView() {
        mTitleBarView = (TitleBarView) view.findViewById(R.id.title_bar);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.GONE);
        mTitleBarView.setTitleText(R.string.msgs);
        mCustomListView = (CustomListView) view.findViewById(R.id.lv_news);//listview
        mCustomListView.setOnRefreshListener(this);//设置listview下拉刷新监听
        mCustomListView.setCanLoadMore(false);//设置禁止加载更多
        mCustomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final Session session = sessionList.get(arg2 - 1);
                if (session.getType().equals(XmppUtils.MSG_TYPE_ADD_FRIEND)) {
                    if (!TextUtils.isEmpty(session.getIsdispose())) {
                        if (!session.getIsdispose().equals("1")) {
                            AlertDialog.Builder bd = new AlertDialog.Builder(mContext);
                            bd.setItems(new String[]{"同意"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Roster roster = XmppConnect.getXmppConnectInstance().getRoster();
                                    XmppUtils.addGroup(roster, "我的好友");//先默认创建一个分组
                                    if (XmppUtils.addUsers(roster, session.getFrom() + "@" + XmppConnect.getXmppConnectInstance().getServiceName(), session.getFrom(), "我的好友")) {
                                        //告知对方，同意添加其为好友
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    //注意消息的协议格式 =》接收者卍发送者卍消息类型卍消息内容卍发送时间
                                                    String message = session.getFrom() + userid;
                                                    XmppUtils.sendMessage(XmppConnect.getXmppConnectInstance(), message, session.getFrom());
                                                } catch (XMPPException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        sessionDao.updateSessionToDisPose(session.getId());//将本条数据在数据库中改为已处理
//										ToastUtil.showShortToast(mContext, "你们已经是好友了，快去聊天吧！");
                                        sessionList.remove(session);
                                        session.setIsdispose("1");
                                        sessionList.add(0, session);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        ToastUtils.show(mContext, "添加好友失败");
                                    }
                                }
                            });
                            bd.create().show();
                        } else {
                            ToastUtils.show(mContext, "已同意");
                        }
                    }
                } else {
                    if (session.getChatType().equals(ChatUtils.D_CHAT)) {//如果是单独聊天
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("userId", session.getFrom());
                        startActivity(intent);
                    } else {//去群聊天室
                        Intent intent = new Intent(mContext, RoomChatActivity.class);
                        intent.putExtra(ChatUtils.ROOM_ID_KEY, session.getRoomJid());
                        intent.putExtra(ChatUtils.ROOM_NAME_KEY, ChatUtils.getUserName(session.getRoomJid()));
                        startActivity(intent);
                    }

                }
            }
        });
    }

    private void initDataS() {
        //注意，当数据量较多时，此处要开线程了，否则阻塞主线程

        sessionList = sessionDao.queryAllSessions(userid);
        adapter = new SessionAdapter(mContext, sessionList);
        mCustomListView.setAdapter(adapter);
    }

    @Override
    protected void process(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        mContext = getActivity();
        userid = MyApplication.getMyApplication().getMyPreferenceManager().readString(ApplictionUtils.USER_NAME_NET, "");
        sessionDao = new SessionDao(mContext);
        findView();
        initDataS();
    }

    @Override
    protected void onMessageHandle(Object param1, Object param2, String respond) {

    }


    @Override
    public void onRefresh() {
        mCustomListView.onRefreshComplete();
        initDataS();
    }

    /**
     * 接受消息广播
     */
    private class MyUIUpBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sessionList.clear();
            initDataS();
        }
    }
}
