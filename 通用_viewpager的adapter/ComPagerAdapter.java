package 通用_viewpager的adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 这个是用于viewpager的通用adapter.性能还待验证.
 * 测试发现点击事件的响应不是本页,而是下页???
 * 			原因:在convert里,listview的adapter设置为了全局变量,导致listview数据共用了
 * 目前支持对单个layout的所有控件赋值等(和ComAdapter一样的使用).但对fragment支持还不太好
 * 
 * @author PanJunLong
 *
 * @param <T>
 */
public abstract class ComPagerAdapter<T> extends PagerAdapter {
	private List<T> mData = new ArrayList<T>();// 每个item的数据
	// private List<View> list_view = new ArrayList<View>();
	Context mcontext;
	protected int layoutId;// inf布局的id

	public ComPagerAdapter(Context context, List<T> list_view) {
		if (mData != null) {
			this.mData.clear();
			this.mData.addAll(list_view);
		}
		mcontext = context;
		layoutId = getRLayoutID();
	}

	abstract public int getRLayoutID();

	@Override
	public int getCount() {
		// return mData == null ? 0 : mData.size();
		return mData == null ? 0 : (mData.size() > 0 ? mData.size() : 7);
	}

	/**
	 * 当前显示页
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		boolean flag = arg0 == arg1;
		return flag;
	}

	/**
	 * 创建给定位置界面，并添加到container容器中。虽然他只需要确保这是finishUpdate返回时完成的操作
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// View t = list_view.get(position);
		View t = getView(position, null, container);
		container.addView(t);
		return t;
	}

	/**
	 * 销毁上一页
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	/*
	 * 在每次调用notifyDataSetChanged()时，都会激活getItemPosition(Object object)方法，
	 * 该方法会遍历viewpager的所有item（据我debug的结果，只有当前页和其左右加起来共3页被
	 * 遍历了，待确定），为每个item返回一个状态值（POSITION_NONE/POSITION_UNCHANGED），
	 * 如果是none，那么该item会被destroyItem(ViewGroup container, int position, Object
	 * object) 方法remove掉，然后重新加载，如果是unchanged，就不会重新加载，默认是unchanged，
	 * 所以如果我们不重写getItemPosition(Object object)，就无法看到刷新效果。
	 */
	// @Override
	// public int getItemPosition(Object object) {
	// View view = (View) object;
	// int currentPage = mcontext.getCurrentPagerIdx();
	// if (currentPage == (Integer) view.getTag()) {
	// return POSITION_NONE;
	// } else {
	// return POSITION_UNCHANGED;
	// }
	//
	// }

	public T getItem(int position) {
		if (mData == null) {
			return null;
		} else {
			int num = mData.size();
			if (num == 0) {
				return null;
			} else if (num <= position) {
				return mData.get(position - 1);
			} else {
				return mData.get(position);
			}
		}
	}

	// 默认背景
	private int[] colorArray = new int[] { Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW,
			Color.WHITE };

	private View getDefaultBackGround(int position) {
		// //判断泛型参数类型
		// if(Fragment.class.isAssignableFrom(T.class)){
		//
		// }
		View view = new View(mcontext);
		view.setBackgroundColor(colorArray[position % 7]);
		return view;
	}

	private View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getHolder(mcontext, layoutId, convertView, parent);
		T t = getItem(position);
		if (t != null) {
			convert(holder, getItem(position));
			return holder.getmConvertView();
		} else {
			return getDefaultBackGround(position);
		}

	};

	/**
	 * 控件赋值（需要在声明adapter时实现的方法，里面主要是item xml中的控件的声明和viewholder的关联）
	 * 
	 * @param holder
	 * @param bean
	 */
	public abstract void convert(ViewHolder holder, T bean);

	private static class ViewHolder {
		private SparseArray<View> mViews;
		private View mConvertView;

		/**
		 * 构造函数
		 * 
		 * @param context
		 * @param resLayoutId
		 *            item的xml的R值
		 * @param convertView
		 *            item对应的view
		 * @param parent
		 *            父布局
		 */
		private ViewHolder(Context context, int resLayoutId, View convertView, ViewGroup parent) {
			this.mViews = new SparseArray<View>();
			// this.position = position;
			this.mConvertView = LayoutInflater.from(context).inflate(resLayoutId, parent, false);
			this.mConvertView.setTag(this);
		}

		/**
		 * 获取一个ViewHolder
		 * 
		 * @param context
		 * @param resLayoutId
		 * @param convertView
		 * @param parent
		 * @return
		 */
		public static ViewHolder getHolder(Context context, int resLayoutId, View convertView, ViewGroup parent) {
			if (convertView == null) {
				return new ViewHolder(context, resLayoutId, convertView, parent);
			}
			return (ViewHolder) convertView.getTag();
		}

		/**
		 * 通过控件的id获取对应的控件，如果没有则加入mViews;记住 <T extends View> T 这种用法
		 * 
		 * @param viewId
		 *            item中单个控件的R值
		 * @return
		 */
		public <T extends View> T findViewById(int viewId) {
			View view = mViews.get(viewId);
			if (view == null) {
				view = mConvertView.findViewById(viewId);
				mViews.put(viewId, view);
			}
			return (T) view;
		}

		/**
		 * 获得一个convertView
		 * 
		 * @return
		 */
		public View getmConvertView() {
			return mConvertView;
		}
	}

	/*
	 * 当你实现一个PagerAdapter时，至少需要覆盖以下几个方法:
	 * 
	 * instantiateItem(ViewGroup, int) destroyItem(ViewGroup, int, Object)
	 * getCount() isViewFromObject(View, Object)
	 * 
	 * PagerAdapter比AdapterView的使用更加普通.ViewPager使用回调函数来表示一个更新的步骤，
	 * 而不是使用一个视图回收机制。在需要的时候pageradapter也可以实现视图的回收或者使用一种
	 * 更为巧妙的方法来管理视图，比如采用可以管理自身视图的fragment。
	 * viewpager不直接处理每一个视图而是将各个视图与一个键联系起来。这个键用来跟踪且唯一代表
	 * 一个页面，不仅如此，该键还独立于这个页面所在adapter的位置。
	 * 当pageradapter将要改变的时候他会调用startUpdate函数，接下来会调用一次或多次的
	 * instantiateItem或者destroyItem。 最后在更新的后期会调用finishUpdate。 当finishUpdate返回时
	 * instantiateItem返回的对象应该添加到父ViewGroup destroyItem返回的对象应该被ViewGroup删除。
	 * methodisViewFromObject(View, Object)代表了当前的页面是否与给定的键相关联。
	 * 
	 * 对于非常简单的pageradapter或许你可以选择用page本身作为键，在创建并且添加到viewgroup后
	 * instantiateItem方法里返回该page本身即可
	 * destroyItem将会将该page从viewgroup里面移除。isViewFromObject方法里面直接可以返回view==object。
	 * 
	 * pageradapter支持数据集合的改变，数据集合的改变必须要在主线程里面执行，然后还要调用
	 * notifyDataSetChanged方法。和baseadapter非常相似。数据集合的改变包括页面的添加删除和修
	 * 改位置。viewpager要维持当前页面是活动的，所以你必须提供getItemPosition方法。
	 */

}