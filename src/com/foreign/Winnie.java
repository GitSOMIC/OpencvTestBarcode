//package com.foreign;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
///**
// * @author Jerry
// * @version 1.0
// */
//public class Winnie {
//    class Color {
//        int r;
//        int g;
//        int b;
//    }
//
//    // true 是白 false 是黑
//    boolean last = true;
//    boolean measure = true;
//    /** 文件选择器 */
//    JFileChooser jfc = new JFileChooser();
//    /** 待识别的图片 */
//    BufferedImage image;
//    /** 图片长度 */
//    static int length;
//    /** 图片宽度 */
//    static int width;
//    /** 单位宽度 */
//    static int perLength = 0;
//
//    /** 字符集 */
//    String[] a = {"0001101","0011001","0010011","0111101","0100011","0110001","0101111","0111011","0110111","0001011"};
//    String[] b = {"0100111","0110011","0011011","0100001","0011101","0111001","0000101","0010001","0001001","0010111"};
//    String[] c = {"1110010","1100110","1101100","1000010","1011100","1001110","1010000","1000100","1001000","1110100"};
//
//    /** 前置码集 */
//    String[] front = {"AAAAAA", "AABABB", "AABBAB", "AABBBA", "ABAABB", "ABBAAB", "ABBBAA","ABABAB","ABABBA","ABBABA"};
//    public void loadImage(){
//        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
//        jfc.setMultiSelectionEnabled(false);
//        jfc.setFileFilter(new FileNameExtensionFilter("图片文件(*.png, *.jpg, *.jpeg)",
//                "png","jpg","jpeg"));
//        File directory = new File("");
//        jfc.setCurrentDirectory(new File(directory.getAbsolutePath()));
//        int status = jfc.showOpenDialog(null);
//
//        // 打开图片
//        if(status==JFileChooser.APPROVE_OPTION) {
//            File sourceImage = jfc.getSelectedFile();
//            try {
//                image = ImageIO.read(sourceImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public Color[] proc(){
//        length = image.getHeight();
//        width = image.getWidth();
//
//        // 第一行条码
//        Color[] color = new Color[width];
//
//
//
//        // 获取第一行的像素值，并获取标准宽度
//        for (int i = 0; i < width; i++){
//            Color temp = new Color();
//            int pixel = image.getRGB(i, length/2);
//            temp.r = ((pixel >> 16) &  0xFF);
//            temp.g = ((pixel >> 8) &  0xFF);
//            temp.b = ((pixel &  0xFF));
//            color[i] = temp;
//
//            // 是否在获取标准宽度
//            if (measure){
//                if (temp.r == 255 || temp.g == 255 || temp.b == 255) {
//                    if (!last){
//                        measure = false;
//                    }
//                    last = true;
//                } else if (temp.r == 0 || temp.g == 0 || temp.b == 0){
//                    perLength++;
//                    last = false;
//                }
//            }
//        }
//        return color;
//    }
//
//    public int decode(Color[] color){
//        // EN-13 共 84 块数据区
//        int[] data = new int[84 * perLength];
//        int[] pureData = new int[84];
//        for (int i = 0; i < width; i++){
//            if (!measure){
//                if(color[i].r == 0 || color[i].g == 0 || color[i].b == 0){
//                    measure = true;
//                }
//            }
//
//            if (measure){
//                boolean range = count > 3 * perLength - 1 && count < 45 * perLength - 1
//                        || count > 50 * perLength - 1 && count < 85 * perLength - 1;
//                if (range){
//                    if (color[i].r == 255 || color[i].g == 255 || color[i].b == 255) {
//                        data[start] = 0;
//                        start++;
//                    } else if (color[i].r == 0 || color[i].g == 0 || color[i].b == 0){
//                        data[start] = 1;
//                        start++;
//                    }
//                }
//                count++;
//            }
//        }
//        // 获取纯净数据
//        for (int i = 0; i < pureData.length; i++) {
//            pureData[i] = data[i * perLength];
//        }
//        int[] result = new int[12];
//        count = 1;
//        int size = 7;
//        StringBuilder code = new StringBuilder();
//        StringBuilder sort = new StringBuilder();
//
//        for (int i = 0; i < pureData.length; i++) {
//            code.append(pureData[i]);
//            if (((i+1) % size) == 0){
//                for (int j = 0; j < a.length; j++){
//                    if (code.toString().equals(a[j])){
//                        if (count < 7){
//                            sort.append("A");
//                        }
//                        result[count] = j;
//                        count++;
//                    } else if (code.toString().equals(b[j])){
//                        if (count < 7){
//                            sort.append("B");
//                        }
//                        result[count] = j;
//                        count++;
//                    } else if (code.toString().equals(c[j])){
//                        if (count < 7){
//                            sort.append("C");
//                        }
//                        result[count] = j;
//                        count++;
//                    }
//                }
//                code = new StringBuilder();
//            }
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//}
