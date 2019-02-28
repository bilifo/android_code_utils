package 类型转换及获取;

import android.content.Context;

public class TypeTools {

	/**
	 * 获得对象的类，并以字符串的形式显示
	 * @param obj
	 * @return
	 */
	public String getTypeString(Object obj){
		String	classString=obj.getClass().toString();
		String	type=classString.substring(classString.lastIndexOf('.')+1,classString.length());
		return type;
	}
	

	/**
	 * 单个object转class
	 * @param obj
	 * @return
	 */
	private Class ObjectToClass(Object obj){
		return obj.getClass();
	}
	/**
	 * 多个object转class
	 * @param objs
	 * @return
	 */
	private Class[] ObjectToClassS(Object[] objs){
		Class[] temp=new Class[objs.length];
		for(int i=0;i<objs.length;i++){
			temp[i]=objs.getClass();
		}
		return temp;
	}
	
	/**
	 * int型转成时分秒的字符串
	 * @param value
	 * @return
	 */
    public static String milli2Str(int value) {
        int second = (value / 1000) % 60;
        int mintue = (value / (1000 * 60)) % 60;
        int hour = (value / (1000 * 60)) / 60;
        if (hour == 0) {
            return String.format("%02d:%02d", mintue, second);
        } else {
            return String.format("%02d:%02d:%02d", hour, mintue, second);
        }
    }
    
    //字符串化磁盘大小
    public static String calculateFileSize(long fileSize) {
        String sizeStr = "";
        long k = fileSize / 1024 % 1024;
        long m = fileSize / (1024 * 1024) % 1024;
        long g = fileSize / (1024 * 1024 * 1024);
        if (g == 0) {
            sizeStr = String.format("%sM", m);
        } else {
            sizeStr = String.format("%sG", g);
        }
        return sizeStr;
    }
    
    //dp转px
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //sp转px
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
