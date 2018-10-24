package com.arcfun.uhfclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class OwnerInfo implements Parcelable {
    /** id */
    private int id;
    /** user_nickname */
    private String nickName;
    /** sex */
    private int sex;
    /** birthday */
    private int birthday;
    /** score */
    private int score;
    /** mobile */
    private String mobile;
    /** vendor */
    private float vendor = 0;

    public OwnerInfo(int id, String name, int score, String phone) {
        this(id, name, score, 0, score, phone);
    }

    public OwnerInfo(int id, String name, int sex, int birth, int score,
            String phone) {
        this.id = id;
        this.nickName = name;
        this.sex = sex;
        this.birthday = birth;
        this.score = score;
        this.mobile = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public float getVendor() {
        return vendor;
    }

    public void setVendor(float vendor) {
        this.vendor = vendor;
    }

    public static final Parcelable.Creator<OwnerInfo> CREATOR = new Creator<OwnerInfo>() {
        @Override
        public OwnerInfo createFromParcel(Parcel source) {
            return new OwnerInfo(source);
        }

        @Override
        public OwnerInfo[] newArray(int size) {
            return new OwnerInfo[size];
        }
    };

    protected OwnerInfo(Parcel source) {
        id = source.readInt();
        nickName = source.readString();
        score = source.readInt();
        mobile = source.readString();
        vendor = source.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nickName);
        dest.writeInt(score);
        dest.writeString(mobile);
        dest.writeFloat(vendor);
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + nickName + ", mobile=" + mobile
                + ", score=" + score + "]";
    }
}