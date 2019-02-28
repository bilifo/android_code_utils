package 通用_Log打印;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

public class MyLog {
	/**
	 * true:打开所有日志 false:关闭所有日志
	 */
	public static boolean CLOSE_ALL_LOG = false;

	/**
	 * 自制的log
	 * 
	 * @param text
	 */
	static public void showLog(Context context, String text) {
		if (!CLOSE_ALL_LOG) {
			StackTraceElement mStackTraceElement = getStackTraceElement();
			Class mclass = null;
			try {
				mclass = Class.forName(mStackTraceElement.getClassName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//没有实现ThisClassCloseLog标志接口
			if (mclass != null && (!ThisClassCloseLog.class.isAssignableFrom(mclass)))
				Log.d(getAppName(context) + "->" + getCallClassName(mStackTraceElement) + "->"
						+ getCallMethodName(mStackTraceElement), text);
		}
	}

	/**
	 * 自制的普通toast
	 * 
	 * @param context
	 * @param text
	 */
	static public void showToast(Context context, String text) {
		if (!CLOSE_ALL_LOG) {
			Toast.makeText(context, text, 0).show();
		}
	}

	/**
	 * 自制的在一段时间里只弹一次的toast
	 * 
	 * @param context
	 * @param text
	 */
	static public void showSingleToast(Context context, String text) {
		if (!CLOSE_ALL_LOG) {
			MyToast.showToast(context, text);
		}
	}

	/**
	 * 在一段时间里只弹一次的toast
	 * 
	 * @author PanJunLong
	 *
	 */
	private static class MyToast {
		// 解决反复弹toast
		private static String oldMsg;
		protected static Toast toast = null;
		private static long oneTime = 0;
		private static long twoTime = 0;

		public static void showToast(Context context, String s) {
			if (toast == null) {
				toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
				toast.show();
				oneTime = System.currentTimeMillis();
			} else {
				twoTime = System.currentTimeMillis();
				if (s.equals(oldMsg)) {
					if (twoTime - oneTime > Toast.LENGTH_SHORT) {
						toast.show();
					}
				} else {
					oldMsg = s;
					toast.setText(s);
					toast.show();
				}
			}
			oneTime = twoTime;
		}

		public static void showToast(Context context, int resId) {
			showToast(context, context.getString(resId));
		}
	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得调用MyLog这个类的call类的调用方法的方法名
	 * 
	 * @param targetStackTrace
	 * @return
	 */
	public static String getCallMethodName(StackTraceElement targetStackTrace) {
		return targetStackTrace.getMethodName();
	}

	/**
	 * 获得调用MyLog这个类的call类的类名
	 * 
	 * @param targetStackTrace
	 * @return
	 */
	public static String getCallClassName(StackTraceElement targetStackTrace) {
		return targetStackTrace.getClassName();
	}

	/**
	 * 获得调用MyLog这个类的call类的StackTraceElement
	 * 
	 * @return
	 */
	private static StackTraceElement getStackTraceElement() {
		StackTraceElement targetStackTrace = null;
		boolean shouldTrace = false;
		StackTraceElement[] stacks = new Throwable().getStackTrace();
		for (StackTraceElement mStackTraceElement : stacks) {
			boolean isLogMethod = mStackTraceElement.getClassName().equals(MyLog.class.getName());
			if (shouldTrace && !isLogMethod) {
				targetStackTrace = mStackTraceElement;
				break;
			}
			shouldTrace = isLogMethod;
		}
		return targetStackTrace;
	}

	/**
	 * 用状态模式来关闭一个类的log信息。某个类实现了该接口，将不会打印log
	 * 
	 * @author PanJunLong
	 *
	 */
	public interface ThisClassCloseLog {
	}

}
