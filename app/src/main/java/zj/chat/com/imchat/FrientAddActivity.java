package zj.chat.com.imchat;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zj.chat.com.imchat.commadapter.CommBaseAdapter;
import zj.chat.com.imchat.commadapter.ViewHolder;
import zj.chat.com.imchat.common.BaseActivity;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.custom.view.TitleBarView;
import zj.chat.com.imchat.dialog.ToastDialog;
import zj.chat.com.imchat.dialog.ToastUtils;
import zj.chat.com.imchat.util.StringUtils;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 添加好友
 */
public class FrientAddActivity extends BaseActivity implements View.OnClickListener {
    private TitleBarView mTitleBarView = null;//title部分
    private TextView tv_ss = null;//搜素按钮
    private EditText et_content = null;//输入搜素 内容
    private ListView mListView = null;//搜素到的好友
    private FrientAdatper mFrientAdatper = null;
    private List<String> mLists = new ArrayList<>();

    @Override
    protected void initPageView() {
        mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.GONE);
        mTitleBarView.setTitleText("添加好友");
        tv_ss = (TextView) findViewById(R.id.tv_ss);
        et_content = (EditText) findViewById(R.id.et_content);
        mListView = (ListView) findViewById(R.id.lv_list);
    }

    @Override
    protected void initPageViewListener() {
        tv_ss.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected int initContentLayoutID() {
        return R.layout.activity_frient_add_layout;
    }

    @Override
    protected int initTitleLayoutID() {
        return 0;
    }

    @Override
    protected void getIntentData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ss://搜索
                String conetn = et_content.getText().toString().trim();
                if (StringUtils.stringIsEmp(conetn)) {
                    ToastUtils.show(mContext, "请输入名称");
                    return;
                }
                new AsyncTask<String, Void, List<String>>() {
                    @Override
                    protected List<String> doInBackground(String... params) {
                        String con = params[0];
                        return XmppUtils.searchUsers(con);
                    }

                    @Override
                    protected void onPostExecute(List<String> strings) {
                        super.onPostExecute(strings);
                        mLists.clear();
                        mLists = strings;
                        if (mFrientAdatper == null) {
                            mFrientAdatper = new FrientAdatper(mContext);
                            mFrientAdatper.setmLists(mLists);
                            mListView.setAdapter(mFrientAdatper);
                        } else {
                            mFrientAdatper.setmLists(mLists);
                            mFrientAdatper.notifyDataSetChanged();
                        }
                    }
                }.execute(conetn);
                break;
        }
    }

    private class FrientAdatper extends CommBaseAdapter<String> {

        public FrientAdatper(Context context) {
            super(context);
        }

        @Override
        public int getLayoutId() {
            return R.layout.view_add_frient_layout;
        }

        @Override
        public void convert(ViewHolder holder, String s, int postion) {
            ((TextView) holder.getView(R.id.tv_name)).setText(s);
        }
    }
}
