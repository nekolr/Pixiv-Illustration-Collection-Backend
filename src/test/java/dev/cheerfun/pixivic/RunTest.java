package dev.cheerfun.pixivic;

import java.math.BigInteger;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {

        String strA="7905495048182386429";
        String strB="9097472708293611413";
        String strAB=StrToBinstr(strA);
        String strBB=StrToBinstr(strB);
        //对两个二进制字符串，字符数相同，统计差异数
        int count=0;
        int len=strAB.length();
        for(int i=0;i<len;i++){
            if(strAB.charAt(i)!=strBB.charAt(i)) count++;
        }
        System.out.println("海明距离是："+count);
    }

    //将字符串转换成二进制字符串，以空格相隔
    public static String StrToBinstr(String str) {
        String result = new BigInteger(str, 10).toString(2);
       /* char[] strChar=str.toCharArray();
        String result="";
        for(int i=0;i<strChar.length;i++){
            result +=Integer.toBinaryString(strChar[i]);
        }*/
        System.out.println(result);
        return result;
    }

}
