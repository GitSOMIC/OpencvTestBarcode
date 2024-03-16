package com.ctbu.barcodeean13.decode.pojo.impl;

/**
 * @author Jerry
 * @version 1.0
 */
public class Result {
    private String ean13;
    private boolean isValid;

    public String getEan13() {
        return ean13;
    }

    public void setEan13(String ean13) {
        this.ean13 = ean13;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Result() {
    }

    public Result(String ean13, boolean isValid) {
        this.ean13 = ean13;
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "Result{" +
                "ean13='" + ean13 + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
