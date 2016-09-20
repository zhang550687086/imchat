package zj.chat.com.imchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import zj.chat.com.imchat.R;


public class ToastDialog extends Dialog implements OnClickListener {
    private Context mContext;
    private OnButtonClick monButtonClick;
    private TextView tv_message;
    private Window window;
    private boolean isBack = false;// 是否需要控制返回

    public ToastDialog(Context context, OnButtonClick mOnButtonClick) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.monButtonClick = mOnButtonClick;
    }

    /**
     * 确定监听
     */
    public interface OnButtonClick {
        public void onComrirm();
    }

    /**
     * 确定按钮监听
     */
    public void setComrifmClick(OnButtonClick mOnButtonClick) {
        this.monButtonClick = mOnButtonClick;

    }

    public void setKeyBack(boolean isBack) {
        this.isBack = isBack;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && isBack) {

            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        window = getWindow();
//		window.setContentView(R.layout.view_toast_layou);
        window.setContentView(R.layout.view_tishi_dialog);
        window.setBackgroundDrawableResource(R.drawable.transparent_background);
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.6); // 高度设置为屏幕的0.6
        lp.height = (int) (d.heightPixels * 0.5);

        lp.dimAmount = 0.2f; // dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗

        window.setAttributes(lp);
        // window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.findViewById(R.id.confirm).setOnClickListener(this);
        tv_message = (TextView) window.findViewById(R.id.tv_message);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.confirm:// 确定
                this.dismiss();
                if (monButtonClick != null) {
                    monButtonClick.onComrirm();

                }

                break;

            default:
                break;
        }
    }

    /**
     * 设置提示消息
     *
     * @param msg
     */
    public void setMessage(String msg) {
        tv_message.setText(msg);
    }

    public static void ShowDialog(Context mContext, String msg) {
        ToastDialog d = null;
        if (d == null) {
            d = new ToastDialog(mContext, null);
        }
        if (d.isShowing()) {
            d.dismiss();
        }

        d.show();
        d.setMessage(msg + "");
    }

    /**
     * 安全判断
     *
     * @param mContext
     * @param msg
     * @param isYx
     */
    public static void MyShowDialog(Context mContext, String msg, Boolean isYx) {
        if (isYx) {
            return;
        }
        ToastDialog d = null;
        if (d == null) {
            d = new ToastDialog(mContext, null);
        }
        if (d.isShowing()) {
            d.dismiss();
        }

        d.show();
        d.setMessage(msg + "");

    }

}
