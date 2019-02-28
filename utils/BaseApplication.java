package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

/**
 * use Instrumentation see activity lifecycle
 * use：AndroidManifest.xml
 * 			in application
 * 				add android:name=".BaseApplication"
 * 用Instrumentation看各个activity的生命周期的交互
 * 在AndroidManifest.xml的 application标签下，添加 android:name=".BaseApplication"
 * 
 * PS：see fragment lifecycle，use fragmentmanager. enableDebugLogging(true); 
 * @author jlpan
 * 
 */
public class BaseApplication extends Application {
	static String TAG = null;
	PackageManager mPackageManager = null;

	@Override
	public void onCreate() {
		TAG = getAppName(getApplicationContext());
		replaceInstrumentation();
	}

	static public class MyInstrumentation extends Instrumentation {

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			super.callActivityOnCreate(activity, icicle);
			// Activ
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnCreate--");
		}

		@Override
		public void callActivityOnDestroy(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnDestroy(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnDestroy--");
		}

		@Override
		public void callActivityOnNewIntent(Activity activity, Intent intent) {
			// TODO Auto-generated method stub
			super.callActivityOnNewIntent(activity, intent);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnNewIntent--");
		}

		@Override
		public void callActivityOnPause(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnPause(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnPause--");
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			super.callActivityOnPostCreate(activity, icicle);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnPostCreate--");
		}

		@Override
		public void callActivityOnRestart(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnRestart(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnRestart--");
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.callActivityOnRestoreInstanceState(activity,
					savedInstanceState);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnRestoreInstanceState--");
		}

		@Override
		public void callActivityOnResume(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnResume(activity);
			Log.d(TAG, activity.getClass().getSimpleName() + ":" + "--OnResume--");
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity,
				Bundle outState) {
			// TODO Auto-generated method stub
			super.callActivityOnSaveInstanceState(activity, outState);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnSaveInstanceState--");
		}

		@Override
		public void callActivityOnStart(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnStart(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnStart--");
		}

		@Override
		public void callActivityOnStop(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnStop(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnStop--");
		}

		@Override
		public void callActivityOnUserLeaving(Activity activity) {
			// TODO Auto-generated method stub
			super.callActivityOnUserLeaving(activity);
			Log.d(TAG,  activity.getClass().getSimpleName() + ":" + "--OnUserLeaving--");
		}

		@Override
		public void callApplicationOnCreate(Application app) {
			// TODO Auto-generated method stub
			super.callApplicationOnCreate(app);
			Log.d(TAG, app.getPackageName() + ":" + "--OnCreate--");
		}
	}

	/**
	 * �滻ϵͳĬ�ϵ�Instrumentation
	 */
	public static void replaceInstrumentation() {
		Class<?> activityThreadClass;
		try {
			// ����activity thread��class
			activityThreadClass = Class.forName("android.app.ActivityThread");

			// �ҵ�����currentActivityThread
			Method method = activityThreadClass
					.getDeclaredMethod("currentActivityThread");
			// ������������Ǿ�̬�ģ����Դ���Null������
			Object currentActivityThread = method.invoke(null);

			// ��֮ǰActivityThread�е�mInstrumentation�滻�������Լ���
			Field field = activityThreadClass
					.getDeclaredField("mInstrumentation");
			field.setAccessible(true);
			MyInstrumentation mInstrumentation = new MyInstrumentation();
			field.set(currentActivityThread, mInstrumentation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡӦ�ó�������
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [��ȡӦ�ó���汾������Ϣ]
	 * 
	 * @param context
	 * @return ��ǰӦ�õİ汾����
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
