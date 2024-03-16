package com.ctbu.decode;

/**
 * @author Jerry
 * @version 1.0
 */
public class LeftCode {

    private int code;
    private String parity;

    public LeftCode() {
    }

    public LeftCode(int code, String parity) {
        this.code = code;
        this.parity = parity;
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

    @Override
    public String toString() {
        return "LeftCode{" +
                "code=" + code +
                ", parity='" + parity + '\'' +
                '}';
    }
}
