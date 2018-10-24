package com.arcfun.uhfclient.data;

public class ResultDataInfo {
    /** 0=fail[description];1=success;2=fail[not login]  */
    private int code;
    private int type;
    private long timestamp;
    private String account;
    private String phone;
    private String manager;
    private String success_before;
    private String success_income;
    private String success_after;
    private String fail_category;
    private String fail_weight;
    private int fail_piece;
    private int fail_credit;

    public ResultDataInfo(MessageInfo info, int code, long timestamp,
            String success1, String success2, String success3) {
        this.code = code;
        this.type = info.getType();
        this.timestamp = timestamp;
        this.account = info.getUserName();
        this.phone = info.getPhoneNum();
        this.manager = info.getManagerId();
        this.success_before = success1;
        this.success_income = success2;
        this.success_after = success3;
        this.fail_category = info.getCategory();
        this.fail_weight = info.getWeight().trim();
        this.fail_piece = info.getPiece();
        this.fail_credit = info.getIncome();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSuccessBefore() {
        return success_before;
    }

    public void setSuccessBefore(String success_before) {
        this.success_before = success_before;
    }

    public String getSuccessIncome() {
        return success_income;
    }

    public void setSuccessIncome(String success_income) {
        this.success_income = success_income;
    }

    public String getSuccessAfter() {
        return success_after;
    }

    public void setSuccessAfter(String success_after) {
        this.success_after = success_after;
    }

    public String getFailCategory() {
        return fail_category;
    }

    public void setFailCategory(String fail_category) {
        this.fail_category = fail_category;
    }

    public String getFailWeight() {
        return fail_weight;
    }

    public void setFailWeight(String fail_weight) {
        this.fail_weight = fail_weight;
    }

    public int getFailPiece() {
        return fail_piece;
    }

    public void setFailPiece(int fail_piece) {
        this.fail_piece = fail_piece;
    }

    public int getFailCredit() {
        return fail_credit;
    }

    public void setFailCredit(int fail_credit) {
        this.fail_credit = fail_credit;
    }
}