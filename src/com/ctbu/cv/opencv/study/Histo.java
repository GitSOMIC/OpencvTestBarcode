package com.ctbu.cv.opencv.study;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jerry
 * @version 1.0
 */
public class Histo {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("imag/lena.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("src", src);
        HighGui.waitKey();

        Mat dst = new Mat();

//        Imgproc.equalizeHist(src,dst);
//        HighGui.imshow("dst", dst);
//        HighGui.waitKey();


        List<Mat> matList = new LinkedList<>();
        matList.add(dst);

        float[] range = {0, 256};
        MatOfFloat histRange = new MatOfFloat(range);

        Mat Hist = new Mat();
        Imgproc.calcHist(matList, new MatOfInt(0), new Mat(), Hist, new MatOfInt(256), histRange);

        int wide = 512;
        int high = 400;
        Mat histImage = new Mat(high, wide, CvType.CV_8UC3, new Scalar(0, 0, 0));
        Core.normalize(Hist, Hist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] HistData = new float[(int) (Hist.total() * Hist.channels())];
        Hist.get(0, 0, HistData);

        int binW = (int) Math.round((double) wide / 256);
        for (int i = 0; i < 256; i++) {
            Imgproc.line(histImage,
                    new Point(binW * (i), high),
                    new Point(binW * (i), high - Math.round(HistData[i])),
                    new Scalar(255, 255, 255), 2);
        }
        HighGui.imshow("calcHist", histImage);
        HighGui.waitKey();
    }
}
