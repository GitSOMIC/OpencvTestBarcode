import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.math.BigInteger;
import java.util.*;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.LINE_AA;

/**
 * @author Jerry
 * @version 1.0
 */
public class testBarCode02 {
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
        Mat srcImag = Imgcodecs.imread("imag/barcode_01.webp");

        double ratio = srcImag.size().height / 500;
        Mat SrcImagClone = srcImag.clone();


//                gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)  #将图片变为灰度图片
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImag, grayImage, Imgproc.COLOR_RGB2GRAY);
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
        HighGui.imshow("闭运算*2图像", morphImage);
        HighGui.waitKey();
//#4次腐蚀 再4次膨胀：消除小斑点
//                closed2 = cv2.erode(closed1, None, iterations = 4)
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(3, 3));

        Imgproc.erode(morphImage,morphImage,kernel2,new Point(-1, -1),4);
//        closed3 = cv2.dilate(closed2, None, iterations = 4)
        Imgproc.dilate(morphImage,morphImage,kernel2,new Point(-1,-1),4);
//# images = cv2.resize(closed3,(480,640))
//# cv2.imshow("closed3", images)
//# cv2.waitKey(0)
        HighGui.imshow("4次腐蚀 再4次膨胀图像", morphImage);
        HighGui.waitKey();
//#开运算
        Mat morphImage2 = new Mat();
//                ret,th2=cv2.threshold(closed3,0.1,255,cv2.THRESH_BINARY)
        Imgproc.threshold(morphImage,morphImage2,1,255,Imgproc.THRESH_BINARY);
//        kernel = np.ones((10,10),np.uint8)
        Mat kernel3 = Mat.ones(new Size(10, 10), CvType.CV_8UC1);
//        opening = cv2.morphologyEx(th2, cv2.MORPH_OPEN, kernel,iterations = 2)
        Imgproc.morphologyEx(morphImage2,morphImage2,Imgproc.MORPH_OPEN,kernel3,new Point(-1,-1),2);

//        images = cv2.resize(opening,(480,640))
//        cv2.imshow("contours", images)
//        cv2.waitKey(0)
        HighGui.imshow("开运算图像", morphImage2);
        HighGui.waitKey();
//#腐蚀
//        kernel = np.ones((5,5),np.uint8)
        Mat kernel4 = Mat.ones(new Size(5, 5), CvType.CV_8UC1);
//        erosion = cv2.erode(opening,kernel,iterations = 2)
        Imgproc.erode(morphImage2,morphImage2,kernel4,new Point(-1,-1),2);
//        opening = cv2.resize(erosion ,(480,640))
//        cv2.imshow("contours",opening )
//        cv2.waitKey(0)
        HighGui.imshow("腐蚀图像", morphImage2);
        HighGui.waitKey();
//
//#找出边界

        Imgproc.Canny(morphImage2,morphImage2,60,200);
        HighGui.imshow("source",morphImage2);
        HighGui.waitKey();
//        contours, hierarchy = cv2.findContours(erosion.copy(),cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        List<MatOfPoint> contours = new ArrayList<>();
//        Vector<Vector<Point>> contours2 = new Vector<>();
//        Vector<Vec4d> vec4ds = new Vector<>();
                                        //要求MatOfPoint
        Imgproc.findContours(morphImage2,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
//        contours = cv2.findContours(img_7, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
//        contours = imutils.grab_contours(contours)
//        c = sorted(contours, key = cv2.contourArea, reverse = True)[0]
        contours.sort(new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                return (int) (contourArea(o2) - contourArea(o1));
            }
        });
        List newContours = new ArrayList<>();


//        rect = cv2.minAreaRect(c)
        RotatedRect minRect = minAreaRect((MatOfPoint2f) newContours.get(0));
            if (minRect.angle < 2.0) {      //要求MatOfPoint2f所以上面就在转
//                Rect myRect = boundingRect((Mat) newContours.get(0));
                Rect myRect = minRect.boundingRect();
                rectangle(srcImag, myRect, new Scalar(0, 255, 255), 3, LINE_AA);
                HighGui.imshow("7图", srcImag);
                HighGui.waitKey(0);
            }
        Mat cutMat = new Mat();
        Imgproc.boxPoints(minRect,cutMat);

//        double width = minRect.size.width;
//        double height = minRect.size.height;
//        Size size = minRect.size;
//        double angle = minRect.angle;
//        Point center = minRect.center;
//
        Rect rect = minRect.boundingRect();

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
