package com.ctbu.cv.opencv.study;

import com.sun.crypto.provider.TlsKeyMaterialGenerator;
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
public class Rotate {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }//调用Opencv本地库文件

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\lena.jpg");
        HighGui.imshow("lena1",src);
        HighGui.waitKey(0);
        Mat dst = src.clone();//克隆一个一样的图像
        //设置一个点，坐标是这个图像的宽的1/2，高的1/2
        Point center = new Point(src.width() / 2.0, src.height() / 2.0);
        //                                             旋转点，选择角度，缩放比例
        Mat affineTrans = Imgproc.getRotationMatrix2D(center, 33.0, 1.0);
        Imgproc.warpAffine(src,dst,affineTrans,dst.size(),Imgproc.INTER_NEAREST);
        HighGui.imshow("lena2",dst);
        HighGui.waitKey(0);
    }
}
