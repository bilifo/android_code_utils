package 通用_Log打印;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自动打印时间和方法名的log
 * 使用方式:
 * 		LogActs.d("sss");
 * @author PanJunLong
 *
 */
@SuppressWarnings("unused")
public final class LogActs {
    private static final String regex = "vdo-->";
    public static boolean LOG_ALL = true;
    public static boolean LOG_V = true;
    public static boolean LOG_D = true;
    public static boolean LOG_I = true;
    public static boolean LOG_W = true;
    public static boolean LOG_E = true;
    /**
     * 获取文件名
     *
     * @return
     */
    public static String getFileName() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getFileName();
    }

    /**
     * 获取函数名
     *
     * @return
     */
    public static String getFunctionName() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }

    /**
     * 获取行号
     *
     * @return
     */
    public static int getLineNumber() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getLineNumber();
    }

    /**
     * 获取时间
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        Date now = new Date(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(now);
    }


    private static String buildLog() {
        StackTraceElement[] stacktrace = (new Exception()).getStackTrace();// Thread.currentThread().getStackTrace();
        StackTraceElement traceElement = stacktrace[2];
        StringBuilder stringBuilder = new StringBuilder()
                .append(traceElement.getFileName()).append("/")
                .append(traceElement.getMethodName()).append("()/")
                .append(traceElement.getLineNumber()).append(regex);
        String TAG = stringBuilder.toString();
        return TAG;
    }

    public static void v(Object obj) {
        if (LOG_ALL && LOG_V) {
            String msg=getString(obj);
            String TAG = buildLog();
            Log.v(TAG, msg);
        }
    }

    public static void d(Object obj) {
        if (LOG_ALL && LOG_D) {
            String msg=getString(obj);
            String TAG = buildLog();
            Log.d(TAG, msg);
        }
    }

    public static void i(Object obj) {
        if (LOG_ALL && LOG_I) {
            String msg=getString(obj);
            String TAG = buildLog();
            Log.i(TAG, msg);
        }
    }

    public static void w(Object obj) {
        if (LOG_ALL && LOG_W) {
            String msg=getString(obj);
            String TAG = buildLog();
            Log.w(TAG, msg);
        }
    }

    public static void e(Object obj) {
        if (LOG_ALL && LOG_E) {
            String msg=getString(obj);
            String TAG = buildLog();
            Log.e(TAG, msg);
        }
    }

    public static void a(Object obj) {
        if (LOG_ALL && LOG_E) {
            String msg=getString(obj);
            String TAG = buildLog();
            logAll(TAG, msg);
        }
    }

    private static final int LOG_MAXLENGTH = 2000;

    public static void logAll(String tag, Object obj) {
        String msg=getString(obj);
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                Log.d(tag + "-" + i,  msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.d(tag + "-" + i, msg.substring(start, strLength));
                break;
            }
        }
    }

    private static String getString(Object obj) {
        String msg = "" + obj;
        if(TextUtils.isEmpty(msg)){
            msg=" ";
        }
        return msg;
    }
}
