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
public class testBarCode06 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {


//import cv2
//import numpy as  np
//import math
//        from pyzbar.pyzbar import decode
//
//#读图
//                img=cv2.imread(r'XX.png',1) #读取图片
        Mat srcImag = Imgcodecs.imread("imag/oneCode/ean13/CocaCola45.jpg");
        HighGui.imshow("灰度图片", srcImag);
        HighGui.waitKey();
        double ratio = srcImag.size().height / 500;
        Mat SrcImagClone = srcImag.clone();


//                gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)  #将图片变为灰度图片
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImag, grayImage, Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("灰度图片", grayImage);
        HighGui.waitKey();
//
//#使用Sobel算子,求得水平和垂直方向灰度图像的梯度差
//        gradX = cv2.Sobel(gray,ddepth = cv2.CV_32F,dx = 1,dy = 0,ksize = -1)
//        gradY = cv2.Sobel(gray,ddepth = cv2.CV_32F,dx = 0,dy = 1,ksize = -1)
//                                        CvType.CV_32F
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(grayImage, gradX, CvType.CV_32F, 1, 0, -1);
        Imgproc.Sobel(grayImage, gradY, CvType.CV_32F, 0, 1, -1);

//        gradient = cv2.subtract(gradX, gradY)
        Mat gradientImag = new Mat();
        Core.subtract(gradX, gradY, gradientImag);
//        gradient = cv2.convertScaleAbs(gradient)
        Core.convertScaleAbs(gradientImag, gradientImag);

//        images = cv2.resize(gradient,(480,640))
        //改变图像尺寸干嘛？？
//        Imgproc.resize(gradientImag, gradientImag, new Size(480, 640));

//        cv2.imshow("closed1", images)
//        cv2.waitKey(0)
        HighGui.imshow("梯度图像", gradientImag);
        HighGui.waitKey();
//#均值滤波，消除高频噪声 (8*8)像素块
//                blurred = cv2.blur(gradient,(8,8))
        Mat blurImage = new Mat();
        Imgproc.blur(gradientImag, blurImage, new Size(8, 8));
        HighGui.imshow("均值滤波后的图像(8*8)像素块", blurImage);
        HighGui.waitKey();
//#二值化
//                ret,thresh = cv2.threshold(blurred, 225, 255, cv2.THRESH_BINARY)
        Mat thresholdImage = new Mat();
//        	Imgproc.THRESH_BINARY：当像素值超过阈值thresh时取maxval，否则取0；
        Imgproc.threshold(blurImage, thresholdImage, 210, 255, Imgproc.THRESH_BINARY);
        HighGui.imshow("二值化图像", thresholdImage);
        HighGui.waitKey();
//#闭运算 填充条码空隙 （参数自己调整）
//        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(25,25))
//        closed1 = cv2.morphologyEx(thresh,cv2.MORPH_CLOSE,kernel, iterations = 2)
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(10, 5));  //自己改成5
        Mat morphImage = new Mat();
        //	anchor : 瞄点，默认值为(-1,-1)，代表这个核的中心位置；
        Imgproc.morphologyEx(thresholdImage, morphImage, Imgproc.MORPH_CLOSE, kernel, new Point(-1, -1), 2);


//# images = cv2.resize(closed1,(480,640))
        //改变图像尺寸干嘛？？
        // Mat resizedMorphImage = new Mat();
        // Imgproc.resize(morphImage,resizedMorphImage,new Size(480,640));
        // Imgproc.resize(morphImage, morphImage, new Size(480, 640));

//# cv2.imshow("closed1", images)
//# cv2.waitKey(0)
        HighGui.imshow("闭运算图像kernel(10,5)迭代*2", morphImage);
        HighGui.waitKey();
//#4次腐蚀 再4次膨胀：消除小斑点
//                closed2 = cv2.erode(closed1, None, iterations = 4)
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(3, 3));
        Mat erodingImage = new Mat();
        Imgproc.erode(morphImage, erodingImage, kernel2, new Point(-1, -1), 4);
//        closed3 = cv2.dilate(closed2, None, iterations = 4)
        Mat dilatingImage = new Mat();
        Imgproc.dilate(erodingImage, dilatingImage, kernel2, new Point(-1, -1), 4);
//# images = cv2.resize(closed3,(480,640))
//# cv2.imshow("closed3", images)
//# cv2.waitKey(0)

        HighGui.imshow("4次腐蚀 再4次膨胀图像", dilatingImage);
        HighGui.waitKey();
//#开运算
        Mat morphImage2 = new Mat();
//                ret,th2=cv2.threshold(closed3,0.1,255,cv2.THRESH_BINARY)
//                                                Imgproc.threshold(dilatingImage, morphImage2, 1, 255, Imgproc.THRESH_BINARY);
//                                        //        kernel = np.ones((10,10),np.uint8)
//                                                HighGui.imshow("二值化图像", morphImage2);
//                                                HighGui.waitKey();
        Mat kernel3 = Mat.ones(new Size(10, 10), CvType.CV_8UC1);
//        opening = cv2.morphologyEx(th2, cv2.MORPH_OPEN, kernel,iterations = 2)
        Imgproc.morphologyEx(dilatingImage, morphImage2, Imgproc.MORPH_OPEN, kernel3, new Point(-1, -1), 2);

//        images = cv2.resize(opening,(480,640))
//        cv2.imshow("contours", images)
//        cv2.waitKey(0)
        HighGui.imshow("开运算图像", morphImage2);
        HighGui.waitKey();
//#腐蚀
//        kernel = np.ones((5,5),np.uint8)
        Mat kernel4 = Mat.ones(new Size(5, 5), CvType.CV_8UC1);
//        erosion = cv2.erode(opening,kernel,iterations = 2)
        Imgproc.erode(morphImage2, morphImage2, kernel4, new Point(-1, -1), 2);
//        opening = cv2.resize(erosion ,(480,640))
//        cv2.imshow("contours",opening )
//        cv2.waitKey(0)
        HighGui.imshow("腐蚀图像*2", morphImage2);
        HighGui.waitKey();
//
//#找出边界

        Imgproc.Canny(morphImage2, morphImage2, 60, 200);
        HighGui.imshow("Canny(60,200)", morphImage2);
        HighGui.waitKey();
//        contours, hierarchy = cv2.findContours(erosion.copy(),cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        List<MatOfPoint> contours = new ArrayList<>();
//        Vector<Vector<Point>> contours2 = new Vector<>();
//        Vector<Vec4d> vec4ds = new Vector<>();
        //要求MatOfPoint
        Imgproc.findContours(morphImage2, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        /**
         * 找到轮廓
         */

//        contours = cv2.findContours(img_7, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
//        contours = imutils.grab_contours(contours)
//        c = sorted(contours, key = cv2.contourArea, reverse = True)[0]
        /**
         * 找到轮廓按大小排序
         */
        contours.sort(new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return (int) (contourArea(o2) - contourArea(o1));
            }
        });

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
        RotatedRect minRect = minAreaRect((MatOfPoint2f) newContours.get(0));
        if (minRect.angle < 2.0) {      //要求MatOfPoint2f所以上面就在转
//                Rect myRect = boundingRect((Mat) newContours.get(0));
            Rect myRect = minRect.boundingRect();
            Mat srcImagCloe = srcImag.clone();
            rectangle(srcImagCloe, myRect, new Scalar(0, 255, 255), 3, LINE_AA);
            HighGui.imshow("7图", srcImagCloe);
            HighGui.waitKey(0);
        }


//        double width = minRect.size.width;
//        double height = minRect.size.height;

////#图形旋转矫正 并截取最大边界区域
////#修改最小框大小 放大50
////        rect = cv2.minAreaRect(c)
//        double width = minRect.size.width + 50;
//        double height = minRect.size.height ;
////        b = list(rect)
//
////        b[1] = list(b[1])
////        b[1][0] = b[1][0] + 50  宽
////        b[1][1] = b[1][1] + 50  高
////        new_rect = tuple(b)
////        box = np.int0(cv2.boxPoints(new_rect))

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
//        int x1 = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
//                result.get(3, 0)[0]);
//        int y1 = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
//                result.get(3, 1)[0]);
        int widthd = (int) max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]) - x0;
        int heightd = (int) max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]) - y0;
        System.out.println("x0:" + x0 + "y0:" + y0 + "width:" + widthd + "height:" + heightd);

        double barCodeArea1 = minRect.size.width * minRect.size.height;  //一维码区域面积
        double barCodeArea = contourArea((Mat) newContours.get(0)); //一维码区域面积
        System.out.println(srcImag.size().height + "  " + srcImag.size().width);
//        double srcArea = minRect.size.width * minRect.size.height;
        double srcImagArea = srcImag.size().height * srcImag.size().width;  //源图片区域面积
//        double srcImagArea = contourArea(srcImag);  //源图片区域面积  srcImage是图，contourArea是计算轮廓面积
        /**
         * 按一维码区域占图像比例扩大截取区域x轴方向大小
         */
        double by = barCodeArea / srcImagArea;   //一维码区域占比
        System.out.println("一维码区域占比" + by);
//        int v1 = (int) Math.ceil(by * 100 * 5) ;
        int v = (int) (by * 100 * 10);  //by是小数，int强转执行顺序要注意

//        Rect roi = new Rect(x0-50, y0, widthd+100, heightd);
        /**
         * 利用透视变换截 一维码区域
         */
        Rect roi = new Rect(x0 - v, y0, widthd + v + v, heightd);  //截取的宽度增加一些，因为空白区，没被框取
        Mat dst = new Mat(srcImag, roi);

        HighGui.imshow("T", dst);
        HighGui.waitKey();
        /**
         * 转成灰度图，二值化
         */
        Mat detGray = new Mat();
        Imgproc.cvtColor(dst, detGray, Imgproc.COLOR_RGB2GRAY);
        HighGui.imshow("灰度图片", detGray);
        HighGui.waitKey();

//        Imgproc.threshold(detGray, detGray, 50, 255, Imgproc.THRESH_BINARY);
//        HighGui.imshow("二值化", detGray);
//        HighGui.waitKey();
        Imgcodecs.imwrite("Trimming.jpg", detGray);


        //--------------------------------------调用Zxing解码
        Image i = null;//二维码或条形码的图片
        try {
            //能否不去内存中读取
            i = ImageIO.read(new FileInputStream("Trimming.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<DecodeHintType, String> m = new HashMap<>();//二维码或条形码的参数
        m.put(DecodeHintType.CHARACTER_SET, "utf-8");//设置编码
        Result s = null;//二维码或条形码
        try {
            s = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource((BufferedImage) i))), m);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(s.getText());//输出二维码或条形码的内容
        System.out.println(s.getBarcodeFormat());//输出扫描到得是二维码还是条形码

//        MultiFormatOneDReader  一维码解码
//        try {
//            Result decode = new MultiFormatOneDReader(m).decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource((BufferedImage) i))));
//        } catch (NotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (FormatException e) {
//            throw new RuntimeException(e);
//        }


////#draw_img = cv2.drawContours(img.copy(), [box], -1, (0, 0, 255), 3)
////# 获取画框宽高(x=orignal_W,y=orignal_H)
////        orignal_W = math.ceil(np.sqrt((box[3][1] - box[2][1])**2 + (box[3][0] - box[2][0])**2))
//
//            Math.ceil(box.)


////        orignal_H= math.ceil(np.sqrt((box[3][1] - box[0][1])**2 + (box[3][0] - box[0][0])**2))
////# 原图中的四个顶点,与变换矩阵
////        pts1 = np.float32([box[0], box[1], box[2], box[3]])
////        pts2 = np.float32([[int(orignal_W+1),int(orignal_H+1)], [0, int(orignal_H+1)], [0, 0], [int(orignal_W+1), 0]])
////# 生成透视变换矩阵；进行透视变换
////                M = cv2.getPerspectiveTransform(pts1, pts2)
////        result_img = cv2.warpPerspective(img, M*1.5, (int(orignal_W+20),int(orignal_H+20)))
////        cv2.imshow("JIAOZHEN",result_img)
////        cv2.waitKey(0)

        /*
def drow_box(img, cnt):
    rect_box = cv2.boundingRect(cnt)
    rotated_box = cv2.minAreaRect(cnt)

    cv2.rectangle(img, (rect_box[0], rect_box[1]), (rect_box[0] + rect_box[2], rect_box[1] + rect_box[3]), (0, 255, 0), 2)

    box = cv2.boxPoints(rotated_box)
    box = np.int0(box)
    cv2.drawContours(img, [box], 0, (0, 0, 255), 2)

    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    # plt.imshow(img)
    # plt.show()

    return img, rotated_box, box

    def crop1(img, cnt):
    horizon = True

    img, rotated_box, _ = drow_box(img, cnt)

    center, size, angle = rotated_box[0], rotated_box[1], rotated_box[2]
    center, size = tuple(map(int, center)), tuple(map(int, size))

    print(angle)

    if horizon:
        if size[0] < size[1]:
            angle -= 270
            w = size[1]
            h = size[0]
        else:
            w = size[0]
            h = size[1]
        size = (w, h)

    height, width = img.shape[0], img.shape[1]

    M = cv2.getRotationMatrix2D(center, angle, 1)
    img_rot = cv2.warpAffine(img, M, (width, height))
    img_crop = cv2.getRectSubPix(img_rot, size, center)

    show([img, img_rot, img_crop])
 */


//        box = cv2.cv.BoxPoints(rect) if imutils.is_cv2() else cv2.boxPoints(rect)
//        box = np.int0(box)
//        cv2.drawContours(img, [box], -1, (0,255,0), 20)

//        Optional<MatOfPoint> max = contours.stream().max(new Comparator<MatOfPoint>() {------------max
//            @Override
//            public int compare(MatOfPoint o1, MatOfPoint o2) {
//                return (int) (contourArea(o1) - contourArea(o2));
//            }
//        });

//        MatOfPoint m2f = contours.get((int) maxLoc.x);
//        for (int i = newContours.size()-1; i !=0 ; i--) {
//            RotatedRect minRect = minAreaRect((MatOfPoint2f) newContours.get((int) i));
//            if (minRect.angle < 2.0) {      //要求MatOfPoint2f所以上面就在转
//                Rect myRect = boundingRect((Mat) newContours.get((int) i));
//                rectangle(srcImag, myRect, new Scalar(0, 255, 255), 3, LINE_AA);
//                HighGui.imshow("7图", srcImag);
//                HighGui.waitKey(0);
//            }
//
//        }
        //法二
//        Mat target = new Mat(morphImage2.height(), morphImage2.width(), CvType.CV_8UC3, new Scalar(255, 255, 255));
//        for (int i = 0; i < contours.size(); i++) {
//            Imgproc.drawContours(morphImage2,contours,i,new Scalar(255,0,0),3);
//        }
//
//        HighGui.imshow("contours",morphImage2);
//        HighGui.waitKey();

        //法yi
//        for (int i = 0; i < contours.size(); i++) {
//
////            Rect rect=boundingRect((Mat)contours[i]);
//            Rect rect = Imgproc.boundingRect((Mat)contours.get(i));
////            rectangle(image,rect,Scalar(255),2);
//            Imgproc.rectangle(morphImage2,rect,new Scalar(0,0,255),2);
//        }
//        HighGui.imshow("框出图", morphImage2);
//        HighGui.waitKey();

//
//#获取最大轮廓
/**
 * sorted(iterable, cmp=None, key=None, reverse=False)
 * 参数说明：
 *
 * iterable -- 可迭代对象。
 * cmp -- 比较的函数，这个具有两个参数，参数的值都是从可迭代对象中取出，此函数必须遵守的规则为，大于则返回1，小于则返回-1，等于则返回0。
 * key -- 主要是用来进行比较的元素，只有一个参数，具体的函数的参数就是取自于可迭代对象中，指定可迭代对象中的一个元素来进行排序。
 * reverse -- 排序规则，reverse = True 降序 ， reverse = False 升序（默认）。
 */
//        c = sorted(contours, key = cv2.contourArea, reverse = True)[0]

//
//#图形旋转矫正 并截取最大边界区域
//#修改最小框大小 放大50
//        rect = cv2.minAreaRect(c)
//        b = list(rect)

//        b[1] = list(b[1])
//        b[1][0] = b[1][0] + 50
//        b[1][1] = b[1][1] + 50
//        new_rect = tuple(b)
//        box = np.int0(cv2.boxPoints(new_rect))
//#draw_img = cv2.drawContours(img.copy(), [box], -1, (0, 0, 255), 3)
//# 获取画框宽高(x=orignal_W,y=orignal_H)
//        orignal_W = math.ceil(np.sqrt((box[3][1] - box[2][1])**2 + (box[3][0] - box[2][0])**2))
//        orignal_H= math.ceil(np.sqrt((box[3][1] - box[0][1])**2 + (box[3][0] - box[0][0])**2))
//# 原图中的四个顶点,与变换矩阵
//        pts1 = np.float32([box[0], box[1], box[2], box[3]])
//        pts2 = np.float32([[int(orignal_W+1),int(orignal_H+1)], [0, int(orignal_H+1)], [0, 0], [int(orignal_W+1), 0]])
//# 生成透视变换矩阵；进行透视变换
//                M = cv2.getPerspectiveTransform(pts1, pts2)
//        result_img = cv2.warpPerspective(img, M*1.5, (int(orignal_W+20),int(orignal_H+20)))
//        cv2.imshow("JIAOZHEN",result_img)
//        cv2.waitKey(0)

//#二值化 筛掉一些错误
//        ret,thresh = cv2.threshold(result_img,128, 255, cv2.THRESH_BINARY)
//#二维码编码检测
//                barcodes = decode(thresh)
//        result = []
//        for barcode in barcodes:
//        barcodeData = barcode.data.decode("utf-8")
//        result.append(barcodeData)
//        print(result)
//

    }


}
