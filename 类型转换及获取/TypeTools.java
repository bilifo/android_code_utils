package 类型转换及获取;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//待完善
public class TypeTools {

    private static TypeTools mTypeTools = new TypeTools();

    public static TypeTools newInstance() {
        return mTypeTools;
    }

    /**
     * 获得对象的类，并以字符串的形式显示
     *
     * @param obj
     * @return
     */
    public String getTypeString(Object obj) {
        String classString = obj.getClass().toString();
        String type = classString.substring(classString.lastIndexOf('.') + 1, classString.length());
        return type;
    }


    /**
     * 单个object 转 class
     *
     * @param obj
     * @return
     */
    private Class ObjectToClass(Object obj) {
        return obj.getClass();
    }

    /**
     * 多个object 转 class
     *
     * @param objs
     * @return
     */
    private Class[] ObjectToClassS(Object[] objs) {
        Class[] temp = new Class[objs.length];
        for (int i = 0; i < objs.length; i++) {
            temp[i] = objs.getClass();
        }
        return temp;
    }

    /**
     * int型 转成 时分秒的字符串
     *
     * @param value
     * @return
     */
    public String milliToStr(int value) {
        int second = (value / 1000) % 60;
        int mintue = (value / (1000 * 60)) % 60;
        int hour = (value / (1000 * 60)) / 60;
        if (hour == 0) {
            return String.format("%02d:%02d", mintue, second);
        } else {
            return String.format("%02d:%02d:%02d", hour, mintue, second);
        }
    }

    /**
     * 字符串化磁盘大小
     */
    public String calculateFileSize(long fileSize) {
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

    /**
     * dp 转 px
     */
    public int dpTopx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp 转 px
     */
    public int spTopx(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * list 转 set
     */
    public <T> Set<T> listToSet(List<T> list) {
        return new HashSet(list);
    }

    /**
     * set 转 list
     */
    public <T> List<T> setToList(Set<T> set) {
        return new ArrayList(set);
    }

    /**
     * 数组 转 list
     */
    public <T> List<T> arraysToList(T[] arr) {
        return Arrays.asList(arr);
    }

    /**
     * 数组 转 set
     */
    public <T> Set<T> arraysToSet(T[] arr) {
        return new HashSet<T>(arraysToList(arr));
    }

    /**
     * 数组 转 字符串
     */
    public <T> String arraysToStr(T[] mybyte) {
        return Arrays.toString(mybyte);
    }

    /**
     * 16进制字节 转 hex形式字符串
     */
    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v) + " ";
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 用指定分割符分割字符串为字符串数组
     * @param str
     * @param separator
     * @return
     */
    public String[] strToStrarr(String str,String separator){
        return str.split(separator);
    }

    /**
     * 16进制形式的int[] 转 16进制形式的字符串
     */

    /**
     * (单个)16进制形式的字符 转 16进制形式的int
     */
    public int stringToHex(String str){
        return Integer.parseInt(str,16);
    }

    /**
     * (单个)10进制形式的字符 转 10进制形式的int
     */
    public int stringToInt(String str){
        return Integer.parseInt(str,10);
    }

    /**
     * (单个)int 转 asc码字符
     */
    public char intToAsc(int value){
        return (char) value;
    }

    /**
     * (单个)asc码字符 转 int
     */
    public int AscToint(char value){
        return (int) value;
    }

    /**
     * (单个)asc码字符 转 byte
     */
    public byte AscTobyte(int value){
        return (byte) value;
    }

}