package zj.chat.com.imchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import zj.chat.com.imchat.R;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * 声音dialog
 */
public class VoiceProDialog extends Dialog {
    private Context mContext = null;
    //声音图片
    private ImageView iv_record = null;

    public VoiceProDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public VoiceProDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected VoiceProDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        View view = LinearLayout.inflate(mContext, R.layout.view_vioce_dialog, null);
        window.setContentView(view);
        window.setBackgroundDrawableResource(R.color.transparent);
        setCanceledOnTouchOutside(false);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        iv_record = (ImageView) view.findViewById(R.id.iv_record);
    }

    /**
     * 根据声音的强度来显示不同的图片
     * @param dj
     */
    public void setVioveStates(double dj) {
        switch ((int) dj) {
            case 0:
            case 1:
            case 2:
                iv_record.setImageResource(R.mipmap.chat_icon_voice1);
                break;
            case 3:
            case 4:
            case 5:
                iv_record.setImageResource(R.mipmap.chat_icon_voice2);
                break;
            case 6:
            case 7:
            case 8:
                iv_record.setImageResource(R.mipmap.chat_icon_voice3);
                break;
            case 9:
            case 10:
            case 11:
                iv_record.setImageResource(R.mipmap.chat_icon_voice4);
                break;
            case 12:
            case 13:
            case 14:
                iv_record.setImageResource(R.mipmap.chat_icon_voice5);
                break;
            default:
                iv_record.setImageResource(R.mipmap.chat_icon_voice6);
                break;
        }
    }
}
