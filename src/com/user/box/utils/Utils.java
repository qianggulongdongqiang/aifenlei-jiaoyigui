package com.user.box.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_WEIGHT = 1;
    public static final int TYPE_EPC = 2;
    public static final int LENGTH_EPC = 24;
    public static final int LENGTH_RAW_RFID = 33;
    public static final int LENGTH_RAW_WEIGHT = 13;

    public static final int TRANS_WEIGHT = 0;
    public static final int TRANS_PIECES = 1;

    /** Baud rate */
    public static final int BAUD_RATE_DEF = 9600;
    public static final int BAUD_RATE_UHF = 115200;

    /** Serial Port */
    public static final String SERIAL_PROT_1 = "/dev/ttyS1";
    public static final String SERIAL_PROT_2 = "/dev/ttyS3";

    public static int MAX_GRID_VIEW_NUM = 9;
    public static int NUMBER_COLUMNS = 3;

    private static final String PHONE_NUMBER_REGEX =
            "^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$";

    public static String decode(String bytes) {
        String hexString = "0123456789ABCDEF";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    public static String formatPhoneNumber(String args) {  
        Matcher matcher = Pattern.compile(PHONE_NUMBER_REGEX).matcher(args);  
        if (matcher.find()) {  
            return matcher.group().replaceAll("(?<=[\\d]{3})\\d(?=[\\d]{4})", "*");  
        }  
        return args;  
    }
}