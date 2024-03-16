import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @version 1.0
 * Sobel纵向算子提取了图像的水平特征
 * Sobel横向算子提取了图像的竖直特征
 */
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        test1();
    }

    /**
     * Sobel纵向算子提取了图像的水平特征
     * Sobel横向算子提取了图像的竖直特征
     */
    public static void test1() {
        Mat srcImag = Imgcodecs.imread("imag/oneCode/06code128/code12802.png");
        HighGui.imshow("yuan", srcImag);
        HighGui.waitKey();
        double ratio = srcImag.size().height / 500;
        Mat SrcImagClone = srcImag.clone();
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImag, grayImage, Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("灰度图片", grayImage);
        HighGui.waitKey();
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(grayImage, gradX, CvType.CV_32F, 1, 0, -1);
        Imgproc.Sobel(grayImage, gradY, CvType.CV_32F, 0, 1, -1);
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);
        HighGui.imshow("X", gradX);
        HighGui.waitKey();
        HighGui.imshow("Y", gradY);
        HighGui.waitKey();
    }
    public static void test2() {
        Mat srcImag = Imgcodecs.imread("imag/oneCode/ean13/CWSHI.jpg");
        HighGui.imshow("yuan", srcImag);
        HighGui.waitKey();
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImag, grayImage, Imgproc.COLOR_RGB2GRAY);
        Mat thresholdImage = new Mat();
        HighGui.imshow("灰度图片", grayImage);
        HighGui.waitKey();
        Imgcodecs.imwrite("grayImage.jpg", grayImage);
        //Imgproc.THRESH_BINARY：当像素值超过阈值thresh时取maxval，否则取0；
        Imgproc.threshold(grayImage, thresholdImage, 150, 255, Imgproc.THRESH_BINARY);
//        Imgproc.threshold(grayImage, thresholdImage, 190, 255, Imgproc.THRESH_OTSU);
//        Imgproc.threshold(grayImage, thresholdImage, 190, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        HighGui.imshow("二值化图像", thresholdImage);
        HighGui.waitKey();
    }

}
