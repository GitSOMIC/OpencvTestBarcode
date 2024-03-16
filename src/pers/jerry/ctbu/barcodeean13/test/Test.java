package pers.jerry.ctbu.barcodeean13.test;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pers.jerry.ctbu.barcodeean13.decode.impl.DecodeEAN13;
import pers.jerry.ctbu.barcodeean13.detect.impl.DetectBarCode;

import static org.opencv.imgproc.Imgproc.LINE_AA;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-04-16  20:26
 * @Description TODO 对整个项目进行测试，不使用junit
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        //条形码定位类
        DetectBarCode detectBarCode = new DetectBarCode();
        //ean13解码类
        DecodeEAN13 decodeEAN13 = new DecodeEAN13();
        String url = "imag/oneCode/ean13/Bookdiannao.jpg";
        Mat source = detectBarCode.getMat(url);
//        imag/oneCode/ean13/Deluxe.jpg


        Mat sourceClone = source;
        for (int i = 0; i < 6; i++) {
            //获得定位到并截取的一维码图像
            Mat detect = detectBarCode.detect(source);
            //使用Ean-13的解码器进行解码
            decodeEAN13.decode(detect);
            //如果解码成功就结束程序
            if (decodeEAN13.isIsSuccess() == true) {
                //在源图像上画出ean13解码结果
                Imgproc.putText(source, decodeEAN13.putEan13True(), new Point(100, 900), 3, 3, new Scalar(0, 255, 255), 4);
                rectangle(source, detectBarCode.getPyke(), new Scalar(0, 255, 255), 3, LINE_AA);
                HighGui.imshow("final", source);
                Imgcodecs.imwrite("final.jpg", source);
                HighGui.waitKey();
                System.out.println("解码结束");
                System.exit(0);
//                return true;
            }
            //解码失败就旋转30°，i<3只转2次，还是解码不出来就判定失败结束程序
            //解码失败就旋转30°，i<6只转5次，还是解码不出来就判定失败结束程序
            source = detectBarCode.rotate(source);
            System.out.println("定位解码，第" + i + "次解码失败,进行第" + i + "次旋转");
        }

        System.out.println("定位->解码结束,直接解码开始...");


        for (int i = 0; i < 3; i++) {
            decodeEAN13.decode(sourceClone);
            if (decodeEAN13.isIsSuccess() == true) {
                System.out.println("解码结束");
                System.exit(0);
//                return true;
            }
            sourceClone = detectBarCode.rotate(sourceClone);
            System.out.println("直接解码，第" + i + "次解码失败,进行第" + i + "次旋转");
        }
//        return false;
    }
}
