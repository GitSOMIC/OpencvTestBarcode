package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Jerry
 * @version 1.0
 */
public class AddWeighted {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }//调用Opencv本地库文件

    public static void main(String[] args) {
        Mat src1 = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\2.png");
        Mat src2 = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\1.png");
        Mat dst = new Mat();
//        Core.addWeighted(src1,0.5,src2,0.5,0,dst);//加权平均
//        Core.add(src1,src2,dst);//简单相加，大于255，为255，255为白色
        Mat mat = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\lena.jpg");
        Core.bitwise_not(mat,dst);//图像取反
        HighGui.imshow("mixed",dst);
        HighGui.waitKey(0);
    }
}
