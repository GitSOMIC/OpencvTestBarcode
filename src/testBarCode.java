import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

/**
 * @author Jerry
 * @version 1.0
 */
public class testBarCode {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);

        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));

        System.out.println("OpenCV:" + m);

        Mat srcImage = Imgcodecs.imread("imag/frog.png");//原始

        Mat grayImage = new Mat();//灰色

        Mat gradientXImage = new Mat();

        Mat gradientYImage = new Mat();

        Mat gradientImage = new Mat();

        Mat blurImage = new Mat();//降噪图

        Mat thresholdImage = new Mat();

        Mat morphImage = new Mat();

        Mat resultFileNameSring = new Mat();


        HighGui.imshow("原图", srcImage);
        HighGui.waitKey(0);
        if (srcImage.empty()) {

            System.out.println("image file read error");

            return;

        }

//图片转为灰度图片

        if (srcImage.channels() == 3) {

            Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_RGB2GRAY);

        } else {

            grayImage = srcImage.clone();

        }


        HighGui.imshow("灰色图", grayImage);
        HighGui.waitKey(0);
//建立图像的梯度幅值(滤波器)

        Imgproc.Scharr(grayImage, gradientXImage, CvType.CV_32F, 1, 0);

        Imgproc.Scharr(grayImage, gradientYImage, CvType.CV_32F, 0, 1);

//因为我们需要的条形码在需要X方向水平,所以更多的关注X方向的梯度幅值,而省略掉Y方向的梯度幅值

        Core.subtract(gradientXImage, gradientYImage, gradientImage);

//归一化为八位图像

        Core.convertScaleAbs(gradientImage, gradientImage);

//看看得到的梯度图像是什么样子
        HighGui.imshow("3图", gradientImage);


//对图片进行相应的模糊化,使一些噪点消除

        Imgproc.blur(gradientImage, blurImage, new Size(9, 9));

//模糊化以后进行阈值化,得到到对应的黑白二值化图像,二值化的阈值可以根据实际情况调整

        Imgproc.threshold(blurImage, thresholdImage, 210, 255, Imgproc.THRESH_BINARY);

//看看二值化图像
        HighGui.imshow("4图", thresholdImage);
        HighGui.waitKey(0);


//二值化以后的图像,条形码之间的黑白没有连接起来,就要进行形态学运算,消除缝隙,相当于小型的黑洞,选择闭运算

//因为是长条之间的缝隙,所以需要选择宽度大于长度

        Mat kernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(21, 7));

        Imgproc.morphologyEx(thresholdImage, morphImage, MORPH_CLOSE, kernel);

//看看形态学操作以后的图像
        HighGui.imshow("5图", morphImage);
        HighGui.waitKey(0);


//现在要让条形码区域连接在一起,所以选择膨胀腐蚀,而且为了保持图形大小基本不变,应该使用相同次数的膨胀腐蚀

//先腐蚀,让其他区域的亮的地方变少最好是消除,然后膨胀回来,消除干扰,迭代次数根据实际情况选择

        Imgproc.erode(morphImage, morphImage, Imgproc.getStructuringElement
                (MORPH_RECT, new Size(3, 3)), new Point(-1, -1), 4);

        Imgproc.dilate(morphImage, morphImage, Imgproc.getStructuringElement
                (MORPH_RECT, new Size(3, 3)), new Point(-1, -1), 4);

//看看形态学操作以后的图像

        HighGui.imshow("6图", morphImage);
        HighGui.waitKey(0);


        List contours = new ArrayList();

        List contourArea = new ArrayList();

        Mat hierarchy = new Mat();

//接下来对目标轮廓进行查找,目标是为了计算图像面积

        Imgproc.findContours(morphImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        System.out.println("============" + contours.size());

//计算轮廓的面积并且存放

        for (int i = 0; i < contours.size(); i++) {

            contourArea.add(Imgproc.contourArea((Mat) contours.get(i)));

        }

//找出面积最大的轮廓

        Double maxValue;
        Point maxLoc;

        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(morphImage);
//
// minMaxLoc(contourArea, NULL,&maxValue,NULL,&maxLoc);
//
// minMaxLoc()
//
// Core.MinMaxLocResult
//
// Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(morphImage,morphImage);//Core.minMaxLoc(morphImage);

        maxValue = minMaxLocResult.maxVal;

        maxLoc = minMaxLocResult.maxLoc;//-----------------------

        System.out.println("x:" + maxLoc.x + " y:" + maxLoc.y + " maxVal:" + maxValue);

//

// System.out.println("*********Start*******");

        List newContours = new ArrayList<>();

        for (Object point : contours) {

            MatOfPoint2f newPoint = new MatOfPoint2f(((MatOfPoint) point).toArray());

            newContours.add(newPoint);

        }

//计算面积最大的轮廓的最小的外包矩形

        MatOfPoint2f m2f = (MatOfPoint2f) contours.get((int) maxLoc.x);

        RotatedRect minRect = minAreaRect((MatOfPoint2f) newContours.get((int) maxLoc.x));

//

//

//

//

//

        //为了防止找错,要检查这个矩形的偏斜角度不能超标

        //如果超标,那就是没找到

        if (minRect.angle < 2.0) {

//找到了矩形的角度,但是这是一个旋转矩形,所以还要重新获得一个外包最小矩形

            Rect myRect = boundingRect((Mat) newContours.get((int) maxLoc.x));

// //把这个矩形在源图像中画出来

            rectangle(srcImage, myRect, new Scalar(0, 255, 255), 3, LINE_AA);

// //把这个矩形在源图像中画出来
            HighGui.imshow("7图", srcImage);
            HighGui.waitKey(0);


        }

    }
}
