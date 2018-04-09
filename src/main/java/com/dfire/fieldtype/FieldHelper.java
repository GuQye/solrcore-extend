package com.dfire.fieldtype;

/**
 * @author 粟谷 sugu@2dfire.com
 * @date 2018年03月20日 下午7:01:51
 */
public class FieldHelper {
    public static boolean isCharOrNum(char a) {
        int v = a;
        boolean isNum = (v >= 48) && (v <= 57);
        boolean isHighCase = (v >= 65) && (v <= 90);
        boolean isLowCase = (v >= 97) && (v <= 122);
        return isNum || isHighCase || isLowCase;
    }

    public static boolean isChinese(char a) {
        int v = a;
        return (v >= 19968) && (v <= 171941);
    }

    public static int processInt(int old,char fc, int i,int BitsPerValue) {
        int position = (Spell.NumOfValue-i -1) * BitsPerValue;
        /** a~z : 1~26  0~9:27~36 特殊字符:42*/
        int num = (fc>='0' && fc<='9')?(fc-'0'+27):(fc-'a'+1);
        old = (num << position) | old;
        return old;
    }

    public static void main(String[] args) {
        System.out.println(processInt(0,'9',1,5));
    }
}