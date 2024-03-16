package pers.jerry.ctbu.barcodeean13.pojo.impl;

import pers.jerry.ctbu.barcodeean13.pojo.Code;

/**
 * @author Jerry
 * @version 1.0
 * @Description TODO 用于保存结果的普通Java类
 */
public class Result implements Code {
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
