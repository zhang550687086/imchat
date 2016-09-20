package zj.chat.com.imchat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import zj.chat.com.imchat.R;

/**
 * 加载dialog
 *
 * @author zj
 *
 */

public class LoadingProgressDialog extends Dialog {

	private TextView mMsgView = null; // 消息视图
	private String mMessage = null; // 提示消息
	private Context mContext;

	public LoadingProgressDialog(Context context) {
		super(context, R.style.dialog);
		this.mContext = context;
	}

	public LoadingProgressDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	public LoadingProgressDialog(Context context, String message) {
		super(context, R.style.dialog);
		mMessage = message;
		this.mContext = context;
	}

	/**
	 * 设置提示内容
	 *
	 * @param value
	 *            : 提示消息
	 */
	public void setMessage(String value) {
		mMessage = value;
		updateMessage();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		// LayoutInflater factory = LayoutInflater.from(this.getContext());
		View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
		window.setContentView(layout);
		setCanceledOnTouchOutside(false);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mMsgView = (TextView) layout.findViewById(R.id.tv_message);
		updateMessage();
		WindowManager.LayoutParams lp = window.getAttributes();
		DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.4); // 高度设置为屏幕的0.6
		lp.height = (int) (d.widthPixels * 0.4);
		lp.alpha = 0.6f;// 设置dialog透明度
		window.setAttributes(lp);
	}

	// 更新视图
	private void updateMessage() {
		if (!TextUtils.isEmpty(mMessage) && mMsgView != null) {
			mMsgView.setText(mMessage);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
}
