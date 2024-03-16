package com.ctbu.cv.opencv.study;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
 
public class PPerspective {
 
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
         
        Mat src=Imgcodecs.imread("imag/book.png");
        Mat dst=new Mat();

        Point[] pt1 = new Point[4];
        pt1[0] = new Point(95, 129);
        pt1[1] = new Point(260, 157);
        pt1[2] = new Point(57, 469);
        pt1[3] = new Point(248, 454);        

        Point[] pt2 = new Point[4];
        pt2[0] = new Point(0, 0);
        pt2[1] = new Point(300, 0);
        pt2[2] = new Point(0,600);
        pt2[3] = new Point(300,600);
        
        MatOfPoint2f mop1 = new MatOfPoint2f(pt1); 
        MatOfPoint2f mop2 = new MatOfPoint2f(pt2);
                   
        Mat perspectiveMmat=Imgproc.getPerspectiveTransform(mop1, mop2);
        Imgproc.warpPerspective(src, dst, perspectiveMmat, new Size(300,600));

        HighGui.imshow("book", dst); 
		HighGui.waitKey(0);             
 
    }
 
}

