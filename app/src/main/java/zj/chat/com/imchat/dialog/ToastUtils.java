package zj.chat.com.imchat.dialog;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils
 */
public class ToastUtils {

    public static final int LENGTH_MAX = -1;
    // private static boolean mCanceled = true;
    // private static Handler mHandler;
    // private static Context mContext;
    private static Toast mToast;

    public static void show(int resId, int duration) {
        mToast.setText(resId);
        if (duration != LENGTH_MAX) {
            mToast.setDuration(duration);
            mToast.show();
            // } else if(mCanceled) {
            // mToast.setDuration(Toast.LENGTH_LONG);
            // mCanceled = false;
            // showUntilCancel();
        }
    }

    /**
     * @param text     要显示的内容
     * @param duration 显示的时间长 根据LENGTH_MAX进行判断 如果不匹配，进行系统显示 如果匹配，永久显示，直到调用hide()
     */
    public static void show(String text, int duration) {
        mToast.setText(text);
        if (duration == Toast.LENGTH_LONG) {
            mToast.setDuration(duration);
            mToast.show();
        } else if (duration == Toast.LENGTH_SHORT) {
            mToast.setDuration(duration);
            mToast.show();
        } else if (duration != LENGTH_MAX) {
            mToast.setDuration(duration);
            mToast.show();
        } else {
            // if(mCanceled) {
            // mToast.setDuration(Toast.LENGTH_LONG);
            // mCanceled = false;
            // showUntilCancel();
            // }
        }
    }

    /**
     * 隐藏Toast
     */
    // public void hide(){
    // mToast.cancel();
    // mCanceled = true;
    // }

    // public boolean isShowing() {
    // return !mCanceled;
    // }

    // private static void showUntilCancel() {
    // if(mCanceled)
    // return;
    // mToast.show();
    // mHandler.postDelayed(new Runnable() {
    // public void run() {
    // showUntilCancel();
    // }
    // },3000);
    // }
    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, 5000);
    }

    public static void show(CharSequence text, Context mContext) {
        show(mContext, text, Toast.LENGTH_LONG);
    }

    public static void show(Context context, CharSequence text, int duration) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        // Toast.makeText(context, text, duration).show();
        show(text.toString(), duration);
    }

    public static void show(Context context, int resId, Object... args) {
        show(context,
                String.format(context.getResources().getString(resId), args),
                Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration,
                            Object... args) {
        show(context,
                String.format(context.getResources().getString(resId), args),
                duration);
    }

    public static void show(Context context, String format, int duration,
                            Object... args) {
        show(context, String.format(format, args), duration);
    }
}
