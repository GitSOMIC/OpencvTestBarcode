package com.ctbu.test;

import com.ctbu.decode2.DecodeEAN13;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.HashSet;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-04-16  12:41
 * @Description TODO
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        DecodeEAN13 decodeEAN13 = new DecodeEAN13();
//        Mat mat = decodeEAN13.getMat("imag/oneCode/ean13/1760131.png");
//        decodeEAN13.decode(mat);


//        System.out.println(eachLines.size());
//        System.out.println(eachLines);

        Mat mat = decodeEAN13.getMat("imag/barcode_01.webp");
        Mat grayImage = new Mat();
        Imgproc.cvtColor(mat, grayImage, Imgproc.COLOR_RGB2GRAY);

        Imgproc.adaptiveThreshold(grayImage,grayImage,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,2,2);
        HighGui.imshow("1",mat);
        HighGui.waitKey();
    }

}
