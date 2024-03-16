package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Jerry
 * @version 1.0
 */
public class Pyramid {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\boy.png");
        HighGui.imshow("src", src);
        HighGui.waitKey();
        Mat dst = new Mat();

        Imgproc.pyrDown(src, dst);
        HighGui.imshow("down", dst);
        HighGui.waitKey();

        Imgproc.pyrUp(dst, dst);
        HighGui.imshow("up", dst);
        HighGui.waitKey();

        Mat mat = new Mat(src.size(), CvType.CV_64F);

        Core.subtract(dst,src,mat);
    HighGui.imshow("Lap", mat);
        HighGui.waitKey();



    }
}
