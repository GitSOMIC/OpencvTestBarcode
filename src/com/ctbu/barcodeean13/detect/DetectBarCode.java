package com.ctbu.barcodeean13.detect;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static jdk.nashorn.internal.objects.NativeMath.max;
import static jdk.nashorn.internal.objects.NativeMath.min;
import static org.opencv.imgproc.Imgproc.*;


/**
 * @author Jerry
 * @version 1.0
 */
public class DetectBarCode {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * 旋转图片
     * @param Image
     */
    public Mat rotate(Mat Image) {
        int width = Image.cols();
        int height = Image.rows();
        //旋转中心点
        Point center = new Point(width / 2, height / 2);
        //旋转30°
        Mat rotationMatrix = getRotationMatrix2D(center, 30, 1);
        Size dSize = new Size(width, height);
        Mat rotatedImage  = new Mat();
        Imgproc.warpAffine(Image,rotatedImage,rotationMatrix,dSize);
        return rotatedImage;
    }

    public Mat getMat(String url){
        Mat srcImag = Imgcodecs.imread(url);
        return srcImag;
    }

    public Mat detect(Mat srcImag) {

//        HighGui.imshow("灰度图片", srcImag);
//        HighGui.waitKey();
        double ratio = srcImag.size().height / 500;
        Mat SrcImagClone = srcImag.clone();


//  将图片变为灰度图片
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImag, grayImage, Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("灰度图片", grayImage);
        HighGui.waitKey();

//使用Sobel算子,求得水平和垂直方向灰度图像的梯度差
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(grayImage, gradX, CvType.CV_32F, 1, 0, -1);
        Imgproc.Sobel(grayImage, gradY, CvType.CV_32F, 0, 1, -1);

        Mat gradientImag = new Mat();
        //gradX和gradY相减
        Core.subtract(gradX, gradY, gradientImag);
        //对像素取绝对值
        Core.convertScaleAbs(gradientImag, gradientImag);

//        HighGui.imshow("梯度图像", gradientImag);
//        HighGui.waitKey();
//          均值滤波，消除高频噪声 (8*8)像素块
        Mat blurImage = new Mat();
        Imgproc.blur(gradientImag, blurImage, new Size(8, 8));
//        HighGui.imshow("均值滤波后的图像(8*8)像素块", blurImage);
//        HighGui.waitKey();
//      二值化
        Mat thresholdImage = new Mat();
        //Imgproc.THRESH_BINARY：当像素值超过阈值thresh时取maxval，否则取0；
        Imgproc.threshold(blurImage, thresholdImage, 210, 255, Imgproc.THRESH_BINARY);
//        HighGui.imshow("二值化图像", thresholdImage);
//        HighGui.waitKey();
//      闭运算 填充条码空隙 （参数需要调整）

        //获取Kernel （10，5）
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 5));  //自己改成5
        Mat morphImage = new Mat();
        //	anchor : 瞄点，默认值为(-1,-1)，代表这个核的中心位置；
        Imgproc.morphologyEx(thresholdImage, morphImage, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1), 2);
//        HighGui.imshow("闭运算图像kernel(10,5)迭代*2", morphImage);
//        HighGui.waitKey();
//      4次腐蚀 再4次膨胀：消除小斑点
//                closed2 = cv2.erode(closed1, None, iterations = 4)
        //获取Kernel （3，3）
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat erodingImage = new Mat();
        Imgproc.erode(morphImage, erodingImage, kernel2, new Point(-1, -1), 4);
        Mat dilatingImage = new Mat();
        Imgproc.dilate(erodingImage, dilatingImage, kernel2, new Point(-1, -1), 4);
//        HighGui.imshow("4次腐蚀 再4次膨胀图像", dilatingImage);
//        HighGui.waitKey();
//      开运算
        Mat morphImage2 = new Mat();
//        Imgproc.threshold(dilatingImage, morphImage2, 1, 255, Imgproc.THRESH_BINARY);
//        HighGui.imshow("二值化图像", morphImage2);
//        HighGui.waitKey();
        //获取Kernel （10，10）
        Mat kernel3 = Mat.ones(new Size(10, 10), CvType.CV_8UC1);
        Imgproc.morphologyEx(dilatingImage, morphImage2, Imgproc.MORPH_OPEN, kernel3, new Point(-1, -1), 2);

//        HighGui.imshow("开运算图像", morphImage2);
//        HighGui.waitKey();
//      腐蚀
        Mat kernel4 = Mat.ones(new Size(5, 5), CvType.CV_8UC1);
        Imgproc.erode(morphImage2, morphImage2, kernel4, new Point(-1, -1), 2);
//        HighGui.imshow("腐蚀图像*2", morphImage2);
//        HighGui.waitKey();
//      Canny找出边界

        Imgproc.Canny(morphImage2, morphImage2, 60, 200);
//        HighGui.imshow("Canny(60,200)", morphImage2);
//        HighGui.waitKey();

        /**
         * 找到轮廓，存储到contours中
         */
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(morphImage2, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        /**
         * 将找到的轮廓contours按大小排序，这里排序方法（sort排序）利用Java内部类的
         */
        contours.sort(new Comparator<MatOfPoint>() {
            @Override
//            contourArea()  获得矩形面积
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return (int) (contourArea(o2) - contourArea(o1));
            }
        });

        //JavaAPI导致的特殊状况  需要类型转换MatOfPoint2f
        List newContours = new ArrayList<>();
        for (Object point : contours) {
            MatOfPoint2f newPoint = new MatOfPoint2f(((MatOfPoint) point).toArray());
            newContours.add(newPoint);
        }
//        for (Object newContour : newContours) {
//            double peri = arcLength((MatOfPoint2f) newContour, true);
//            Mat approx = new Mat();
//            Imgproc.approxPolyDP((MatOfPoint2f)newContour,(MatOfPoint2f)approx,0.02* peri,true);
//        }
//        rect = cv2.minAreaRect(c)
        /**
         * 找到从大到小排序后最大的轮廓的最小外接矩形
         */
        /*
              newContours.get(0)只处理第一个也是排序后最大的一个，
              如果按照排序后的顺序从大到小依次处理，就可以实现识别一张图有多个一维码的情况下的识别
         */
//        minAreaRect()获取最小外接矩形
        RotatedRect minRect = minAreaRect((MatOfPoint2f) newContours.get(0));
        //如果最小外接矩形倾斜小于2° 在我们认为找对了
        if (minRect.angle < 2.0) {      //要求MatOfPoint2f所以上面就在类型转换
//            Rect myRect = boundingRect((Mat) newContours.get(0));
//            boundingRect()获得最小外接矩形的矩形边框
            Rect myRect = minRect.boundingRect();
            //复制一份原图，以便后面的裁剪
            Mat srcImagCloe = srcImag.clone();
            rectangle(srcImagCloe, myRect, new Scalar(0, 255, 255), 3, LINE_AA);
            HighGui.imshow("定位画出矩形框", srcImagCloe);
            HighGui.waitKey(0);
        }


//        double width = minRect.size.width;
//        double height = minRect.size.height;

        /**
         * 计算最小外接矩形的四个点像素坐标
         */
        Mat result = new Mat();
        Imgproc.boxPoints(minRect, result);
        System.out.println(result.dump());
        System.out.println(result.get(0, 1)[0]);
        int x0 = (int) min(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]);
        int y0 = (int) min(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]);
        //不解决旋转问题 x1 y1用不上
//        int x1 = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
//                result.get(3, 0)[0]);
//        int y1 = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
//                result.get(3, 1)[0]);
        int widthd = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]) - x0;
        int heightd = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]) - y0;
        System.out.println("x0:" + x0 + "y0:" + y0 + "width:" + widthd + "height:" + heightd);

//        double barCodeArea1 = minRect.size.width * minRect.size.height;  //一维码区域面积
        double barCodeArea = contourArea((Mat) newContours.get(0)); //一维码区域面积 使用 contourArea函数
        System.out.println(srcImag.size().height + "  " + srcImag.size().width);
//        double srcArea = minRect.size.width * minRect.size.height;
        double srcImagArea = srcImag.size().height * srcImag.size().width;  //源图片区域面积
//        double srcImagArea = contourArea(srcImag);  //源图片区域面积  srcImage是图，contourArea是计算轮廓面积
        /**
         * 按一维码区域占图像比例扩大截取区域x轴方向大小，防止丢失静空区
         */
        double wy = widthd / srcImag.size().width;//一维码区域宽度占比
        double by = barCodeArea / srcImagArea;   //一维码区域占比
        System.out.println("一维码区域占比" + by);
//        int v1 = (int) Math.ceil(by * 100 * 5) ;
        int v = (int) (by * 100 * wy*10);  //by是小数，int强转执行顺序要注意

//        Rect roi = new Rect(x0-50, y0, widthd+100, heightd);
        /**
         * 利用透视变换截取 一维码区域
         */
        Rect roi = new Rect(x0 - v, y0, widthd + v + v, heightd);  //截取的宽度增加一些，因为空白区，没被框取
        Mat dst = new Mat(srcImag, roi);

        HighGui.imshow("一维码区域", dst);
        HighGui.waitKey();
        /**
         * 转成灰度图，二值化 ,
         * 不在定位后灰度化，统一交给，解码阶段处理
         */
//        Mat detGray = new Mat();
//        Imgproc.cvtColor(dst, detGray, Imgproc.COLOR_RGB2GRAY);
//        HighGui.imshow("灰度图片", detGray);
//        HighGui.waitKey();

//        Imgproc.threshold(detGray, detGray, 50, 255, Imgproc.THRESH_BINARY);
//        HighGui.imshow("二值化", detGray);
//        HighGui.waitKey();
//        Imgcodecs.imwrite("Trimming.jpg", detGray);
        return dst;

    }


}
