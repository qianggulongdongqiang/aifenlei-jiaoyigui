package com.reader.base;

public class CMD {
    public final static byte HEAD = (byte) 0x3c;
    public final static byte BEG = (byte) 0x58;
    public final static byte END = (byte) 0x68;
    public final static byte FIX = (byte) 0xA7;
    public final static byte INFINIX = (byte) 0xB0;
    public final static byte SINGLE = (byte) 0xB1;
    public final static byte RESPONSE = (byte) 0x05;

    public final static String NAME_OPEN_HOUSE = "01";
    public final static String NAME_CLOSE_HOUSE = "02";
    public final static String NAME_UNSOLICETD_WEIGHT = "03";
    public final static String NAME_UNSOLICETD_SYNC = "04";

    public final static byte TYPE_OPEN_HOUSE = 0x01;
    public final static byte TYPE_CLOSE_HOUSE = 0x02;
    public final static byte TYPE_UNSOLICETD_WEIGHT = 0x03;
    public final static byte TYPE_UNSOLICETD_SYNC = 0x04;
}