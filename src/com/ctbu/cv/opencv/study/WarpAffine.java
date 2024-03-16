package com.ctbu.cv.opencv.study;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Jerry
 * @version 1.0
 */
public class WarpAffine {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\lena.jpg");

        Mat dst = new Mat();
        Point[] pt1 = new Point[3];//输入图像焦点

        pt1[0] = new Point(0, 0);
        pt1[1] = new Point(300, 0);
        pt1[2] = new Point(0, 640);
        Point[] pt2 = new Point[3];//输出图像焦点

        pt2[0] = new Point(0, 0);
        pt2[1] = new Point(300, 0);
        pt2[2] = new Point(320, 640);

        MatOfPoint2f mop1 = new MatOfPoint2f(pt1);
        MatOfPoint2f mop2 = new MatOfPoint2f(pt2);

        Mat mat = Imgproc.getAffineTransform(mop1, mop2);//计算仿射变换2*3矩阵

        Imgproc.warpAffine(src,dst,mat,src.size());

        HighGui.imshow("lena2",dst);
        HighGui.waitKey(0);


    }
}
