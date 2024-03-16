package com.foreign;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.util.ArrayList;
import java.util.List;


class EAN13BarcodeRecognizer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        // Load the image
        Mat image = Imgcodecs.imread("imag/1760131.png");

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Threshold the image to binary
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        // Find contours in the binary image
        Mat hierarchy = new Mat();
//        MatOfPoint contours = new MatOfPoint();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Loop through all contours and find EAN-13 barcodes
        for (int i = 0; i < contours.size(); i++) {
            // Get the current contour
            MatOfPoint contour = new MatOfPoint(contours.get(i));

            // Approximate the contour to a polygon
            MatOfPoint2f approx = new MatOfPoint2f();
            double epsilon = 0.01 * Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approx, epsilon, true);

            // Check if the polygon has 13 edges (for EAN-13 barcode)
            if (approx.total() == 13) {
                // Draw the polygon on the original image
                Mat result = image.clone();
                Imgproc.drawContours(result, contours, i, new Scalar(0, 0, 255), 3);

                // Print the barcode number (the first 12 digits)
                String barcode = "";
                for (int j = 0; j < 12; j++) {
                    // Get the center of each bar
                    int centerX = (int) ((approx.toList().get(j).x + approx.toList().get(j + 1).x) / 2);
                    int centerY = (int) ((approx.toList().get(j).y + approx.toList().get(j + 1).y) / 2);

                    // Get the color of the center pixel
                    double[] color = image.get(centerY, centerX);

                    // If the center pixel is black, add a "0" to the barcode number
                    if (color[0] < 50 && color[1] < 50 && color[2] < 50) {
                        barcode += "0";
                    }
                    // If the center pixel is white, add a "1" to the barcode number
                    else if (color[0] > 200 && color[1] > 200 && color[2] > 200) {
                        barcode += "1";
                    }
                }
                System.out.println("Barcode number: " + barcode);
            }
        }
    }
}

/*
Note that this program assumes that the EAN-13 barcodes in the image are oriented horizontally. If the barcodes are rotated, you may need to use the Hough transform to detect the angle of rotation and rotate the image accordingly before applying the barcode recognition algorithm.
*/