package zj.chat.com.imchat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import zj.chat.com.imchat.R;

/**
 * ==============================================
 *
 * @author Zj
 */
public class TAlertTitleDialog extends Dialog implements View.OnClickListener {
    private TextView mTitleView = null; // 标题
    private ImageView mIcon = null; // 图标
    private TextView mMsgText = null; // 提示内容
    private Button mOK = null; // 确认按钮
    private Button mCancel = null; // 取消按钮
    private OnButtonClickListener mListener = null; // 监听者
    private boolean mEnableBack = true; // 是否允许BACK键
    public Context mContext;

    public interface OnButtonClickListener {
        /**
         * 确认
         */
        public void onOK();

        /**
         * 取消
         */
        public void onCancel();
    }

    public TAlertTitleDialog(Context context, OnButtonClickListener listener) {
        super(context);
        this.mContext = context;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED,
                WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED);
        Window window = this.getWindow();
        setCanceledOnTouchOutside(false);
        window.setContentView(R.layout.dialog_alert_title);
        window.setBackgroundDrawableResource(R.drawable.transparent_background);
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7); // 高度设置为屏幕的0.6
        lp.dimAmount = 0.4f; // dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
        window.setAttributes(lp);
        // window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mTitleView = (TextView) window.findViewById(R.id.tv_title);
        mMsgText = (TextView) window.findViewById(R.id.tv_message);
        mOK = (Button) window.findViewById(R.id.btnOK);
        mCancel = (Button) window.findViewById(R.id.btnCancel);

        mOK.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                if (mListener != null) {
                    mListener.onOK();
                }
                dismiss();
                break;
            case R.id.btnCancel:
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mEnableBack) {
            super.onBackPressed();
        }
    }

    // 设置BACK键是否允许状态
    public void setKeyBack(boolean enable) {
        mEnableBack = enable;
    }

    // 设置标题
    public void setTitle(int nResId) {
        if (mTitleView != null && nResId > 0) {
            mTitleView.setText(nResId);
        }
    }

    // 设置标题
    public void setTitle(String value) {
        if (mTitleView != null && value != null) {
            mTitleView.setText(value);
        }
    }

    // 设置图标
    public void setIcon(int nResId, int visibility) {
        if (mIcon != null) {
            if (nResId > 0) {
                mIcon.setBackgroundResource(nResId);
            }
            mIcon.setVisibility(visibility);
        }
    }

    // 设置图标
    public void setIcon(Drawable drawable, int visibility) {
        if (mIcon != null) {
            // mIcon.setBackground(drawable);
            mIcon.setVisibility(visibility);
        }
    }

    // 设置消息内容, 颜色
    public void setMessage(String value, int color) {
        setMessage(value);
        setMessageColor(color);
    }

    // 设置消息内容
    public void setMessage(int nResId) {
        if (mMsgText != null && nResId > 0) {
            mMsgText.setText(nResId);
        }
    }

    // 设置消息内容
    public void setMessage(String value) {
        if (mMsgText != null && !TextUtils.isEmpty(value)) {
            mMsgText.setText(value);
        }
    }

    // 设置消息内容颜色
    public void setMessageColor(int color) {
        if (mMsgText != null) {
            mMsgText.setTextColor(color);
        }
    }

    // 设置确认按钮
    public void setBtnOK(int nResId, int visibility) {
        if (mOK != null) {
            if (nResId > 0) {
                mOK.setText(nResId);
            }
            mOK.setVisibility(visibility);
        }
    }

    // 设置确认按钮
    public void setBtnOK(String value, int visibility) {
        if (mOK != null) {
            if (!TextUtils.isEmpty(value)) {
                mOK.setText(value);
            }
            mOK.setVisibility(visibility);
        }
    }

    // 设置取消按钮
    public void setBtnCancel(int nResId, int visibility) {
        if (mCancel != null) {
            if (nResId > 0) {
                mCancel.setText(nResId);
            }
            mCancel.setVisibility(visibility);
        }
    }

    // 设置取消按钮
    public void setBtnCancel(String value, int visibility) {
        if (mCancel != null) {
            if (!TextUtils.isEmpty(value)) {
                mCancel.setText(value);
            }
            mCancel.setVisibility(visibility);
        }
    }

    // 设置取消按钮图标
    public void setBtnCancelIcon(int nResId) {
        if (mCancel != null && nResId > 0) {
            mCancel.setBackgroundResource(nResId);
        }
    }
}
