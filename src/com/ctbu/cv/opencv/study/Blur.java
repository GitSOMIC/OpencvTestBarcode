package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Jerry
 * @version 1.0
 */
public class Blur {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\lena.jpg");
        Mat dst = new Mat();
        HighGui.imshow("lena1",src);
        HighGui.waitKey();

        Imgproc.cvtColor(src,src,Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("lena2",src);
        HighGui.waitKey();

        Mat mat = new Mat();
        Imgproc.threshold(src,mat,125,255,0);//Imgproc.THRESH_BINARY=0
        HighGui.imshow("thread",mat);
        HighGui.waitKey();

    }
}
