package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Jerry
 * @version 1.0
 */
public class DetectEdge {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("imag/boy.png",Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("src", src);
        HighGui.waitKey();

        Mat sobel = new Mat();
        Imgproc.Sobel(src,sobel,-1,0,1);
        HighGui.imshow("sobel",sobel);
        HighGui.waitKey();

        Mat scharr = new Mat();
        Imgproc.Scharr(src,scharr,-1,0,1);
        HighGui.imshow("scharr",scharr);
        HighGui.waitKey();

        Mat gaussianBlur = new Mat();
        Imgproc.GaussianBlur(src,gaussianBlur,new Size(31,5),80,3);
        HighGui.imshow("gaussianBlur",gaussianBlur);
        HighGui.waitKey();

        Mat laplacian = new Mat();
        Imgproc.Laplacian(src,laplacian,0);
        HighGui.imshow("laplacian",laplacian);
        HighGui.waitKey();

        Mat canny = new Mat();
        Imgproc.Canny(src,canny,60,200);
        HighGui.imshow("canny",canny);
        HighGui.waitKey();

        System.out.println("ok");




    }
}
