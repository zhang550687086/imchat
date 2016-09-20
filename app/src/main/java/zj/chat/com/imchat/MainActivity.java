package zj.chat.com.imchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import zj.chat.com.imchat.bin.UserInfo;
import zj.chat.com.imchat.common.BaseActivity;
import zj.chat.com.imchat.common.LoginManager;
import zj.chat.com.imchat.common.MyApplication;
import zj.chat.com.imchat.connect.XmppUtils;
import zj.chat.com.imchat.dialog.ToastDialog;
import zj.chat.com.imchat.util.ApplictionUtils;
import zj.chat.com.imchat.util.MyPreferenceManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_username = null;//账号
    private EditText et_pass = null;//密码
    private Button bt_submit = null;//账号
    private MyReceiveBroad mMyReceiveBroad = null;//注册广波

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    /**
     * 显示本地保存数据
     */
    private void initData() {
        MyPreferenceManager myPreferenceManager = MyApplication.getMyApplication().getMyPreferenceManager();
        String user_name = myPreferenceManager.readString(ApplictionUtils.USER_NAME, "");
        String user_pass = myPreferenceManager.readString(ApplictionUtils.USER_PASS, "");
        if (!"".equals(user_name)) {
            et_username.setText(user_name);
            et_username.setSelection(user_name.length());
        }
        if (!"".equals(user_pass)) {
            et_pass.setText(user_pass);
            et_pass.setSelection(user_pass.length());
        }

    }

    @Override
    protected void initPageView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pass = (EditText) findViewById(R.id.et_pass);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    protected void initPageViewListener() {
        bt_submit.setOnClickListener(this);
    }

    @Override
    protected int initContentLayoutID() {
        return R.layout.activity_main;
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
            case R.id.bt_submit:
                showLoadingDialog("正在登陆...");
                final String userName = et_username.getText().toString().trim();
                final String pass = et_pass.getText().toString().trim();
                if ("".equals(userName)) {
                    return;
                }
                if ("".equals(pass)) {
                    return;
                }
                MyPreferenceManager myPreferenceManager = MyApplication.getMyApplication().getMyPreferenceManager();
                myPreferenceManager.writeString(ApplictionUtils.USER_NAME, userName);
                myPreferenceManager.writeString(ApplictionUtils.USER_NAME_NET, userName + "@" + XmppUtils.serverIp);
                myPreferenceManager.writeString(ApplictionUtils.USER_PASS, pass);

                Intent intentServer = new Intent(MyApplication.getMyApplication().getApplicationContext(), XmppService.class);
                MyApplication.getMyApplication().getApplicationContext().startService(intentServer);
                mMyReceiveBroad = new MyReceiveBroad();
                IntentFilter logIntenSer = new IntentFilter();
                logIntenSer.addAction(ApplictionUtils.LOG_RECVER);
                registerReceiver(mMyReceiveBroad, logIntenSer);
                break;
        }
    }

    private class MyReceiveBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            dismissLoadingDialog();
            boolean isLogin = intent.getBooleanExtra("isLogin", false);
            unLogReceiver();
            if (isLogin) {
                MyApplication.getMyApplication().setCurrUserInfo(new UserInfo(et_username.getText().toString().trim(), et_pass.getText().toString().trim()));
                Intent intents = new Intent(mContext, MainManagerActivity.class);//FriendListActivity
                intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mContext.startActivity(intents);
                finish();
            } else {
                String e = intent.getStringExtra(ApplictionUtils.LOGIN_ERROR_MSG);
                ToastDialog.ShowDialog(mContext, e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unLogReceiver();
    }

    /**
     * 解除注册广播
     */
    private void unLogReceiver() {
        if (mMyReceiveBroad != null) {
            unregisterReceiver(mMyReceiveBroad);
            mMyReceiveBroad = null;
        }
    }
}

