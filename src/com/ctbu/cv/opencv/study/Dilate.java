package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Jerry
 * @version 1.0
 */
public class Dilate {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\letter.png");
        HighGui.imshow("src",src);
        HighGui.waitKey();
        Mat dst = new Mat();

//        Imgproc.erode(src,dst,new Mat());
//        HighGui.imshow("erode",dst);
//        HighGui.waitKey();
//
//        Imgproc.dilate(src,dst,new Mat());
//        HighGui.imshow("dilate",dst);
//        HighGui.waitKey();

//        Imgproc.morphologyEx(src,dst,Imgproc.MORPH_OPEN,new Mat(),new Point(-1,-1),3);
        Imgproc.morphologyEx(src,dst,Imgproc.MORPH_GRADIENT,new Mat());
        HighGui.imshow("Morph",dst);
        HighGui.waitKey();
    }
}
