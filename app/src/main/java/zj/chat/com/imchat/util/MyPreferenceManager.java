package zj.chat.com.imchat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences的工具类
 * 
 * SharedPreferences是Android平台上一个轻量级的存储类，主要是保存一些常用的配置比如窗口状态， 一般在Activity中
 * 重载窗口状态onSaveInstanceState保存一般使用SharedPreferences完成， 它提供了Android平台常规的Long长
 * 整形、Int整形、String字符串型的保存
 * 
 * 在Android系统中，这些信息以XML文件的形式保存在 /data/data/PACKAGE_NAME /shared_prefs 目录下
 * 
 * @author nieshun
 * 
 */
public class MyPreferenceManager {
	/***
	 * 共享首先项
	 */
	private static final String PREFERENCE_NAME = "HBXYPreference";
	private static SharedPreferences shareditorPreferences;
	public static Editor editor;
	private static MyPreferenceManager shareferenceManager;

	private MyPreferenceManager(Context context) {
		if (shareditorPreferences == null || editor == null) {
			try {
				shareditorPreferences = context.getSharedPreferences(
						PREFERENCE_NAME, 0);
				editor = shareditorPreferences.edit();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 单例模式获取 首选项实例
	 * 
	 * @param activity
	 * @return
	 */

	public static MyPreferenceManager getInstance(Context context) {
		if (shareferenceManager == null) {
			shareferenceManager = new MyPreferenceManager(context);
		}
		return shareferenceManager;
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return根据键查值
	 */
	public int readInt(String key, int value) {
		return shareditorPreferences.getInt(key, value);
	}

	public String readString(String key, String value) {
		return shareditorPreferences.getString(key, value);
	}

	public long readLong(String key, long value) {
		return shareditorPreferences.getLong(key, value);
	}

	public boolean readBoolean(String key, boolean flag) {
		return shareditorPreferences.getBoolean(key, flag);
	}


	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return根据键写值
	 */
	public void writeInt(String key, int value) {
		editor.putInt(key, value).commit();
	}

	public void writeString(String key, String value) {
		editor.putString(key, value).commit();
	}

	public void writeLong(String key, long value) {
		editor.putLong(key, value).commit();
	}

	public void writeBoolean(String key, boolean flag) {
		editor.putBoolean(key, flag).commit();
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return根据键删值
	 */
	public void remove(String key) {
		editor.remove(key).commit();
	}

}
