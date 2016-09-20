package zj.chat.com.imchat;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import zj.chat.com.imchat.common.BaseActivity;
import zj.chat.com.imchat.main.fragment.FrientFragment;
import zj.chat.com.imchat.main.fragment.MessageFragment;
import zj.chat.com.imchat.main.fragment.MyselfFragment;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 主界面
 */
public class MainManagerActivity extends BaseActivity {
    private RadioGroup navi_radio_group = null;//下部选项
    private RadioButton navi_btn_message = null;//聊天列表
    private RadioButton navi_btn_firend = null;//好友列表
    private RadioButton navi_btn_setting = null;//我
    private FragmentTransaction ft;
    private MessageFragment mMessageFragment = null;//聊天记录
    private FrientFragment mFrientFragment = null;//好友列表
    private MyselfFragment mMyselfFragment = null;//我


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragemtn();
    }

    private void initFragemtn() {
        navi_btn_firend.setChecked(true);

    }

    @Override
    protected void initPageView() {
        navi_radio_group = (RadioGroup) findViewById(R.id.opens).findViewById(R.id.navi_radio_group);
        navi_btn_message = (RadioButton) findViewById(R.id.navi_btn_message);
        navi_btn_firend = (RadioButton) findViewById(R.id.navi_btn_firend);
        navi_btn_setting = (RadioButton) findViewById(R.id.navi_btn_setting);
    }

    @Override
    protected void initPageViewListener() {

        navi_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.navi_btn_message://选择消息
                        showContentFragment(1, null);
                        break;
                    case R.id.navi_btn_firend://选择好友
                        showContentFragment(2, null);
                        break;
                    case R.id.navi_btn_setting://选择我
                        showContentFragment(3, null);
                        break;
                }
            }
        });
    }

    private void hideContentFragment() {
        ft = getFragmentManager().beginTransaction();
        if (mMessageFragment != null) {
            ft.hide(mMessageFragment);
        }
        if (mFrientFragment != null) {
            ft.hide(mFrientFragment);
        }
        if (mMyselfFragment != null) {
            ft.hide(mMyselfFragment);
        }
    }

    /***
     * 显示內容
     */
    private void showContentFragment(int index, Bundle bundle) {
        ft = getFragmentManager().beginTransaction();
        hideContentFragment();
        switch (index) {
            case 1:
                if (mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                    ft.add(R.id.fl_conent, mMessageFragment);
                } else {
                    ft.show(mMessageFragment);
                }
                ft.commit();
                navi_btn_message.setChecked(true);

                break;
            case 2:
                if (mFrientFragment == null) {
                    mFrientFragment = new FrientFragment();
                    ft.add(R.id.fl_conent, mFrientFragment);
                } else {
                    ft.show(mFrientFragment);
                }
                ft.commit();
                navi_btn_firend.setChecked(true);
                break;
            case 3:
                if (mMyselfFragment == null) {
                    mMyselfFragment = new MyselfFragment();
                    ft.add(R.id.fl_conent, mMyselfFragment);
                } else {
                    ft.show(mMyselfFragment);
                }
                ft.commit();
                navi_btn_setting.setChecked(true);
                break;
        }
    }

    @Override
    protected int initContentLayoutID() {
        return R.layout.activity_main_manager_layout;
    }

    @Override
    protected int initTitleLayoutID() {
        return 0;
    }

    @Override
    protected void getIntentData() {

    }
}
