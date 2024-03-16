package com.ctbu.barcodeean13.test;

import com.ctbu.barcodeean13.decode.DecodeEAN13;
import com.ctbu.barcodeean13.detect.DetectBarCode;
import org.opencv.core.Mat;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-04-16  20:26
 * @Description TODO
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        //条形码定位类
        DetectBarCode detectBarCode = new DetectBarCode();
        //ean13解码类
        DecodeEAN13 decodeEAN13 = new DecodeEAN13();

//        Mat source = detectBarCode.getMat("imag/oneCode/ean13/FaceWash.jpg");
        Mat source = detectBarCode.getMat("imag/oneCode/ean13/CocaCola45.jpg");
        Mat sourceClone = source;
//        Mat source = detectBarCode.getMat("imag/oneCode/ean13/barcode_01.webp");
        for (int i = 0; i < 3; i++) {
            //获得定位到并截取的一维码图像
            Mat detect = detectBarCode.detect(source);
            //使用Ean-13的解码器进行解码
            decodeEAN13.decode(detect);
            //如果解码成功就结束程序
            if (decodeEAN13.isIsSuccess() == true) {
                System.out.println("解码结束");
                System.exit(0);
//                return true;
            }
            //解码失败就旋转30°，i<3只转2次，还是解码不出来就判定失败结束程序
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
