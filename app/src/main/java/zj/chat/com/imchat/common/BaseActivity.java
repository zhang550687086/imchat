package zj.chat.com.imchat.common;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.dialog.LoadingProgressDialog;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public abstract class BaseActivity extends Activity {
    /**
     * 加载提示框
     */
    public LoadingProgressDialog mLoadingProgressDialog = null;
    protected int titleHeight = LinearLayout.LayoutParams.MATCH_PARENT;//title默认高度
    protected Context mContext;
    public TextView tv_back = null;// 返回按钮
    public TextView tv_text = null;// 标题按钮
    public TextView tv_right = null;// 右边按钮
    public LinearLayout ll_title = null;//布局title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        super.onCreate(savedInstanceState);
        mContext = this;
        ininView();
        initPageView();
        initPageViewListener();
        getIntentData();
    }

    private void ininView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_base_layout, null);
        if (initTitleLayoutID() > 0) {//如果设置了title
            RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight);
            ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
            ll_title.addView(inflater.inflate(initTitleLayoutID(), null), layoutParamsContent);
        } else if (initTitleLayoutID() == -1) {//设置默认的title
            RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight);
            ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
            View titleView = inflater.inflate(R.layout.title, null);
            ll_title.addView(titleView, layoutParamsContent);
            tv_back = (TextView) titleView.findViewById(R.id.tv_back);
            tv_text = (TextView) titleView.findViewById(R.id.tv_title);
            tv_right = (TextView) titleView.findViewById(R.id.tv_right);
            tv_back.setOnClickListener(new View.OnClickListener() {//监听返回按钮
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {//没有title
            view.findViewById(R.id.ll_title).setVisibility(View.GONE);
        }

        if (initContentLayoutID() > 0) {
            LinearLayout ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
            View l_view = inflater.inflate(initContentLayoutID(), null);
            RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            ll_content.addView(l_view, layoutParamsContent);
        }
        setContentView(initContentLayoutID());

    }

    /**
     * 是否显示titel
     *
     * @param isShow
     */
    protected void setTitleIsShow(boolean isShow) {
        if (ll_title != null) {
            if (isShow) {
                ll_title.setVisibility(View.VISIBLE);
            } else {
                ll_title.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 设置title标题
     *
     * @param tc
     */
    protected void setTitleContent(String tc) {
        if (initTitleLayoutID() == -1) {
            tv_text.setText(tc);
        }
    }

    /**
     * 初始化布局
     */
    protected abstract void initPageView();

    /**
     * 初始化监听
     */
    protected abstract void initPageViewListener();

    /**
     * 设置布局内容
     *
     * @return
     */
    protected abstract int initContentLayoutID();

    /**
     * 设置title布局
     *
     * @return
     */
    protected abstract int initTitleLayoutID();

    /**
     * 获取Intent数据
     */
    protected abstract void getIntentData();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    /**
     * @param message
     * @param //isFA点击其他地方是否消失
     */
    public void showLoadingDialog(String message, boolean isFA) {
        showLoadingDialog(message);
        mLoadingProgressDialog.setCanceledOnTouchOutside(isFA);

    }

    /**
     * 开启加载对话框,默认提示语为:努力加载中...
     */
    public void showLoadingDialog() {
        showLoadingDialog("正在加载数据中...");
    }

    /**
     * 开启加载对话框
     *
     * @param msg 需要显示的提示文本信息
     */
    public void showLoadingDialog(String msg) {
        // if (!mIsActive) {
        // return;
        // }
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing()) {
            return;
        }
        if (mLoadingProgressDialog == null) {
            mLoadingProgressDialog = new LoadingProgressDialog(mContext);
        }
        mLoadingProgressDialog.show();
        mLoadingProgressDialog.setMessage(msg);

    }

    /**
     * 取消加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingProgressDialog != null) {
            mLoadingProgressDialog.dismiss();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * 替换FRAGMENT
     *
     * @param fragment        : Fragment
     * @param bundle          : Fragment传递的参数
     * @param containerViewId : 容器视图ID
     * @param bAddBackStack   : 是否增加返回堆栈
     * @param bClearStack     : 清空堆栈
     */
    protected void replaceFragment(Fragment fragment, Bundle bundle,
                                   int containerViewId, boolean bAddBackStack, boolean bClearStack) {
        if (fragment == null)
            return;
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (bClearStack) { // 需要清空栈
            while (null != getFragmentManager()
                    && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        if (bAddBackStack) {
            transaction.addToBackStack(null);
        }
        if (!this.isFinishing()) {

            transaction.replace(containerViewId, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

}
