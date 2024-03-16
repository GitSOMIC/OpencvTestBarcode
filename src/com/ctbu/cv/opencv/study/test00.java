package com.ctbu.cv.opencv.study;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class test00 {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }//调用Opencv本地库文件
    public static void main(String[] args) {
//        Mat img = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\2.png");
        //带一个参数,把图像转换为某种图
        Mat img = Imgcodecs.imread("H:\\barCode\\OpencvTest\\imag\\2.png", Imgcodecs.IMREAD_GRAYSCALE);
        Imgcodecs.imwrite("3.png",img);//保存到硬盘,项目根路径
        HighGui.imshow("lena",img);//在屏幕上显示图像,"lena"为任意屏幕名
        HighGui.waitKey(0);//等待x毫秒，0表示等待按任意键


//        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
//
//        Imgcodecs.imwrite("H:\\barCode\\OpencvTest\\imag\\imag01.PNG",img);
//
//        img.release();
        System.out.println("OK!");
    }
}
