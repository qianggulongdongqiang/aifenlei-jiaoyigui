package com.user.box.data;

public class MessageInfo {
    private int _type;
    private String _userName;
    private String _phoneNum;
    private String _managerId;

    private String _category;
    private String _weight;
    private int _piece;
    private float _income;

    public MessageInfo(int type, String name, String phone, String id,
            String category, String weight, int piece, float income) {
        _type = type;
        _userName = name;
        _phoneNum = phone;
        _managerId = id;
        _category = category;
        _weight = weight;
        _piece = piece;
        _income = income;
    }

    public int getType() {
        return _type;
    }

    public void setType(int _type) {
        this._type = _type;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String _userName) {
        this._userName = _userName;
    }

    public String getPhoneNum() {
        return _phoneNum;
    }

    public void setPhoneNum(String _phoneNum) {
        this._phoneNum = _phoneNum;
    }

    public String getManagerId() {
        return _managerId;
    }

    public void setManagerId(String _managerId) {
        this._managerId = _managerId;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String _category) {
        this._category = _category;
    }

    public String getWeight() {
        return _weight;
    }

    public void setWeight(String _weight) {
        this._weight = _weight;
    }

    public int getPiece() {
        return _piece;
    }

    public void setPiece(int _piece) {
        this._piece = _piece;
    }

    public float getIncome() {
        return _income;
    }

    public void setIncome(float _income) {
        this._income = _income;
    }

}