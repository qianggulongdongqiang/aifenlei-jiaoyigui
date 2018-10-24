package com.arcfun.uhfclient.data;


/**
 * api/order/addOrder.html
 *
 */
public class ResultOrderInfo {
    /** 0=fail[description];1=success;2=fail[not login]  */
    
    private int code;
    private String order_sn;
    private long add_time;
    private int points_number;
    private String collecter_name;
    private String collecter_id;

    private String goods_id;
    private String goods_name;
    private String goods_num;
    private int goods_point;

    private int buyer_id;
    private String buyer_name;
    private String buyer_addr;
    private String buyer_phone;
    private int order_from;
    private int id;
    private int user_score;

    public ResultOrderInfo(int code, long timestamp, int point, int score) {
        this.code = code;
        this.add_time = timestamp;
        this.points_number = point;
        this.user_score = score;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getOrdersn() {
        return order_sn;
    }
    public void setOrdersn(String order_sn) {
        this.order_sn = order_sn;
    }
    public long getTimeStamp() {
        return add_time;
    }
    public void setTimeStamp(long add_time) {
        this.add_time = add_time;
    }
    public int getPointsnumber() {
        return points_number;
    }
    public void setPointsnumber(int points_number) {
        this.points_number = points_number;
    }
    public String getCollectername() {
        return collecter_name;
    }
    public void setCollectername(String collecter_name) {
        this.collecter_name = collecter_name;
    }
    public String getCollecterid() {
        return collecter_id;
    }
    public void setCollecterid(String collecter_id) {
        this.collecter_id = collecter_id;
    }
    public String getGoodsid() {
        return goods_id;
    }
    public void setGoodsid(String goods_id) {
        this.goods_id = goods_id;
    }
    public String getGoodsname() {
        return goods_name;
    }
    public void setGoodsname(String goods_name) {
        this.goods_name = goods_name;
    }
    public String getGoodsnum() {
        return goods_num;
    }
    public void setGoodsnum(String goods_num) {
        this.goods_num = goods_num;
    }
    public int getGoodspoint() {
        return goods_point;
    }
    public void setGoodspoint(int goods_point) {
        this.goods_point = goods_point;
    }
    public int getBuyerid() {
        return buyer_id;
    }
    public void setBuyerid(int buyer_id) {
        this.buyer_id = buyer_id;
    }
    public String getBuyername() {
        return buyer_name;
    }
    public void setBuyername(String buyer_name) {
        this.buyer_name = buyer_name;
    }
    public String getBuyeraddr() {
        return buyer_addr;
    }
    public void setBuyeraddr(String buyer_addr) {
        this.buyer_addr = buyer_addr;
    }
    public String getBuyerphone() {
        return buyer_phone;
    }
    public void setBuyerphone(String buyer_phone) {
        this.buyer_phone = buyer_phone;
    }
    public int getOrderfrom() {
        return order_from;
    }
    public void setOrderfrom(int order_from) {
        this.order_from = order_from;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserScore() {
        return user_score;
    }

    public void setUserScore(int user_score) {
        this.user_score = user_score;
    }
    
}