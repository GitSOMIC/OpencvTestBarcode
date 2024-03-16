package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Jerry
 * @version 1.0
 */
public class test01 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat eye = Mat.ones(3, 3, CvType.CV_8UC1);
        System.out.println(eye.dump());//dump()


    }
}
