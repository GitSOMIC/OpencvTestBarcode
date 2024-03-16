package com.ctbu.decode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * @author Jerry
 * @version 1.0
 */
public class DecodeEAN13 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public List getLine(String url) {
        //引入图像
        Mat imag = Imgcodecs.imread(url);
        Mat gray = new Mat();
        //灰度化
        Imgproc.cvtColor(imag, gray, Imgproc.COLOR_RGB2GRAY);
        //二值化 阈值化
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 200, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        HighGui.imshow("1", thresh);
        HighGui.waitKey();
//        取反
        Mat threshNot = new Mat();
        Core.bitwise_not(thresh, threshNot);//thresh取反
        HighGui.imshow("2", threshNot);
        HighGui.waitKey();

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
//       int middleY = (int) ((threshNot.size().height)/2);
        int middleY = threshNot.rows() / 2;

        List line = new ArrayList<Integer>();
        int colsX = threshNot.cols();
        for (int i = 0; i < colsX; i++) {
            //.get(height，width)[0]通道
            line.add((int) threshNot.get(middleY, i)[0]);
            if ((int) line.get(i) == 255) {
                line.set(i, 1);
            }
        }
        return line;

    }

    public List readBars(List line) {
        List bars = new ArrayList<StringBuilder>();
        int currentLength = 1;

        for (int i = 0; i < line.size() - 1; i++) {
            if (line.get(i) == line.get(i + 1)) {
                currentLength++;
            } else {
                StringBuilder str = new StringBuilder();
                for (int j = 0; j < currentLength; j++) {
//                    bars.append(line.get(i).toString());
                    str.append(line.get(i).toString());
                }
                bars.add(str);
                currentLength = 1;
            }
        }
        bars.remove(0);

        return bars;
    }

    /**
     * 获取最细黑线 最小模组(Module)像素长度
     *
     * @param bars
     * @return
     */
    public int detectModuleSize(List bars) {
        int length = bars.get(0).toString().length();
        for (Object bar : bars) {
            length = Math.min(bar.toString().length(), length);
        }
        return length;
    }

    /**
     * 将最细模组，使用正则表达式(regular expression)多个1归一为一个1  多个0归一为一个0
     *
     * @param line
     * @param moduleLength
     * @return
     */
    public String arrayAsString(List<String> line, int moduleLength) {
        StringBuilder s = new StringBuilder();
        for (Object o : line) {
            s.append(o);
        }
        //使用Java正则不用Python这样
//        StringBuilder black = new StringBuilder("1");
//        StringBuilder white = new StringBuilder("0");
//
//        for (int i = 0; i < moduleLength; i++) {
//            black.append("1");
//            white.append("0");
//        }

        String sOut = s.toString();
        sOut = sOut.replaceAll("1{" + moduleLength + "}", "1");
        sOut = sOut.replaceAll("0{" + moduleLength + "}", "0");
        return sOut;
    }

    public Result decodeLine(List line, int moduleLength, String dataString) {
        String GUARD_PATTERN = "101";
        String CENTER_GUARD_PATTERN = "01010";

        int beginIndex = dataString.indexOf(GUARD_PATTERN) + GUARD_PATTERN.length();
        String data_string_left = dataString.substring(beginIndex);
        LinkedList<LeftCode> left_codes = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            int startIndex = i * 7;
            String bar_pattern = data_string_left.substring(startIndex, startIndex + 7);
            LeftCode decode = decodeLeftBarPattern(bar_pattern);
            System.out.println(decode);
            left_codes.add(decode);
        }
        data_string_left = data_string_left.substring(6 * 7);
        int center_index = data_string_left.indexOf(CENTER_GUARD_PATTERN) + CENTER_GUARD_PATTERN.length();
        data_string_left = data_string_left.substring(center_index);

        LinkedList<RightCode> right_codes = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            int startIndex = i * 7;
            String bar_pattern = data_string_left.substring(startIndex, startIndex + 7);
            RightCode decode = decodeRightBarPattern(bar_pattern);
            System.out.println(decode);
            right_codes.add(decode);
        }
        String ean13 = getEAN13(left_codes, right_codes);
        System.out.println("Decoded code:" + ean13);
        boolean isValid = verify(ean13);
        return new Result(ean13, isValid);
    }

    public LeftCode decodeLeftBarPattern(String bar_pattern) {
        HashMap<String, LeftCode> left_pattern_dict = new HashMap();
        left_pattern_dict.put("0001101", new LeftCode(0, "O"));
        left_pattern_dict.put("0100111", new LeftCode(0, "E"));
        left_pattern_dict.put("0011001", new LeftCode(1, "O"));
        left_pattern_dict.put("0110011", new LeftCode(1, "E"));
        left_pattern_dict.put("0010011", new LeftCode(2, "O"));
        left_pattern_dict.put("0011011", new LeftCode(2, "E"));
        left_pattern_dict.put("0111101", new LeftCode(3, "O"));
        left_pattern_dict.put("0100001", new LeftCode(3, "E"));
        left_pattern_dict.put("0100011", new LeftCode(4, "O"));
        left_pattern_dict.put("0011101", new LeftCode(4, "E"));
        left_pattern_dict.put("0110001", new LeftCode(5, "O"));
        left_pattern_dict.put("0111001", new LeftCode(5, "E"));
        left_pattern_dict.put("0101111", new LeftCode(6, "O"));
        left_pattern_dict.put("0000101", new LeftCode(6, "E"));
        left_pattern_dict.put("0111011", new LeftCode(7, "O"));
        left_pattern_dict.put("0010001", new LeftCode(7, "E"));
        left_pattern_dict.put("0110111", new LeftCode(8, "O"));
        left_pattern_dict.put("0001001", new LeftCode(8, "E"));
        left_pattern_dict.put("0001011", new LeftCode(9, "O"));
        left_pattern_dict.put("0010111", new LeftCode(9, "E"));
        return left_pattern_dict.get(bar_pattern);
    }

    public RightCode decodeRightBarPattern(String bar_pattern) {
        HashMap<String, RightCode> right_pattern_dict = new HashMap();
        right_pattern_dict.put("1110010", new RightCode(0));
        right_pattern_dict.put("1100110", new RightCode(1));
        right_pattern_dict.put("1101100", new RightCode(2));
        right_pattern_dict.put("1000010", new RightCode(3));
        right_pattern_dict.put("1011100", new RightCode(4));
        right_pattern_dict.put("1001110", new RightCode(5));
        right_pattern_dict.put("1010000", new RightCode(6));
        right_pattern_dict.put("1000100", new RightCode(7));
        right_pattern_dict.put("1001000", new RightCode(8));
        right_pattern_dict.put("1110100", new RightCode(9));
        return right_pattern_dict.get(bar_pattern);
    }

    public String getEAN13(LinkedList<LeftCode> left_codes, LinkedList<RightCode> right_codes) {
        StringBuilder ean13 = new StringBuilder();
        ean13.append(get_first_digit(left_codes) + "");

        for (LeftCode left_code : left_codes) {
            ean13.append(left_code.getCode() + "");
        }
        for (RightCode right_code : right_codes) {
            ean13.append(right_code.getCode());
        }
        return ean13.toString();
    }

    public int get_first_digit(LinkedList<LeftCode> left_codes) {
        HashMap<String, Integer> parity_dict = new HashMap();
        parity_dict.put("OOOOOO", 0);
        parity_dict.put("OOEOEE", 1);
        parity_dict.put("OOEEOE", 2);
        parity_dict.put("OOEEEO", 3);
        parity_dict.put("OEOOEE", 4);
        parity_dict.put("OEEOOE", 5);
        parity_dict.put("OEEEOO", 6);
        parity_dict.put("OEOEOE", 7);
        parity_dict.put("OEOEEO", 8);
        parity_dict.put("OEEOEO", 9);
        StringBuilder parity = new StringBuilder();
//        for (Object left_code : left_codes) {
//            (LeftCode)left_code
//            parity = parity+ .g
//        }
//        for (int i = 0; i < left_codes.size(); i++) {
//            LeftCode o = (LeftCode) left_codes.get(i);
//            parity.append(o.getParity());
//        }
        for (LeftCode left_code : left_codes) {
            parity.append(left_code.getParity());
        }
        return parity_dict.get(parity.toString());
    }

    //
//    public boolean verify(String ean13) {
//        int[] weight = {1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3};
//        int weighted_sum = 0;
//        for (int i = 0; i < 12; i++) {
//             weighted_sum = weighted_sum + weight[i] * Integer.parseInt(String.valueOf(ean13.charAt(i)));
//        }
//        String weighted_sum_string = weighted_sum + "";
//        int checksum = 0;
//        //取出字符串最后一位
//        int units_digit = (int) (weighted_sum_string.charAt(weighted_sum_string.length() - 1));
//        if (units_digit != 0) {
//            checksum = 10 - units_digit;
//        } else {
//            checksum = 0;
//        }
//        System.out.println("The checksum of " + ean13 + " is " + checksum);
//         if (checksum == Integer.parseInt(String.valueOf(ean13.charAt(ean13.length() - 1)))){
//            System.out.println("The code is valid");
//            return true;
//        } else {
//            System.out.println("The code is invalid");
//            return false;
//        }
//    }
    public boolean verify(String ean13) {
        int[] weight = {1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3};
        int weighted_sum = 0;
        for (int i = 0; i < 13; i++) {
            System.out.println((ean13.charAt(i)));
            //char类型的数据转换成int类型的数字。char会转成ASCII
            weighted_sum = weighted_sum + weight[i] * Integer.parseInt(String.valueOf(ean13.charAt(i)));
        }
        String weighted_sum_string = weighted_sum + "";
        int checksum = 0;
        //取出字符串最后一位
        int units_digit = (int) (weighted_sum_string.charAt(weighted_sum_string.length() - 1));
        checksum = units_digit % 10;
        System.out.println("The checksum of " + ean13 + " is " + checksum);
        //char类型的数据转换成int类型的数字。char会转成ASCII
//        if (checksum == (int) (ean13.charAt(ean13.length() - 1))) {  错误

        if (checksum == Integer.parseInt(String.valueOf(ean13.charAt(ean13.length() - 1)))) {
            System.out.println("The code is valid");
            return true;
        } else {
            System.out.println("The code is invalid");
            return false;
        }
    }

    public static void main(String[] args) {
        DecodeEAN13 decodeEAN13 = new DecodeEAN13();

        List line = decodeEAN13.getLine("imag/oneCode/ean13/1760131.png");
        List bars = decodeEAN13.readBars(line);
        int moduleLength = decodeEAN13.detectModuleSize(bars);
        String dataString = decodeEAN13.arrayAsString(line, moduleLength);
//        System.out.println(dataString);
        Result result = decodeEAN13.decodeLine(line, moduleLength, dataString);
        System.out.println(result);


    }
}
