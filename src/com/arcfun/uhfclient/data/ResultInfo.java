package com.arcfun.uhfclient.data;

public class ResultInfo {
    /** code */
    private int code;
    /** msg */
    private String msg;
    /** data */
    private String data;

    public ResultInfo() {
    }

    public ResultInfo(int _code, String _msg) {
        code = _code;
        msg = _msg;
    }

    public ResultInfo(int _code, String _msg, String _data) {
        code = _code;
        msg = _msg;
        data = _data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }
}