package com.ctbu.detect;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

/**
 * @author Jerry
 * @version 1.0
 * @CreateTime 2023-04-16  18:22
 * @Description TODO
 * @since 1.0
 */
public class Candidate {
   private Mat cropRect;
   private RotatedRect rotatedRect;

    @Override
    public String toString() {
        return "Candidate{" +
                "cropRect=" + cropRect +
                ", rotatedRect=" + rotatedRect +
                '}';
    }

    public Mat getCropRect() {
        return cropRect;
    }

    public void setCropRect(Mat cropRect) {
        this.cropRect = cropRect;
    }

    public RotatedRect getRotatedRect() {
        return rotatedRect;
    }

    public void setRotatedRect(RotatedRect rotatedRect) {
        this.rotatedRect = rotatedRect;
    }

    public Candidate() {
    }

    public Candidate(Mat cropRect, RotatedRect rotatedRect) {
        this.cropRect = cropRect;
        this.rotatedRect = rotatedRect;
    }
}
