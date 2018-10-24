package com.arcfun.uhfclient.data;

public class MessageInfo {
    private int user_id;
    private int _id;
    private int _type;
    private String _userName;
    private String _phoneNum;
    private String _managerId;
    private String _category;
    private int oScore;

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    private String _weight;
    private int _piece;
    private int _income;

    public MessageInfo(GoodInfo info, int userId, String name, String phone,
            int score, String category, String weight, int piece, int income) {
        _id = info.getId();
        _type = info.getType();
        user_id = userId;
        _userName = name;
        _phoneNum = phone;
        oScore = score;
        _managerId = info.getManger();
        _category = category;
        _weight = weight;
        _piece = piece;
        _income = income;
    }

    public MessageInfo(GoodInfo info, OwnerInfo owner,
            String category, String weight, int piece, int income) {
        _id = info.getId();
        _type = info.getType();
        user_id = owner.getId();
        _userName = owner.getNickName();
        _phoneNum = owner.getMobile();
        _managerId = info.getManger();
        _category = category;
        _weight = weight;
        _piece = piece;
        _income = income;
        oScore = owner.getScore();
    }

    public void updateGoodInfo(GoodInfo info) {
        _type = info.getType();
        
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

    public int getIncome() {
        return _income;
    }

    public void setIncome(int _income) {
        this._income = _income;
    }

    public int getScore() {
        return oScore;
    }

    public void setScore(int oScore) {
        this.oScore = oScore;
    }

}