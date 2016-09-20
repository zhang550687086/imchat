package zj.chat.com.imchat.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.commadapter.CommBaseAdapter;
import zj.chat.com.imchat.commadapter.ViewHolder;
import zj.chat.com.imchat.common.BaseFragment;
import zj.chat.com.imchat.custom.view.TitleBarView;
import zj.chat.com.imchat.room.RoomManager;
import zj.chat.com.imchat.room.RoomChatActivity;
import zj.chat.com.imchat.util.ChatUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 我列表
 */
public class MyselfFragment extends BaseFragment {
    private View view = null;
    private TitleBarView mTitleBarView;
    private ListView lv_list = null;
    //房间列表
    private RoomAdapter mRoomAdapter = null;
    private ArrayList<DiscoverItems.Item> rooms = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mTitleBarView = (TitleBarView) view.findViewById(R.id.title_bar);
        lv_list = (ListView) view.findViewById(R.id.lv_list);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.GONE);
        mTitleBarView.setTitleText(R.string.mime);
    }

    @Override
    protected void initPageViewListener() {
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RoomChatActivity.class);
                intent.putExtra(ChatUtils.ROOM_ID_KEY, rooms.get(position).getEntityID());
                intent.putExtra(ChatUtils.ROOM_NAME_KEY, rooms.get(position).getName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected View loadTopLayout() {

        return null;
    }

    @Override
    protected View loadContentLayout() {
        view = View.inflate(mContext, R.layout.fragment_mysely_layout, null);
        return view;
    }

    @Override
    protected void process(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        final RoomManager roomManager = RoomManager.getRommManager(getActivity());
        rooms = roomManager.initRoom(ChatUtils.DEFAULT_ROOM);
        if (rooms != null && rooms.size() > 0) {//如果有房间就显示
            initAdapter();
        }


    }

    private void initAdapter() {
        if (mRoomAdapter == null) {
            mRoomAdapter = new RoomAdapter(getActivity());
            mRoomAdapter.setmLists(rooms);
            lv_list.setAdapter(mRoomAdapter);
        } else {
            mRoomAdapter.setmLists(rooms);
            mRoomAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onMessageHandle(Object param1, Object param2, String respond) {

    }

    /**
     * 群組列表
     */
    private class RoomAdapter extends CommBaseAdapter<DiscoverItems.Item> {

        public RoomAdapter(Context context) {
            super(context);
        }

        @Override
        public int getLayoutId() {
            return R.layout.view_room_list_layout;
        }

        @Override
        public void convert(ViewHolder holder, DiscoverItems.Item roomInfo, int postion) {
            ((TextView) holder.getView(R.id.tv_name)).setText(roomInfo.getName());
        }
    }
}
