package zj.chat.com.imchat.room;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.adapter.FaceVPAdapter;
import zj.chat.com.imchat.cache.ben.RoomChatMsg;
import zj.chat.com.imchat.cache.ben.RoomInfo;
import zj.chat.com.imchat.cache.ben.Session;
import zj.chat.com.imchat.commadapter.CommBaseAdapter;
import zj.chat.com.imchat.commadapter.ViewHolder;
import zj.chat.com.imchat.common.BaseActivity;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppConnect;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.custom.view.DropdownListView;
import zj.chat.com.imchat.custom.view.TakePhotoActivity;
import zj.chat.com.imchat.db.RoomChatMsgDao;
import zj.chat.com.imchat.db.SessionDao;
import zj.chat.com.imchat.dialog.ToastDialog;
import zj.chat.com.imchat.dialog.ToastUtils;
import zj.chat.com.imchat.dialog.VoiceProDialog;
import zj.chat.com.imchat.util.ApplictionUtils;
import zj.chat.com.imchat.util.ChatUtils;
import zj.chat.com.imchat.util.ExpressionUtil;
import zj.chat.com.imchat.util.FilePathUtiles;
import zj.chat.com.imchat.util.ImgBitmatUtils;
import zj.chat.com.imchat.util.MediaPlayManager;
import zj.chat.com.imchat.util.RecordManager;
import zj.chat.com.imchat.util.TimeUtils;
import zj.chat.com.imchat.xmpplistener.AddRoomLinsener;
import zj.chat.com.imchat.xmpplistener.GetRoomMenberLinsener;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 群聊主界面
 */
public class RoomChatActivity extends BaseActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {
    private static final int CAMERA_WITH_DATA = 0x00; // 拍照
    private static final int RESULT_LOAD_IMAGE = 0x01;// 到图库庁图片
    private File mCurrentPhotoFile = null;// 拍照图片路径
    private ViewPager mViewPager;
    private DropdownListView mListView = null;
    private EditText input_sms = null;//输入类容
    private TextView send_sms = null;//发送
    private ImageView image_add = null;//添加
    private ImageView image_face = null;//选择表情
    private String myUserid = "";//自己的ID
    public static String fromUserId = "";//聊天对象ID
    private RoomChatMsgDao msgDao;//消息数据库操作管理在
    private SessionDao sessionDao;//聊天列表
    private Chat userChat = null;//聊天对象
    private int offset;
    public static String roomId = "";//群ID
    private String roomName = "";
    //消息列表
    private List<RoomChatMsg> listMsg = new ArrayList<RoomChatMsg>();
    private String LOG = "ChatActivity:";
    private TextView tv_title_conent;
    private MsgRecver mr = null;
    private final RoomManager roomManager = null;//房间管理类
    private LinearLayout mDotsLayout;
    private Button btn_chat_voice = null;//点击录声音按钮
    private Button btn_chat_keyboard = null;//点击切换到输入文字
    private Button btn_speak = null;//长按说话
    private TextView tv_pic,//图片
            tv_camera,//拍照
            tv_loc;//位置
    private LinearLayout chat_face_container, chat_add_container;
    //表情图标每页6列4行
    private int columns = 6;
    private int rows = 4;
    //每页显示的表情view
    private List<View> views = new ArrayList<View>();
    //表情列表
    private List<String> staticFacesList;
    private MyMsgAdapter mMyMsgAdapter = null;//适配器
    private String recordPath = "";//声音保存路径
    private RecordManager mRecordManager = null;//录音帮助类
    //隔多久去获取一次音量
    private static final int POLL_INTERVAL = 300;
    private VoiceProDialog voiceProDialog = null;
    private Handler mHandler = new Handler();
    //开始录入时间
    private long recordTim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgDao = new RoomChatMsgDao(this);
        sessionDao = new SessionDao(this);
        myUserid = MyApplication.getMyApplication().getMyPreferenceManager().readString(ApplictionUtils.USER_NAME_NET, "");
        tv_title_conent.setText(XmppUtils.getUserName(roomName));
        staticFacesList = ExpressionUtil.initStaticFaces(this);
        initViewPager();
        //显示聊天对象名称
        initChatMsg();
        initRoomInfo();
    }

    /**
     * 初始化表情
     */
    private void initViewPager() {
        int pagesize = ExpressionUtil.getPagerCount(staticFacesList.size(), columns, rows);
        // 获取页数
        for (int i = 0; i < pagesize; i++) {
            views.add(ExpressionUtil.viewPagerItem(this, i, staticFacesList, columns, rows, input_sms));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    /**
     * 表情页切换时，底部小圆点
     *
     * @param position
     * @return
     */
    private ImageView dotsItem(int position) {
        View layout = View.inflate(mContext, R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    /**
     * 获取聊天成员
     */
    private void initRoomInfo() {
        final RoomManager roomManager = RoomManager.getRommManager(mContext);
        //去加入房间
        roomManager.intoRoom(roomId, new AddRoomLinsener() {
            @Override
            public void onAddRoomSucess() {
                roomManager.getAllMember(new GetRoomMenberLinsener() {//获取群成员列表
                    @Override
                    public void onGetRoomMenberSucuss(ArrayList<RoomInfo> rs) {
                    }

                    @Override
                    public void onGetRommMenberError() {
                    }
                });
            }

            @Override
            public void onAddRoomError(Exception e) {
                ToastDialog.ShowDialog(mContext, "加入房间失败:" + e.toString());
            }
        });
    }

    /**
     * 初始化消息列表
     */
    private void initChatMsg() {
        mr = new MsgRecver();
        //注册一个聊天消息广播
        IntentFilter ifs = new IntentFilter();
        ifs.addAction(ChatUtils.ROOM_MSG_FRIEN_RECVER);
        registerReceiver(mr, ifs);
        offset = 0;
        //到本地数据库查询聊天记录
        listMsg = msgDao.queryMsg(roomId, offset);
        offset = listMsg.size();
        mMyMsgAdapter = new MyMsgAdapter(mContext);
        mMyMsgAdapter.setmLists(listMsg);
        mListView.setAdapter(mMyMsgAdapter);

        //默认移动到最后
        mListView.setSelection(mListView.getCount());
        ChatManager cm = XmppConnect.getXmppConnectInstance().getChatManager();
        userChat = cm.createChat(fromUserId, null);
    }

    void updateSession(String type, String content) {

        Session session = new Session();
        session.setFrom(fromUserId);
        session.setTo(myUserid);
        session.setChatType(ChatUtils.GROUP_CHAT);
        session.setNotReadCount("");//未读消息数量
        if (type.equals(ChatUtils.MSG_TYPE_IMG)) {
            session.setContent("[图片]");
        } else if (type.equals(ChatUtils.MSG_TYPE_TEXT)) {
            session.setContent(content);
        } else if (type.equals(ChatUtils.MSG_TYPE_VOICE)) {
            session.setContent("[声音]");
        } else {
            session.setContent("[]");
        }
        session.setRoomJid(roomId);
        session.setTime(TimeUtils.getCurrentTimeAllString());
        session.setType(type);
        if (sessionDao.isRoomContent(roomId)) {
            sessionDao.upRoomDateSession(session);
        } else {
            sessionDao.insertSession(session);
        }
        Intent intent = new Intent();//发送广播，通知消息界面更新
        intent.setAction(ApplictionUtils.FREND_UPDATA);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomId = "";
        if (mr != null) {
            unregisterReceiver(mr);
            mr = null;
        }
        RoomManager.getRommManager(mContext).closeLinsener();
    }

    @Override
    protected void initPageView() {
        mListView = (DropdownListView) findViewById(R.id.formclient_listview);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        input_sms = (EditText) findViewById(R.id.input_sms);
        send_sms = (TextView) findViewById(R.id.send_sms);
        image_add = (ImageView) findViewById(R.id.image_add);
        image_face = (ImageView) findViewById(R.id.image_face);
        tv_title_conent = (TextView) findViewById(R.id.tv_title_conent);
        //表情图标
        image_face = (ImageView) findViewById(R.id.image_face);
        //更多图标
        image_add = (ImageView) findViewById(R.id.image_add);
        //表情布局
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
        //更多
        chat_add_container = (LinearLayout) findViewById(R.id.chat_add_container);

        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);

        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_speak = (Button) findViewById(R.id.btn_speak);
        tv_pic = (TextView) findViewById(R.id.tv_pic);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_loc = (TextView) findViewById(R.id.tv_loc);
    }

    private Runnable mPollTask = new Runnable() {
        public void run() {
            if (mRecordManager == null) {
                return;
            }
            double amp = mRecordManager.getVolume();
            if (voiceProDialog != null && voiceProDialog.isShowing()) {
                voiceProDialog.setVioveStates(amp);
            }
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
    }

    @Override
    protected void initPageViewListener() {
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        send_sms.setOnClickListener(this);
        mListView.setOnRefreshListenerHead(this);
        //表情按钮
        image_face.setOnClickListener(this);
        //更多按钮
        image_add.setOnClickListener(this);
        tv_pic.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_loc.setOnClickListener(this);
        send_sms.setOnClickListener(this);
        //点击界面的时候，如果表情框显示要去关闭
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chat_face_container.getVisibility() == View.VISIBLE) {
                        chat_face_container.setVisibility(View.GONE);
                    }
                    if (chat_add_container.getVisibility() == View.VISIBLE) {
                        chat_add_container.setVisibility(View.GONE);
                    }
                    hideSoftInputView();
                }

                return false;
            }
        });
        voiceProDialog = new VoiceProDialog(mContext);
        btn_speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记录开始录入时间
                        recordTim = System.currentTimeMillis();
                        btn_speak.setBackgroundResource(R.drawable.check_voice_bg);
                        voiceProDialog.show();
                        recordPath = FilePathUtiles.createRecordPath();
                        mRecordManager = new RecordManager(mContext, recordPath);
                        mRecordManager.startRecord();
                        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn_speak.setBackgroundResource(R.drawable.nom_voice_bg);
                        mHandler.removeCallbacks(mPollTask);
                        voiceProDialog.dismiss();
                        mRecordManager.stopRecord();
                        mRecordManager = null;
                        //如果录入声音时间太短不让去发送
                        if (System.currentTimeMillis() - recordTim < 1000) {
                            File file = new File(recordPath);
                            try {
                                file.delete();
                                ToastUtils.show(mContext, "持续时间太短!");
                            } catch (Exception e) {
                            }
                        } else {//去发送声音
                            sendVoice();
                        }


                        break;
                }
                return false;
            }
        });
        //监听edit文本变化
        input_sms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    hideView(3);
                } else {
                    hideView(4);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 去发送声音
     */
    private void sendVoice() {
        final RoomChatMsg cm = getChatMsg("", ChatUtils.MSG_TYPE_VOICE);
        //设置好图片本地路径
        cm.setContentPath(recordPath);
        new AsyncTask<RoomChatMsg, Void, Exception>() {
            @Override
            protected Exception doInBackground(RoomChatMsg... params) {

                RoomChatMsg c = params[0];
                try {
                    RoomManager.getRommManager(mContext).sendVoice(c);
                    return null;
                } catch (XMPPException e) {
                    return e;
                }


            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                if (e == null) {
                    addChatMsg(cm);
                    cm.setMsgId(msgDao.insert(cm));
                    //放到消息列表去
                    updateSession(ChatUtils.MSG_TYPE_VOICE, cm.getContent());
                } else {
                    ToastUtils.show(mContext, "发送图片失败:" + e.toString());
                }
            }
        }.execute(cm);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected int initContentLayoutID() {
        return R.layout.activity_chat_room_layotu;
    }

    @Override
    protected int initTitleLayoutID() {
        return 0;
    }

    @Override
    protected void getIntentData() {
        roomId = getIntent().getStringExtra(ChatUtils.ROOM_ID_KEY);
        roomName = getIntent().getStringExtra(ChatUtils.ROOM_NAME_KEY);
    }

    private RoomChatMsg getChatMsg(String msg, String msgType) {
        String currDate = TimeUtils.getCurrentTimeAllString();
        RoomChatMsg cm = new RoomChatMsg();
        cm.setFromUser(myUserid);
        cm.setType(msgType);
        cm.setContent(msg);
        cm.setDate(currDate);
        cm.setRoomJid(roomId);
        cm.setIsMySend(ChatUtils.IS_MY_SEND);
        return cm;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_sms://发送
                final String msg = input_sms.getText().toString().trim();
                if ("".equals(msg)) {

                    return;
                }
                final RoomChatMsg cm = getChatMsg(msg, ChatUtils.MSG_TYPE_TEXT);
                input_sms.setText("");
                //去显示消息
                addChatMsg(cm);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RoomManager.getRommManager(mContext).sendRoomMessage(ChatUtils.appenSendMsg(cm));
                        cm.setMsgId(msgDao.insert(cm));
                        updateSession(ChatUtils.MSG_TYPE_TEXT, cm.getContent());

                    }
                }).start();
            case R.id.input_sms://点击输入框的时候，要把表情框关闭
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face:
                hideSoftInputView();//隐藏软键盘
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_add:
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.GONE) {
                    chat_add_container.setVisibility(View.VISIBLE);
                } else {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_pic://点击图片
                //点击拍照后先隐藏当前打开的界面
                chat_add_container.setVisibility(View.GONE);
                Intent intentImg = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentImg, RESULT_LOAD_IMAGE);
                break;
            case R.id.tv_camera://拍照
                //点击拍照后先隐藏当前打开的界面
                chat_add_container.setVisibility(View.GONE);
                File filePath = new File(FilePathUtiles.CHAT_IMG_FILE);
                if (!filePath.exists()) {
                    FilePathUtiles.makeDir(filePath);
                }
                String fileName = FilePathUtiles.getPhotoFileName();
                mCurrentPhotoFile = new File(filePath, fileName);
                Intent intent = new Intent(mContext, TakePhotoActivity.class);
                intent.putExtra(TakePhotoActivity.KEY_TAKE_PHOTO_PATH,
                        mCurrentPhotoFile.toString());
                startActivityForResult(intent, CAMERA_WITH_DATA);
                break;
            case R.id.btn_chat_voice://点击显示声音按钮
                Log.i("keey", "声音");
                hideView(1);
                break;
            case R.id.btn_chat_keyboard://点击显示输入框
                hideView(2);
                break;
        }
    }

    private void addChatMsg(RoomChatMsg msg) {
        input_sms.setText("");
        listMsg.add(msg);
        offset = listMsg.size();
        mMyMsgAdapter.setmLists(listMsg);
        mMyMsgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        List<RoomChatMsg> list = msgDao.queryMsg(roomId, offset);
        if (list.size() <= 0) {
            mListView.setSelection(0);
            mListView.onRefreshCompleteHeader();
            return;
        }
        listMsg.addAll(0, list);
        offset = listMsg.size();
        mListView.onRefreshCompleteHeader();
        mMyMsgAdapter.setmLists(listMsg);
        mMyMsgAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size());
    }


    /**
     * 消息适配器
     */
    private class MyMsgAdapter extends CommBaseAdapter<RoomChatMsg> {

        public MyMsgAdapter(Context context) {
            super(context);
        }

        @Override
        public int getLayoutId() {
            return R.layout.chat_lv_item;
        }

        @Override
        public void convert(ViewHolder holder, final RoomChatMsg chatMsg, int postion) {
            //时间
            ((TextView) holder.getView(R.id.chat_time)).setText(chatMsg.getDate());

            //自己发送的消息显示
            RelativeLayout chart_to_container = holder.getView(R.id.chart_to_container);
            //接收到的消息
            LinearLayout chart_from_container = holder.getView(R.id.chart_from_container);
            //声音
            ImageView iv_right_vioce = holder.getView(R.id.iv_right_vioce);
            if (chatMsg.getIsMySend().equals(ChatUtils.IS_MY_SEND)) {
                chart_to_container.setVisibility(View.VISIBLE);
                chart_from_container.setVisibility(View.GONE);
                //自己发送的消息图标
                holder.getView(R.id.chatto_icon);
                TextView chatto_content = holder.getView(R.id.chatto_content);
                //图片
                ImageView chatto_img = holder.getView(R.id.chatto_img);
                if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_TEXT)) {
                    chatto_content.setVisibility(View.VISIBLE);
                    iv_right_vioce.setVisibility(View.GONE);
                    chatto_img.setVisibility(View.GONE);
                    SpannableStringBuilder sb = ExpressionUtil.prase(mContext, chatto_content, chatMsg.getContent());// 对内容做处理
                    chatto_content.setText(sb);
                } else if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_IMG)) {
                    chatto_content.setVisibility(View.GONE);
                    chatto_img.setVisibility(View.VISIBLE);
                    iv_right_vioce.setVisibility(View.GONE);
                    chatto_img.setImageBitmap(null);
                    chatto_img.setImageBitmap(ImgBitmatUtils.getPic(mContext, chatMsg.getContentPath()));
                } else if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_VOICE)) {//声音
                    chatto_content.setVisibility(View.GONE);
                    chatto_img.setVisibility(View.GONE);
                    iv_right_vioce.setVisibility(View.VISIBLE);
                    iv_right_vioce.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MediaPlayManager.getmMediaPlayManager().playMusic(chatMsg.getContentPath());
                        }
                    });
                }
            } else {
                chart_to_container.setVisibility(View.GONE);
                chart_from_container.setVisibility(View.VISIBLE);
                //图标
                holder.getView(R.id.chatfrom_icon);
                TextView chatfrom_content = holder.getView(R.id.chatfrom_content);
                ImageView iv_left_vioce = holder.getView(R.id.iv_left_vioce);
                //img内容
                ImageView chatfrom_img = holder.getView(R.id.chatfrom_img);
                //文本内容
                if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_TEXT)) {
                    chatfrom_content.setVisibility(View.VISIBLE);
                    chatfrom_img.setVisibility(View.GONE);
                    iv_left_vioce.setVisibility(View.GONE);
                    SpannableStringBuilder sb = ExpressionUtil.prase(mContext, chatfrom_content, chatMsg.getContent());// 对内容做处理
                    chatfrom_content.setText(sb);
                } else if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_IMG)) {//图片
                    iv_left_vioce.setVisibility(View.GONE);
                    chatfrom_content.setVisibility(View.GONE);
                    chatfrom_img.setVisibility(View.VISIBLE);
                    chatfrom_img.setImageBitmap(null);
                    chatfrom_img.setImageBitmap(ImgBitmatUtils.getPic(mContext, chatMsg.getContentPath()));
                } else if (chatMsg.getType().equals(ChatUtils.MSG_TYPE_VOICE)) {//声音
                    chatfrom_content.setVisibility(View.GONE);
                    iv_left_vioce.setVisibility(View.VISIBLE);
                    chatfrom_img.setVisibility(View.GONE);
                    iv_left_vioce.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MediaPlayManager.getmMediaPlayManager().playMusic(chatMsg.getContentPath());
                        }
                    });
                }
                //加载进度
                holder.getView(R.id.progress_load).setVisibility(View.GONE);

            }
        }
    }

    private class MsgRecver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                RoomChatMsg cm = (RoomChatMsg) intent.getSerializableExtra(ChatUtils.MSG_KEY);
                addChatMsg(cm);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_WITH_DATA://拍照返回图片
                if (resultCode != RESULT_OK) {
                    return;
                }
                sendImg(mCurrentPhotoFile.toString());
                break;
            case RESULT_LOAD_IMAGE://选择图片
                if (resultCode != RESULT_OK) {
                    return;
                }
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver()
                        .query(selectedImage, filePathColumn, null,
                                null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor
                            .getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    sendImg(picturePath);

                } else {
                    Uri geturi = FilePathUtiles.geturi(data, mContext);
                    sendImg(geturi.getPath());
                }
                break;
        }
    }

    private void sendImg(String filePath) {
        final RoomChatMsg cm = getChatMsg("", ChatUtils.MSG_TYPE_IMG);
        //设置好图片本地路径
        cm.setContentPath(filePath);
        new AsyncTask<RoomChatMsg, Void, Exception>() {
            @Override
            protected Exception doInBackground(RoomChatMsg... params) {

                RoomChatMsg c = params[0];
                try {
                    RoomManager.getRommManager(mContext).sendRoomImgMessage(c, RoomChatActivity.this);
                    return null;
                } catch (XMPPException e) {
                    return e;
                }


            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                if (e == null) {
                    addChatMsg(cm);
                    cm.setMsgId(msgDao.insert(cm));
                    //放到消息列表去
                    updateSession(ChatUtils.MSG_TYPE_IMG, cm.getContent());
                } else {
                    ToastUtils.show(mContext, "发送图片失败:" + e.toString());
                }
            }
        }.execute(cm);
    }

    /**
     * @param id
     */
    private void hideView(int id) {
        switch (id) {
            case 1://点击录制声音
                btn_chat_voice.setVisibility(View.INVISIBLE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                send_sms.setVisibility(View.GONE);
                break;
            case 2:
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.INVISIBLE);
                btn_speak.setVisibility(View.GONE);
                break;
            case 3:
                send_sms.setVisibility(View.VISIBLE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.INVISIBLE);
                btn_speak.setVisibility(View.GONE);
                break;
            case 4:
                send_sms.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                break;
        }
    }
}
