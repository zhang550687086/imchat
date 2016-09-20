package zj.chat.com.imchat.common;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import zj.chat.com.imchat.R;
import zj.chat.com.imchat.http.listener.ICallBack;


/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 打印log日志tag，值为当前类名称，方便输出日志查阅
     */
    public final String tag = this.getClass().getSimpleName();
    /**
     * 是否执行了OnAttach
     */
    private boolean isExceOnAttach = false;
    /**
     * 是否隐藏页面了
     */
    private boolean isHidePage = false;
    /**
     * 是否正在展示页面
     */
    public boolean isShowingPage = false;

    /**
     * 页面跳转传递的参数
     */
    private Bundle bundle;
    /**
     * 基类view
     */
    public View baseMainView;
    /**
     * 顶部title布局
     */
    public RelativeLayout topFragmentLayout;
    /**
     * 底部菜单布局
     */
    public RelativeLayout contentFramentLayout;
    /**
     * 数据为空时候的背景
     */
    public LinearLayout linEmpty;
    /**
     * 数据为空时候的icon
     */
    public ImageView imgEmpty;
    /**
     * 数据为空时候的文案
     */
    public TextView textEmpty;
    /**
     * 数据加载的时候现实loading 圈圈
     */
    public ProgressBar progressBar;
    /**
     * 数据加载的时候现实loading 圈圈图标
     */
    public ImageView imgProgressBarIcon;
    /**
     * progressBar
     */
    public RelativeLayout progressBarLayout;

    public BaseActivity activity;
    /**
     * 网络请求加载dialog
     */
    protected Dialog mLoadingDialog = null;
    /**
     * activity 是否处于可见状态
     */
    public boolean mIsActive = true;
    private Toast mToast;
    public Context mContext;
    private boolean isFirst = true;// 是否第一次进入。用来控制加载框

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isExceOnAttach = true;
        this.activity = (BaseActivity) activity;
        isShowingPage = true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseMainView = inflateLayout(inflater, container, savedInstanceState);
        if (this.getActivity() != null) {
            mContext = this.getActivity();
        }
        Log.i("ActivityManage:", this.getClass().getName());// 打印出每个activity的类名
        Log.e("TAG", tag);
        findBaseViewById();
        /** 初始化页面控件。 */
        initPageView();
        initPageViewListener();
        process(savedInstanceState);
        initData();
        return baseMainView;
    }

    /**
     * 获取全局页面控件对象
     */
    @SuppressWarnings("deprecation")
    protected void findBaseViewById() {
        topFragmentLayout = (RelativeLayout) baseMainView.findViewById(R.id.topLayout);
        contentFramentLayout = (RelativeLayout) baseMainView.findViewById(R.id.contentLayout);
        linEmpty = (LinearLayout) baseMainView.findViewById(R.id.yLinEmpty);
        imgEmpty = (ImageView) baseMainView.findViewById(R.id.yImgEmpty);
        textEmpty = (TextView) baseMainView.findViewById(R.id.yTextEmpty);
        progressBarLayout = (RelativeLayout) baseMainView.findViewById(R.id.progressBarLayout);
        progressBar = (ProgressBar) baseMainView.findViewById(R.id.yProgressBar);
        imgProgressBarIcon = (ImageView) baseMainView.findViewById(R.id.yImgProgressBarIcon);
        View topView = loadTopLayout();
        if (topView == null) {
            topFragmentLayout.setVisibility(View.GONE);
        } else {
            RelativeLayout.LayoutParams layoutParamsTop = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            topFragmentLayout.setVisibility(View.VISIBLE);
            topFragmentLayout.addView(topView, layoutParamsTop);
        }

        View contentView = loadContentLayout();
        if (contentView == null) {
            contentFramentLayout.setVisibility(View.GONE);
        } else {
            RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            contentFramentLayout.setVisibility(View.VISIBLE);
            contentFramentLayout.addView(contentView, layoutParamsContent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isExceOnAttach && !isHidePage) {
            // 不是第一次进来并且没有隐藏，需要刷新页面可以实现此方法
            isShowingPage = true;
        }
        if (isShowingPage) {
            onResumePage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isExceOnAttach = false;
        isShowingPage = false;
        onPausePage(1);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidePage = hidden;
        isShowingPage = !hidden;
        if (!hidden) {
            onResumePage();
        } else {
            onPausePage();
        }
    }

    /**
     * 重新进入页面逻辑处理，留给子fragment重写,默认不做任何处理
     *
     * @param
     */
    public abstract void onResumePage();

    /**
     * 页面暂时关起，留给子fragment重写,默认不做任何处理
     *
     * @param params
     */
    public void onPausePage(Object... params) {

    }

    /**
     * 加载布局文件
     */
    public abstract View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 抽象方法 ，子类必须实现，初始化页面控件。
     */
    protected abstract void initPageView();

    /**
     * 抽象方法 ，子类必须实现，页面控件点击事件处理
     */
    protected abstract void initPageViewListener();

    /**
     * 获取顶部布局
     */
    protected abstract View loadTopLayout();

    /**
     * 获取内容布局
     */
    protected abstract View loadContentLayout();

    /**
     * 抽象方法 ，子类必须实现，逻辑处理
     */
    protected abstract void process(Bundle savedInstanceState);

    /**
     * fragment中的返回方法
     *
     * @return 如果需要页面自己处理返回，那么需要重写此方法
     */
    public boolean onBack() {
        return false;
    }

    /**
     * 页面跳转传递的参数
     *
     * @param bundle
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * 获取页面传递的参数
     *
     * @return
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * 初始数据
     */
    public abstract void initData();

    /**
     * 显示loadingbar
     */
    public void showProgressBar() {
        if (isFirst) {
            progressBarLayout.setBackgroundColor(Color.WHITE);
            imgProgressBarIcon.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.VISIBLE);
            isFirst = false;
        }

    }

    public void showToast(String message) {
        if (null == mToast) {
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 显示带背景的loadingbar
     *
     * @param resId   资源id 0：代表不设置
     * @param colorId 颜色id 0：代表不设置
     */
    public void showProgressBarAndBackGround(int resId, int colorId) {
        progressBarLayout.setVisibility(View.VISIBLE);
        imgProgressBarIcon.setVisibility(View.VISIBLE);
        if (resId != 0) {
            progressBarLayout.setBackgroundResource(resId);
        } else if (colorId != 0) {
            progressBarLayout.setBackgroundColor(colorId);
        } else {
            progressBarLayout.setBackgroundColor(Color.WHITE);
        }

    }

    /**
     * 开启加载对话框,默认提示语为:努力加载中...
     */
    public void showLoadingDialog() {
        showLoadingDialog("正在加载数据中...");
    }

    /**
     * 消息响应事件处理
     *
     * @param
     */
    protected abstract void onMessageHandle(Object param1, Object param2, String respond);

    /**
     * 开启加载对话框
     *
     * @param msg 需要显示的提示文本信息
     */
    public void showLoadingDialog(String msg) {
        if (!mIsActive) {
            return;
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        mLoadingDialog = customLoadingDialog(msg);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show();
    }

    /**
     * 取消加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 自定义LoadingDialog,子类如果不想使用默认实现的Dialog可以重写此方法返回一个自定义的Dialog对象即可。
     *
     * @param msg loading提示语
     * @return Dialog
     */
    public Dialog customLoadingDialog(String msg) {
        ProgressDialog dialog = new ProgressDialog(mContext, R.style.LoadingDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(msg);
        return dialog;
    }

    /**
     * 隐藏loadingbar
     */
    public void hideProgressBar() {
        progressBarLayout.setBackgroundResource(0);
        progressBarLayout.setVisibility(View.GONE);
    }

    /**
     * 请求成功回调 zj
     */
    public class BaseHttpHandler implements ICallBack {

        private String mUrl = null; // 请求URL地址
        private Object mParam1 = null; // 参数1
        private Object mParam2 = null; // 参数2

        public BaseHttpHandler(Object param1, Object param2) {
            this.mParam1 = param1;
            this.mParam2 = param2;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public String getUrl() {
            return this.mUrl;
        }

        @Override
        public void onSuccess(String response) {
            // TODO Auto-generated method stub
            onMessageHandle(mParam1, mParam2, response);
        }

        @Override
        public void onFailed(String error) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * 替换账户FRAGMENT, 不清楚堆栈
     */
    protected void replaceAccountFragment(Fragment fragment, Bundle bundle,
                                          boolean bAddBackStack) {
        replaceFragment(fragment, bundle, R.id.fl_conent, bAddBackStack,
                false);
    }

    /**
     * 替换账户FRAGMENT, 需要清除堆栈
     */
    protected void replaceAccountFragmentWithClear(Fragment fragment,
                                                   Bundle bundle, boolean bAddBackStack) {
        while (null != getFragmentManager()
                && getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        }
        replaceFragment(fragment, bundle, R.id.fl_conent, bAddBackStack,
                false);
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

        FragmentTransaction transaction = null;
        if (containerViewId == R.id.fl_conent
                ) {
            if (null != getActivity()) {
                transaction = getActivity().getFragmentManager()
                        .beginTransaction();
            } else {
                transaction = ((Activity) mContext).getFragmentManager()
                        .beginTransaction();
            }
        } else {
            return;
        }

        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        if (bClearStack) {// 需要清空栈
            while (null != getFragmentManager()
                    && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }

        }
        if (bAddBackStack) {
            transaction.addToBackStack(null);
        }
        if (getActivity() != null && !getActivity().isFinishing()) {// android
            transaction.replace(containerViewId, fragment);
            transaction.commitAllowingStateLoss();
        } else { // 有可能fragment返回的时候，activity被销毁，所有getActivity()取的值为空
            if ((mContext instanceof Activity)
                    && (!((Activity) mContext).isFinishing())) {
                transaction.replace(containerViewId, fragment);
                transaction.commitAllowingStateLoss();
            }
        }
    }
}
