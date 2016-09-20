package zj.chat.com.imchat.commadapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/***
 * 
 * @author xiaoshi email:emotiona_xiaoshi@126.com
 * @TODO 公用Adapter
 * @2015年7月14日
 * @param <T>
 *
 */
public abstract class CommBaseAdapter<T> extends BaseAdapter {
	protected List<T> mLists;
	protected Context mContext;
	protected LayoutInflater mInflater;

	public CommBaseAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public List<T> getmLists() {
		return mLists;
	}

	public void setmLists(List<T> mLists) {
		this.mLists = mLists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLists.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getHolder(position, mContext, convertView, parent, getLayoutId());
		convert(holder, getItem(position), position);
		return holder.getConvertView();
	}

	/***
	 * 获取资源文件
	 * 
	 * @return
	 */
	public abstract int getLayoutId();

	/**
	 * 抽象方法，用户实现控件的事件处理
	 * 
	 * @param holder
	 * @param t
	 */
	public abstract void convert(ViewHolder holder, T t, int postion);
}
