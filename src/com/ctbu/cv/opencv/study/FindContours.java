package com.ctbu.cv.opencv.study;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @version 1.0
 */
public class FindContours {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("imag/boy.png", Imgcodecs.IMREAD_GRAYSCALE);

        Mat gray = new Mat();
        Imgproc.Canny(src,gray,60,200);
        HighGui.imshow("source",gray);
        HighGui.waitKey();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(gray,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat target = new Mat(gray.height(), gray.width(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(target,contours,i,new Scalar(0,0,0),3);
        }

        HighGui.imshow("contours",target);
        HighGui.waitKey();
    }
}
