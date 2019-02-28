package 通用_Application模板;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import 通用_Log打印.MyLog;
import 通用_崩溃前日志保存.CrashCatchHandler;

/**
 * use Instrumentation see activity lifecycle use：AndroidManifest.xml in
 * application add android:name=".BaseApplication"
 * 
 * 用Instrumentation看各个activity的生命周期的交互 在AndroidManifest.xml的 application标签下，添加
 * android:name=".BaseApplication"
 * 
 * PS：see fragment lifecycle，use fragmentmanager. enableDebugLogging(true);
 * 
 * @author jlpan
 * 
 */
public class BaseApplication extends Application {
//	public static String TAG = null;
	PackageManager mPackageManager = null;
	public static Context mcontext=null; 
	@Override
	public void onCreate() {
		super.onCreate();
		mcontext=getApplicationContext();
//		TAG = getAppName(getApplicationContext());
		//这两句是使用崩溃日志保存
		CrashCatchHandler crashCatchHandler = CrashCatchHandler.getInstance();//获得单例
		crashCatchHandler.init(getApplicationContext());//初始化,传入context
		
		replaceInstrumentation();
	}

	static public class MyInstrumentation extends Instrumentation {

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			super.callActivityOnCreate(activity, icicle);
			// Activ
			MyLog.showLog(mcontext, "***OnCreate***");
		}

		@Override
		public void callActivityOnDestroy(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnDestroy(activity);
			MyLog.showLog(mcontext,"***OnDestroy***");
		}

		@Override
		public void callActivityOnNewIntent(Activity activity, Intent intent) {
			// TODO Auto-generated method stub
			super.callActivityOnNewIntent(activity, intent);
			MyLog.showLog(mcontext, "***OnNewIntent***");
		}

		@Override
		public void callActivityOnPause(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnPause(activity);
			MyLog.showLog(mcontext, "***OnPause***");
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			super.callActivityOnPostCreate(activity, icicle);
			MyLog.showLog(mcontext, "***OnPostCreate***");
		}

		@Override
		public void callActivityOnRestart(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnRestart(activity);
			MyLog.showLog(mcontext, "***OnRestart***");
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.callActivityOnRestoreInstanceState(activity, savedInstanceState);
			MyLog.showLog(mcontext, "***OnRestoreInstanceState***");
		}

		@Override
		public void callActivityOnResume(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnResume(activity);
			MyLog.showLog(mcontext, "***OnResume***");
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
			// TODO Auto-generated method stub
			super.callActivityOnSaveInstanceState(activity, outState);
			MyLog.showLog(mcontext, "***OnSaveInstanceState***");
		}

		@Override
		public void callActivityOnStart(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnStart(activity);
			MyLog.showLog(mcontext, "***OnStart***");
		}

		@Override
		public void callActivityOnStop(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnStop(activity);
			MyLog.showLog(mcontext, "***OnStop***");
		}

		@Override
		public void callActivityOnUserLeaving(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnUserLeaving(activity);
			MyLog.showLog(mcontext, "***OnUserLeaving***");
		}

		@Override
		public void callApplicationOnCreate(Application app) {
			// TODO Auto-generated method stub
			super.callApplicationOnCreate(app);
			MyLog.showLog(mcontext, app.getPackageName() + ":" + "***OnCreate***");
		}
	}

	/**
	 * 替换系统默认的Instrumentation
	 */
	public static void replaceInstrumentation() {
		Class<?> activityThreadClass;
		try {
			// 加载activity thread的class
			activityThreadClass = Class.forName("android.app.ActivityThread");

			// 找到方法currentActivityThread
			Method method = activityThreadClass.getDeclaredMethod("currentActivityThread");
			// 由于这个方法是静态的，所以传入Null就行了
			Object currentActivityThread = method.invoke(null);

			// 把之前ActivityThread中的mInstrumentation替换成我们自己的
			Field field = activityThreadClass.getDeclaredField("mInstrumentation");
			field.setAccessible(true);
			MyInstrumentation mInstrumentation = new MyInstrumentation();
			field.set(currentActivityThread, mInstrumentation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * 获取应用程序名称
//	 */
//	public static String getAppName(Context context) {
//		try {
//			PackageManager packageManager = context.getPackageManager();
//			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
//			int labelRes = packageInfo.applicationInfo.labelRes;
//			return context.getResources().getString(labelRes);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * [获取应用程序版本名称信息]
//	 * 
//	 * @param context
//	 * @return 当前应用的版本名称
//	 */
//	public static String getVersionName(Context context) {
//		try {
//			PackageManager packageManager = context.getPackageManager();
//			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
//			return packageInfo.versionName;
//
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 
//	 * @return 当前方法的方法名字符串
//	 */
//	public static String getCurrentMethodName() {
//		int level = 1;
//		StackTraceElement[] stacks = new Throwable().getStackTrace();
//		String methodName = stacks[level].getMethodName();
//		return methodName;
//	}
//
//	/**
//	 * 
//	 * @return 当前类的类名字符串
//	 */
//	public static String getCurrentClassName() {
//		int level = 1;
//		StackTraceElement[] stacks = new Throwable().getStackTrace();
//		String className = stacks[level].getClassName();
//		return className;
//	}

}
