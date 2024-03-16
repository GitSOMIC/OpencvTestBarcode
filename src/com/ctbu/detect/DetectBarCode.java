package com.ctbu.detect;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static jdk.nashorn.internal.objects.NativeMath.max;
import static jdk.nashorn.internal.objects.NativeMath.min;
import static org.opencv.imgproc.Imgproc.minAreaRect;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-04-16  16:19
 * @Description TODO
 * @since 1.0
 */
public class DetectBarCode {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public static void main(String[] args) {
//    Mat srcImag = Imgcodecs.imread("imag/oneCode/ean13/ean1302.png");
    Mat srcImag = Imgcodecs.imread("imag/oneCode/ean13/CocaCola45.jpg");

        LinkedList<Candidate> detect = new DetectBarCode().detect(srcImag);
        for (Candidate o : detect) {
            Mat cropRect = o.getCropRect();
            HighGui.imshow("1",cropRect);

        }
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    public LinkedList detect(Mat img) {
                /*
                ArrayList array = new ArrayList();
            // pathPy：图片文件路径    图像读取(-1代表原图)
            Mat image = imread(pathPy, -1);
            // 图像行:高度height
            int img_rows = image.rows();
            // 图像列:宽度width
            int img_colums = image.cols();
            // 图像通道:维度dims/channels
            int img_channels = image.channels();
            // 图像像素遍历,按通道输出
            for (int i = 0; i < img_channels; i++) {
                for (int j = 0; j < img_rows; j++) {
                    for (int k = 0; k < img_colums; k++) {
                        array.add(image.get(j, k)[i]);
                    }
                }
         */

//        调整所有读取的图像，调整图像大小以进行规范
        double scalePercent = 640 / img.cols();
        double width = img.cols() * scalePercent;
        double height = img.rows() * scalePercent;
        Mat resized = new Mat();
        Imgproc.resize(img, resized, new Size(width, height), Imgproc.INTER_AREA);
        //灰度化
        Mat gray = new Mat();
        Imgproc.cvtColor(resized, gray, Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("7图", gray);
        HighGui.waitKey(0);
        //二值化
        Mat thresh = new Mat();
//        Imgproc.threshold(gray, thresh, 100, 255, Imgproc.THRESH_BINARY  );
        Imgproc.adaptiveThreshold(gray,thresh,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,11,2);
        HighGui.imshow("7图", thresh);
        HighGui.waitKey(0);
        //取反
        Core.bitwise_not(thresh, thresh);
        HighGui.imshow("7图", thresh);
        HighGui.waitKey(0);
//        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
//                new Size(3, 3));
//        Imgproc.dilate(erodingImage, dilatingImage, kernel2, new Point(-1, -1), 4);
        //膨胀
//        kernel = np.ones((3, 20), np.uint8)
        Mat kernel = Mat.ones(new Size(3, 30), CvType.CV_8UC1);
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 30));
        Imgproc.dilate(thresh, thresh, kernel);
        HighGui.imshow("7图", thresh);
        HighGui.waitKey(0);
        //还原为原来大小
        Mat originalImg = new Mat();
        Imgproc.resize(thresh, originalImg, new Size(img.cols(), img.rows()), Imgproc.INTER_AREA);
        HighGui.imshow("7图", originalImg);
        HighGui.waitKey(0);
        //找到轮廓
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(originalImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        LinkedList<Object> candidates = new LinkedList<>();
        int index = 0;
        List<Integer> addedIndex = new LinkedList();

        List<MatOfPoint2f> newContours = new ArrayList<>();
        for (Object point : contours) {
            MatOfPoint2f newPoint = new MatOfPoint2f(((MatOfPoint) point).toArray());
            newContours.add(newPoint);
        }

        for (MatOfPoint2f cnt : newContours) {
            RotatedRect rotatedRect = minAreaRect(cnt);
            Mat box = new Mat();
            Imgproc.boxPoints(rotatedRect, box);

            Mat cropped = cropRect(rotatedRect, box, img);

            HighGui.imshow("7图", cropped);
            HighGui.waitKey(0);

            int widthCols = cropped.cols();
            int[] a= {3,3,3,3};
            int childIndex = hierarchy.get(0,index,a);
            //EAN13最小需要95个像素进行表示
            if (widthCols>95){
                boolean hasOverlapped = false;

                for (Integer i : addedIndex) {
                    if (i == childIndex){
                        hasOverlapped=true;
                    }
                }
                if (hasOverlapped == false){
                    addedIndex.add(index);
                    Candidate candidate = new Candidate(cropped, rotatedRect);
                    candidates.add(candidate);
                }
            }
        }
        return candidates;
    }

    public Mat cropRect(RotatedRect rotatedRect, Mat result, Mat img) {
        System.out.println(result.dump());
        System.out.println(result.get(0, 1)[0]);
        int x0 = (int) min(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]);
        int y0 = (int) min(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]);
        int x1 = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]);
        int y1 = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]);
        int widthd = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]) - x0;
        int heightd = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]) - y0;
        System.out.println("x0:" + x0 + "y0:" + y0 + "x1:" + x1 + "y1:" + y1 + "width:" + widthd + "height:" + heightd);

//# Center of rectangle in source image
        //将被裁剪的矩形的中心
        Point center = new Point((x0 + x1) / 2, (y0 + y1) / 2);
//          # Size of the upright rectangle bounding the rotated rectangle
        Size size = new Size((x1 - x0), (y1 - y0));
        Mat cropped = new Mat();
        // Cropped upright rectangle
        Imgproc.getRectSubPix(img, size, center, cropped);



        double angle = rotatedRect.angle;
        //不等于90°需要旋转rotation
        if (angle != 90) {
            if (angle > 45) {
                angle = 0 - (90 - angle);
            } else {
                angle = angle;
                System.out.println("angle:" + angle);
            }
            Point point = new Point(size.height / 2, size.width / 2);
            Mat rotationMatrix2D = Imgproc.getRotationMatrix2D(point, angle, 1);

            Imgproc.warpAffine(cropped, cropped, rotationMatrix2D, size);
            int croppedW = heightd > widthd ? heightd : widthd;
            int croppedH = heightd < widthd ? heightd : widthd;
// # Final cropped & rotated rectangle
            center = new Point(size.height / 2, size.width / 2);
            size = new Size(croppedW, croppedH);
            Mat croppedRotated = new Mat();
            Imgproc.getRectSubPix(cropped, size, center, croppedRotated);
            return croppedRotated;
        }
        return cropped;
    }
}
