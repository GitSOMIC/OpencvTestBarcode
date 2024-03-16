package pers.jerry.ctbu.barcodeean13.pojo.impl;

import pers.jerry.ctbu.barcodeean13.pojo.Code;

/**
 * @author Jerry
 * @version 1.0
 * @Description TODO 为了左区解码，用于存储左区编码规则的普通Java类
 */
public class LeftCode implements Code {

    private int code;
    private String parity;
    private Integer alterCode;

    @Override
    public String toString() {
        return "LeftCode{" +
                "code=" + code +
                ", parity='" + parity + '\'' +
                ", alterCode=" + alterCode +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public Integer getAlterCode() {
        return alterCode;
    }

    public void setAlterCode(Integer alterCode) {
        this.alterCode = alterCode;
    }

    public LeftCode() {
    }

    public LeftCode(int code, String parity, Integer alterCode) {
        this.code = code;
        this.parity = parity;
        this.alterCode = alterCode;
    }

    public LeftCode(int code, String parity) {
        this.code = code;
        this.parity = parity;
    }
}
