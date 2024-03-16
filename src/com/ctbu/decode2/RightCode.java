package com.ctbu.decode2;

/**
 * @author Jerry
 * @version 1.0
 */
public class RightCode implements Code {
    private int code;

    private Integer alterCode;

    @Override
    public String toString() {
        return "RightCode{" +
                "code=" + code +
                ", alterCode=" + alterCode +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Integer getAlterCode() {
        return alterCode;
    }

    public void setAlterCode(Integer alterCode) {
        this.alterCode = alterCode;
    }

    public RightCode(int code) {
        this.code = code;
    }

    public RightCode() {
    }

    public RightCode(int code, Integer alterCode) {
        this.code = code;
        this.alterCode = alterCode;
    }

}
