package com.ctbu.decode;

/**
 * @author Jerry
 * @version 1.0
 */
public class RightCode {
    private int code;

    @Override
    public String toString() {
        return "RightCode{" +
                "code=" + code +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RightCode() {
    }

    public RightCode(int code) {
        this.code = code;
    }
}
