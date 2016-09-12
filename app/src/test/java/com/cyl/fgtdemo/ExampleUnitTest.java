package com.cyl.fgtdemo;

import com.cyl.fgtdemo.model.app.GlobalData;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public  void maintest() {
        byte[]oupbuf={(byte) 0xEF,(byte) 0x01,(byte) 0x01,(byte) 0x00,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte)0x02,(byte)0x00,(byte) 0x7c,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x01};
   //    int sb= 64;
      int sb= CRC_XModem(oupbuf);
        System.out.println(sb);
      byte[] abc= changeByte(sb);
        for (int i=0;i<abc.length;i++){
            System.out.println(String.format("0x%02x", abc[i]));
        }
/*       int sb1 = 1231875;
        int year = ((sb1) >> 26);
        int month = ((sb1) >> 22) & 0x000f;
        int date = ((sb1) >> 17) & 0x001f;
        int hour = ((sb1) >> 12) & 0x001f;
        int min = ((sb1) >> 6) & 0x003f;
        int sec = (sb1) & 0x003f;
        System.out.println(String.format("%04d-%02d-%02d %02d:%02d:%02d", year + 2010, month, date, hour, min, sec)) ;*/
       // byte  devsn[]=new byte[]{(byte)0x9d,(byte)0x00,0x00,0x00};
/*        byte[] devsn={0x08,0x00,0x00,0x00};
       int sb= byteToInt2(devsn);
        System.out.println(sb);*/
/*        byte[] oupbuf=new byte[2];
        int st=2;
        System.arraycopy(changeByte(st),0, oupbuf, 0, 2);
        for (int i=0;i<oupbuf.length;i++){
            System.out.println(String.format("0x%02x", oupbuf[i]));
        }*/

/*        System.out.println(sb);
        System.out.println(String.format("0x%04x", sb));*/

/*        System.out.println(String.format("0x%02x", bytes[0]));
        System.out.println(String.format("0x%02x", bytes[1]));*/
/*        byte[] sm={(byte)0xd0,(byte)0xdc,(byte)0xc0,(byte)0xa4,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        String str = null;
        try {
            str = new String(sm,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(str);*/
      //  System.out.println(toHexString(sm));
    }

    public static int byteToInt2(byte[] b) {

        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i=3;i>=0;i--){
            n<<=8;
            temp=b[i]&mask;
            n|=temp;
        }
        return n;
    }
    public static int CRC_XModem(byte[] bytes){
        int crc = 0x00;          // initial value
        int polynomial = 0x1021;
        for (int index = 0 ; index< bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }
    private byte[] changeByte(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = { b1, b2, b3, b4 };
        return bytes;
    }

/*    public byte[] changeByte(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = { b1, b2, b3, b4 };
        byte[] bytes1={0x00,0x00,(byte)0xff,(byte)0xff};
        byte[]bytes2=new byte[4];
        bytes2[0]=(byte)(bytes[0]|0x00);
        bytes2[1]=(byte)(bytes[1]|0x00);
        bytes2[2]=(byte)(bytes[2]|0xff);
        bytes2[3]=(byte)(bytes[3]|0xff);
        return bytes2;
    }*/

    public static String toHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }
        public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static String str2Hexstr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }
}