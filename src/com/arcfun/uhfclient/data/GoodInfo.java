package com.arcfun.uhfclient.data;

import com.arcfun.uhfclient.utils.Utils;

/**
 * api/common/getSecGoods.html
 *
 */
public class GoodInfo {
    /**id*/
    private int id;
    /**name*/
    private String name;
    /**unit_name*/
    private String unit_name;
    /**unit*/
    private String unit;
    /**purchasing_price*/
    private float price;
    /**purchasing_point*/
    private float point;
    /**img_1*/
    private String image_1;
    /**img_2*/
    private String image_2;
    /**SH00011*/
    private String manger = "回收员a";
    public int getType() {
        if (unit_name.equals("重量")) {
            return Utils.TRANS_WEIGHT;
        } else {
            return Utils.TRANS_PIECES;
        }
    }
    public String getManger() {
        return manger;
    }

    public void setManger(String manger) {
        this.manger = manger;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GoodInfo(int id, String name, String uint_name, String unit, float price, float point,
            String image_1, String image_2) {
        this.id = id;
        this.name = name;
        this.unit_name = uint_name;
        this.unit = unit;
        this.price = price;
        this.point = point;
        this.image_1 = image_1;
        this.image_2 = image_2;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public float getPoint() {
        return point;
    }
    public void setPoint(float point) {
        this.point = point;
    }
    public String getImage_1() {
        return image_1;
    }
    public void setImage_1(String image_1) {
        this.image_1 = image_1;
    }
    public String getImage_2() {
        return image_2;
    }
    public void setImage_2(String image_2) {
        this.image_2 = image_2;
    }
    @Override
    public String toString() {
        return "[id=" + id + ",name=" + name + ",u_name=" +
                unit_name + ",unit=" + unit + ",price=" + price +
                ",point=" + point + ",imag1=" + image_1 +
                ",imag2=" + image_2 + "]";
    }
}