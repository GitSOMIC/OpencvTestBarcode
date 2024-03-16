package com.ctbu.decode2;

import com.ctbu.decode.Result;
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

    /**
     * 将每个bar的具体的像素，变成每个bar的像素数量
     *
     * @param patterns
     * @return int patterns
     */
    public List patternsToLength(List patterns) {
        for (int i = 0; i < patterns.size(); i++) {
            int length = patterns.get(i).toString().length();
            patterns.set(i, length);
        }
        return patterns;
    }

    /**
     * 用AT1 ,AT2 定义T1,T2的归一化值
     * Ti/I<2.5/7, Ki=2；
     * 2.5/7< Ti/I<3.5/7，Ki=3；
     * 3.5/7< Ti/I<4.5/7，Ki=4；
     * Ti/I>4.5/7, Ki=5。
     * @param value
     * @return
     */
    public int divide(double value) {
        if (value < 2.5 / 7) {
            return 2;
        } else if (value < 3.5 / 7) {
            return 3;
        } else if (value < 4.5 / 7) {
            return 4;
        } else
            return 5;
    }

    /**
     * 左区解码器
     *
     * @param at1
     * @param at2
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     * @return
     */
    public static LeftCode decode_left(int at1, int at2, int c1, int c2, int c3, int c4) {
        HashMap<String, LeftCode> left_pattern = new HashMap();
        left_pattern.put("2,2", new LeftCode(6, "O"));
        left_pattern.put("2,3", new LeftCode(0, "E"));
        left_pattern.put("2,4", new LeftCode(4, "O"));
        left_pattern.put("2,5", new LeftCode(3, "E"));
        left_pattern.put("3,2", new LeftCode(9, "E"));
        left_pattern.put("3,3", new LeftCode(8, "O", 2));
        left_pattern.put("3,4", new LeftCode(7, "E", 1));
        left_pattern.put("3,5", new LeftCode(5, "O"));
        left_pattern.put("4,2", new LeftCode(9, "O"));
        left_pattern.put("4,3", new LeftCode(8, "E", 2));
        left_pattern.put("4,4", new LeftCode(7, "O", 1));
        left_pattern.put("4,5", new LeftCode(5, "E"));
        left_pattern.put("5,2", new LeftCode(6, "E"));
        left_pattern.put("5,3", new LeftCode(0, "O"));
        left_pattern.put("5,4", new LeftCode(4, "E"));
        left_pattern.put("5,5", new LeftCode(3, "O"));
        LeftCode leftPatternDict = left_pattern.get(at1 + "," + at2);
        /*
            解决四种情况例外
            左侧奇字符和右侧偶字符1, 7归一化值均为44，
            左侧奇字符和右侧偶字符2, 8归一化值均为33，
            左侧偶字符1, 7归一化值均为34，
            左侧偶字符2, 8归一化值均为43，

         */
        boolean alternative = false;//是否有冲突
        Integer code;//接收 alterCode

        if (at1 == 3 && at2 == 3) {
            if (c3 + 1 >= c4) {
                alternative = true;
            }
        }
        if (at1 == 3 && at2 == 4) {
            if (c2 + 1 >= c3) {
                alternative = true;
            }
        }
        if (at1 == 4 && at2 == 3) {
            if (c2 + 1 >= c1) {
                alternative = true;
            }
        }
        if (at1 == 4 && at2 == 4) {
            if (c1 + 1 >= c2) {
                alternative = true;
            }
        }

        //通过alternative结果判断 具体字符值
        if (alternative) {
            code = leftPatternDict.getAlterCode();
        } else {
            code = leftPatternDict.getCode();
        }

        LeftCode finalCode = new LeftCode(code, leftPatternDict.getParity());
        return finalCode;
    }

    /**
     * 右区解码器
     *
     * @param at1
     * @param at2
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     * @return
     */
    public static RightCode decode_right(int at1, int at2, int c1, int c2, int c3, int c4) {
        HashMap<String, RightCode> right_pattern = new HashMap();
        right_pattern.put("2,2", new RightCode(6));
        right_pattern.put("2,4", new RightCode(4));
        right_pattern.put("3,3", new RightCode(8, 2));
        right_pattern.put("3,5", new RightCode(5));
        right_pattern.put("4,2", new RightCode(9));
        right_pattern.put("4,4", new RightCode(7, 1));
        right_pattern.put("5,3", new RightCode(0));
        right_pattern.put("5,5", new RightCode(3));

        RightCode rightPatternDict = right_pattern.get(at1 + "," + at2);

        boolean alternative = false;//是否有冲突
        Integer code;//接收 alterCode

        /*
            解决两种情况例外
            左侧奇字符和右侧偶字符1, 7归一化值均为44，
            左侧奇字符和右侧偶字符2, 8归一化值均为33
         */
        if (at1 == 3 && at2 == 3) {
            if (c3 + 1 >= c4) {
                alternative = true;
            }
        }
        if (at1 == 4 && at2 == 4) {
            if (c1 + 1 >= c2) {
                alternative = true;
            }
        }

        if (alternative) {
            code = rightPatternDict.getAlterCode();
        } else {
            code = rightPatternDict.getCode();
        }
        RightCode finalRightCode = new RightCode(code);
        return finalRightCode;

    }

    /**
     * 获取 相似边距离译码法 需要的C1,C2,C3,C4,L,T1,T2,AT1,AT2
     * @param patterns
     * @param isLeft
     * @return
     */
    public LinkedList readPatterns(List patterns, boolean isLeft) {
//        StringBuilder codes = new StringBuilder();
        LinkedList<Code> codes = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            int startIndex = i * 4;
            List<Integer> sliced = patterns.subList(startIndex, startIndex + 4);
            int c1 = sliced.get(0);
            int c2 = sliced.get(1);
            int c3 = sliced.get(2);
            int c4 = sliced.get(3);
            int total = c1 + c2 + c3 + c4;
            double t1 = c1 + c2;
            double t2 = c2 + c3;
            int at1 = divide(t1 / total);
            int at2 = divide(t2 / total);
            //如果是左区就用左区解码
            if (isLeft) {
                LeftCode leftCode = decode_left(at1, at2, c1, c2, c3, c4);
                codes.add(leftCode);
                //如果是右区就用右区解码
            } else {
                RightCode rightCode = decode_right(at1, at2, c1, c2, c3, c4);
                codes.add(rightCode);
            }
        }
        return codes;
    }

    /**
     * 将左区，右区解码结果和在一起拼接出EAN13码
     * @param leftCodes
     * @param rightCodes
     * @return
     */
    public String getEAN13(LinkedList<LeftCode> leftCodes, LinkedList<RightCode> rightCodes) {
        StringBuilder ean13 = new StringBuilder();
        ean13.append(get_first_digit(leftCodes) + "");

        for (LeftCode left_code : leftCodes) {
            ean13.append(left_code.getCode() + "");
        }
        for (RightCode right_code : rightCodes) {
            ean13.append(right_code.getCode());
        }
        return ean13.toString();
    }

    /**
     * 将line进行解码
     * @param line
     */
    public void decodeLine(List line) {

        List bars = readBars(line);

        List leftGuard = bars.subList(0, 3);//LEFT_GUARD = "101"
        List leftPatterns = bars.subList(3, 27);//左区
        List centerGuard = bars.subList(27, 32);//CENTER_GUARD = "01010"
        List rightPatterns = bars.subList(32, 56);//右区
        List rightGuard = bars.subList(56, 59);//RIGHT_GUARD = "101"

        leftPatterns = patternsToLength(leftPatterns);
        rightPatterns = patternsToLength(rightPatterns);
        LinkedList leftCodes = readPatterns(leftPatterns, true);
        LinkedList rightCodes = readPatterns(rightPatterns, false);
        String ean13 = getEAN13(leftCodes, rightCodes);
        boolean verify = verify(ean13);
        System.out.println("Detected code:" + ean13 + " verified:" + verify);
        if (verify){
            System.exit(0);
        }

    }
    @Deprecated
    public com.ctbu.decode.Result decodeLine(List line, int moduleLength, String dataString) {
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
    @Deprecated
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
    @Deprecated
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

    /**
     * 解码入口
     *
     * @param threshNot
     * @return
     */
    public void decode(Mat threshNot) {
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
        int height = threshNot.rows();
        for (int i = height - 1; i > 0; i--) {
            try {
                List line = new LinkedList<Integer>();
                int colsX = threshNot.cols();
                for (int j = 0; j < colsX; j++) {
                    //.get(height，width)[0]通道
                    line.add((int) threshNot.get(i, j)[0]);
                    if ((int) line.get(j) == 255) {
                        line.set(j, 1);
                    }
                }
                decodeLine(line);
            } catch (Exception e) {
                System.out.println(e+"  第"+i+"行像素解析失败");
            }
        }
        System.out.println("所有行像素解析完毕");
//        int middleY = threshNot.rows() / 2;
//
//        List line = new ArrayList<Integer>();
//        int colsX = threshNot.cols();
//        for (int i = 0; i < colsX; i++) {
//            //.get(height，width)[0]通道
//            line.add((int) threshNot.get(middleY, i)[0]);
//            if ((int) line.get(i) == 255) {
//                line.set(i, 1);
//            }
//        }
//        decodeLine(line);
    }

    /**
     * 通过url获取图片文件
     * @param url
     * @return
     */
    public Mat getMat(String url) {
        //从硬盘引入图像
        Mat imag = Imgcodecs.imread(url);
        Mat gray = new Mat();
        //灰度化
        Imgproc.cvtColor(imag, gray, Imgproc.COLOR_RGB2GRAY);
        //二值化 阈值化
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 200, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
//        Imgproc.threshold(gray, thresh, 125, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//        HighGui.imshow("thresh", thresh);
//        HighGui.waitKey();
        //取反
        Mat threshNot = new Mat();
        Core.bitwise_not(thresh, threshNot);//thresh取反
//        HighGui.imshow("threshNot", threshNot);
//        HighGui.waitKey();
//        HighGui.destroyAllWindows();
        return threshNot;
    }

    /**
     * 从硬盘获取图像进行处理，输出line
     * @param url
     * @return
     */
    @Deprecated
    public List getLine(String url) {
        //从硬盘引入图像
        Mat imag = Imgcodecs.imread(url);
        Mat gray = new Mat();
        //灰度化
        Imgproc.cvtColor(imag, gray, Imgproc.COLOR_RGB2GRAY);
        //二值化 阈值化
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 200, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//        Imgproc.threshold(gray, thresh, 125, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        HighGui.imshow("thresh", thresh);
        HighGui.waitKey();
//        取反
        Mat threshNot = new Mat();
        Core.bitwise_not(thresh, threshNot);//thresh取反
        HighGui.imshow("threshNot", threshNot);
        HighGui.waitKey();
        HighGui.destroyAllWindows();

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
//        读取图像行数rows得到高度height
//        int height = threshNot.rows();
//        for (int i = height-1 ; i > 0; i--) {
//            try {
//
//            }catch (Exception e){
//
//            }
//        }


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

    /**
     * 将连续分离的各个像素点，变成相同像素为一组的形式
     * [1,1,1,1,0,0,0,0] --> [1111,0000]
     *
     * @param line
     * @return bars
     */
    public static List readBars(List line) {
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
    @Deprecated
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
    @Deprecated
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

    /**
     * 根据奇偶表，反向获取 首位值
     * @param left_codes
     * @return 首位值
     */
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

    /**
     * 根据EAN-13吗的规则进行校验
     * @param ean13
     * @return
     */
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

//        List line = decodeEAN13.getLine("imag/oneCode/ean13/1760131.png");
//        List bars = decodeEAN13.readBars(line);
//        int moduleLength = decodeEAN13.detectModuleSize(bars);
//        String dataString = decodeEAN13.arrayAsString(line, moduleLength);
////        System.out.println(dataString);
//        Result result = decodeEAN13.decodeLine(line, moduleLength, dataString);
//        System.out.println(result);
        Mat mat = decodeEAN13.getMat("imag/oneCode/ean13/1760131.png");
        decodeEAN13.decode(mat);


    }
}
