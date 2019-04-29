package com.dense.kuiniu.dense_frame.serialport;

import java.util.Arrays;

/**
 * Created by ls on 2018/4/25 0025 09:13
 * 数据转换工具
 */
public class SerialTool {
    //-------------------------------------------------------
    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    static public int isOdd(int num) {
        return num & 0x1;
    }

    //-------------------------------------------------------
    static public int HexToInt(String inHex)//Hex字符串转int
    {

        return Integer.parseInt(inHex, 16);
    }

    public static String byteToString(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return String.valueOf(b & 0xFF);
    }

    public static int byteToint(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    //-------------------------------------------------------
    static public byte HexToByte(String inHex)//Hex字符串转byte
    {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    static public String Byte2Hex(byte inByte)//1字节转2个Hex(16进制)字符
    {
        return String.format("%02x", inByte).toUpperCase();
    }

    public static String hexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    //-------------------------------------------------------
    static public String ByteArrToHex(byte[] inBytArr)//字节数组转转hex字符串
    {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    static public String ByteArrToHex(byte[] inBytArr, int offset, int byteCount)//字节数组转转hex字符串，可选长度
    {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    //转hex字符串转字节数组
    static public byte[] HexToByteArr(String inHex)//hex字符串转字节数组
    {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    /**
     * 计算并获得16CRC
     *
     * @param bytes
     * @return
     */
    public static byte[] getCRC(byte[] bytes, int length) {
        int crc = CRC16.calcCrc16(bytes, 0, length);
        //高低位互换，输出符合相关工具对Modbus CRC16的运算
        //String a = String.format("%04X", crc);
        byte[] a = intToByteArray(crc);
        if (a[0] == -17) {
            a[0] = 0x00;
        }
        if (a[1] == -17) {
            a[1] = 0x00;
        }
        if (a[2] == -17) {
            a[2] = 0x00;
        }
        if (a[3] == -17) {
            a[3] = 0x00;
        }
        return a;
    }

    private static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] ByteTobit(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    public static byte[] ByteTobits(byte[] a) {

        byte[] array = new byte[24];
        for (int c = 0; c < 3; c++) {
            byte temp = a[c];
            for (int i = 7; i > -1; i--) {
                array[c * 8 + i] = (byte) ((temp >> (i)) & 0x1);
            }
        }
        return array;
    }


    /**
     * java 16进制求和
     */
    public static String makeChecksum(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return "00";
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return "00";
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        return String.valueOf(total);//得10进制和
        //  return hexInt(total);
    }

    public static byte[] inToBytes(byte[] inBytArr, int num) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        String datas = strBuilder.toString();
        //  String nums = datas.substring(0, datas.length() - 6);//去除最后两个byte
        int sum = Integer.parseInt(SerialTool.makeChecksum(datas));
        byte[] array = new byte[2];
        int H = sum >> 8;
        int L = sum & 0x00FF;
        array[0] = (byte) L;
        array[1] = (byte) H;
        return array;
    }

    /**
     * pjl++ 返回byte[]数组的hex形式字符串
     */
    public static String retune_HEX_String(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(SerialTool.Byte2Hex(data[i]) + " ");
        }
        return sb.toString();
    }

    /**
     * 两个确定数组合并
     */
    public static byte[] arraysMerge(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * pjl++ 查询一个数组是否包含另一个数组
     * @param searched
     * @param find
     * @param start
     * @return
     */
    public static int byteArrIndexOf(byte[] searched, byte[] find, int start) {
        boolean matched = false;
        int end = find.length - 1;
        int skip = 0;
        for (int index = start; index <= searched.length - find.length; ++index) {
            matched = true;
            if (find[0] != searched[index] || find[end] != searched[index + end]) continue;
            else skip++;
            if (end > 10)
                if (find[skip] != searched[index + skip] || find[end - skip] != searched[index + end - skip])
                    continue;
                else skip++;
            for (int subIndex = skip; subIndex < find.length - skip; ++subIndex) {
                if (find[subIndex] != searched[index + subIndex]) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return index;
            }
        }
        return -1;
    }
}

