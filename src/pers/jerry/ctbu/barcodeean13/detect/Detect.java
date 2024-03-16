package pers.jerry.ctbu.barcodeean13.detect;

import org.opencv.core.Mat;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-05-02  16:48
 * @Description TODO
 * @since 1.0
 */
public interface Detect {
    Mat detect(Mat srcImag);
}
