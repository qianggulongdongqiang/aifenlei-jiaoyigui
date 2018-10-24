package com.arcfun.uhfclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class QrCode implements Parcelable {
    /** code */
    private int code;
    /** msg */
    private String msg;
    /** ticket */
    private String ticket;
    /** url */
    private String url;
    /** getImg */
    private String getImg;

    public QrCode(int _code, String _msg) {
        this.code = _code;
        this.msg = _msg;
    }

    public QrCode(int _code, String _msg, String _ticket, String _url,
            String _getImg) {
        this.code = _code;
        this.msg = _msg;
        this.ticket = _ticket;
        this.url = _url;
        this.getImg = _getImg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGetImg() {
        return getImg;
    }

    public void setGetImg(String getImg) {
        this.getImg = getImg;
    }

    public static final Parcelable.Creator<QrCode> CREATOR = new Creator<QrCode>() {
        @Override
        public QrCode createFromParcel(Parcel source) {
            return new QrCode(source);
        }

        @Override
        public QrCode[] newArray(int size) {
            return new QrCode[size];
        }
    };

    protected QrCode(Parcel source) {
        code = source.readInt();
        msg = source.readString();
        ticket = source.readString();
        url = source.readString();
        getImg = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(msg);
        dest.writeString(ticket);
        dest.writeString(url);
        dest.writeString(getImg);
    }

    @Override
    public String toString() {
        return "[code=" + code + ", ticket=" + ticket + ", url=" + url + "]";
    }
}