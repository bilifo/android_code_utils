package GE11可借鉴的一些方法;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;

public class ToolsUtils {
	/**
     * 判断某个Activity 界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * 利用反射获得存储设备的UUID
     * @param context
     * @param volumePath
     * @return
     */
    public static String getVolumeUUID(Context context, String volumePath) {
        String uuid = "";
        if (!TextUtils.isEmpty(volumePath)) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            try {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                getVolumeList.setAccessible(true);
                Object volumes = getVolumeList.invoke(storageManager);
                if (volumes != null && volumes instanceof Object[]) {
                    for (Object volume : (Object[]) volumes) {
                        Class storageVolume = Class.forName("android.os.storage.StorageVolume");
                        Method getPath = storageVolume.getMethod("getPath");
                        Object pathOb = getPath.invoke(volume);
                        if (pathOb != null) {
                            String path = pathOb.toString();
                            if (path.equals(volumePath)) {
                                Method getUuid = storageVolume.getMethod("getUuid");
                                Object uuidOb = getUuid.invoke(volume);
                                if (uuidOb != null) {
                                    uuid = uuidOb.toString();
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return uuid;
    }
    
   
    
    
}
