package com.ctbu.decode;

/**
 * @author Jerry
 * @version 1.0
 */
public enum enumLeftCode {
    O0(0,"O"),E0(0,"E");
    private int code;
    private String parity;

    enumLeftCode(int code, String parity) {
        this.code = code;
        this.parity = parity;
    }
}
