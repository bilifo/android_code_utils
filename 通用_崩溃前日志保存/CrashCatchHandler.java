package com.dense.kuiniu.dense_frame.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.dense.kuiniu.dense_frame.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 崩溃日志抓取
 * <p>
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告
 * 使用方法:
 * 1/复制该类到项目
 * 2/在Application类的onCreat里面启用该类
 * CrashCatchHandler crashCatchHandler = CrashCatchHandler.getInstance();//获得单例
 * crashCatchHandler.init(getApplicationContext());//初始化,传入context
 * 3/给予写权限<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <p>
 * 这里是博客地址：http://blog.csdn.net/liuhe688/article/details/6584143#
 *
 * @author liuhe688
 * @一些改动 waka
 */
public class CrashCatchHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private static final CrashCatchHandler INSTANCE = new CrashCatchHandler();// 单例模式
    private Context context;
    private Thread.UncaughtExceptionHandler defaultHandler;// 系统默认的UncaughtException处理类
    private Map<String, String> infosMap = new HashMap<String, String>(); // 用来存储设备信息和异常信息

    /**
     * 私有构造方法，保证只有一个CrashHandler实例
     */
    private CrashCatchHandler() {

    }

    /**
     * 获取CrashHandler，单例模式
     *
     * @return
     */
    public static CrashCatchHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置当前CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && defaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            defaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "exception : ", e);
                e.printStackTrace();
            }
            // 杀死进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return 如果处理了该异常信息, 返回true;否则返回false.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast显示异常信息
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "程序出现未捕获的异常，即将退出！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        collectDeviceInfo(context);// 收集设备参数信息
        saveCrashInfoToFile(ex);// 保存日志文件

        return true;
    }

    /**
     * 收集设备信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        // 使用包管理器获取信息
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // TODO 在这里得到包的信息
                String versionName = pi.versionName == null ? "" : pi.versionName;// 版本名;若versionName==null，则="null"；否则=versionName
                String versionCode = pi.versionCode + "";// 版本号
                infosMap.put("versionName", versionName);
                infosMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an NameNotFoundException occured when collect package info");
            e.printStackTrace();
        }

        // 使用反射获取获取系统的硬件信息
        Field[] fields = Build.class.getDeclaredFields();// 获得某个类的所有申明的字段，即包括public、private和proteced，
        for (Field field : fields) {
            field.setAccessible(true);// 暴力反射 ,获取私有的信息;类中的成员变量为private,故必须进行此操作
            try {
                infosMap.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "an IllegalArgumentException occured when collect reflect field info", e);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an IllegalAccessException occured when collect reflect field info", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("CommitPrefEdits")
    private String saveCrashInfoToFile(Throwable ex) {
        // 字符串流
        StringBuffer stringBuffer = new StringBuffer();

        // 获得设备信息
        for (Map.Entry<String, String> entry : infosMap.entrySet()) {// 遍历map中的值
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(key + "=" + value + "\n");
        }

        // 获得错误信息
        Writer writer = new StringWriter();// 这个writer下面还会用到，所以需要它的实例
        PrintWriter printWriter = new PrintWriter(writer);// 输出错误栈信息需要用到PrintWriter
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        if (cause != null) {// 循环，把所有的cause都输出到printWriter中
            cause.printStackTrace(printWriter);
            cause = ex.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuffer.append(result);

        // 写入文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appData = MyApplication.getContext().getDataDir().getPath();// /data/data/包名/

        //创建父文件夹
        File file = new File(appData + "/crash");
        if (!file.exists()) {
            file.mkdirs();// 如果不存在，则创建所有的父文件夹
        }
        else{//没做日志上传,所以先做一个日志限额,满10个日志,清除时间靠前的日志
            File[] childs=file.listFiles();
            if(childs.length>10){
                long[] lastTime=new long[childs.length];//排序修改最后时间
                Map<String,File> tmp=new HashMap<>();//key:最后修改时间
                for(int i=0;i<childs.length;i++){
                    lastTime[i]=childs[i].lastModified();
                    tmp.put(String.valueOf(childs[i].lastModified()),childs[i]);
                }
                Arrays.sort(lastTime);
                //从后向前,删除时间靠前file
                for (int i=lastTime.length-11;i<lastTime.length-10&&i>=0;i--){
                    long lasttime=lastTime[i];
                    File rmFile=tmp.get(String.valueOf(lasttime));
                    rmFile.delete();
                    tmp.remove(String.valueOf(lasttime));
                }
            }else{

            }
        }
        //log存放路径
        String crashFileName = file.getPath() + "/crash_" + simpleDateFormat.format(new Date()) + ".log";

        try {
            FileOutputStream fos = new FileOutputStream(crashFileName);
            fos.write(stringBuffer.toString().getBytes());
            fos.close();

            //TODO 在这里可以将文件名写入sharedPreferences中，方便下一次打开程序时对错误日志进行操作
            /*SharedPreferences.Editor editor = mContext
                    .getSharedPreferences("waka").edit();
            editor.putString("lastCrashFileName", crashFileName);
            editor.commit();*/


            return crashFileName;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "an FileNotFoundException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "an IOException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        }
        return null;
    }

}
