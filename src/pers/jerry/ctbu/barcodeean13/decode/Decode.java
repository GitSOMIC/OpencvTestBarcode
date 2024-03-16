package pers.jerry.ctbu.barcodeean13.decode;

import org.opencv.core.Mat;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-05-02  16:43
 * @Description TODO
 * @since 1.0
 */
public interface Decode {
    void decode(Mat detect);
}
