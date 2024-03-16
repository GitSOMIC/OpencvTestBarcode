///**
// * 自适用二值化+zxing识别条形码
// * @Author 王嵩
// * @param response
// * @param imagefile
// * @param binaryType 二值化类型
// * @param blockSize 附近区域面积
// * @param constantC 它只是一个常数，从平均值或加权平均值中减去的常数
// * @Date 2018年5月17日
// * 更新日志
// * 2018年5月17日 王嵩  首次创建
// */
//@RequestMapping(value = "zxing")
//public void zxing(HttpServletResponse response, String imagefile, Integer adaptiveMethod, Integer binaryType,
//        Integer blockSize, Double constantC) {
//        //
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        logger.info("\n 自适用二值化方法");
//
//        // 灰度化
//        // Imgproc.cvtColor(source, destination, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//        String sourcePath = Constants.PATH + imagefile;
//        logger.info("url==============" + sourcePath);
//        // 加载为灰度图显示
//        Mat source = Highgui.imread(sourcePath, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//        Mat destination = new Mat(source.rows(), source.cols(), source.type());
//        logger.info("binaryType:{},blockSize:{},constantC:{}", binaryType, blockSize, constantC);
//        switch (adaptiveMethod) {
//        case 0:
//        adaptiveMethod = Imgproc.ADAPTIVE_THRESH_MEAN_C;
//        break;
//        case 1:
//        adaptiveMethod = Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
//        break;
//        }
//
//        switch (binaryType) {
//        case 0:
//        binaryType = Imgproc.THRESH_BINARY;
//        break;
//        case 1:
//        binaryType = Imgproc.THRESH_BINARY_INV;
//        break;
//        case 2:
//        binaryType = Imgproc.THRESH_TRUNC;
//        break;
//        case 3:
//        binaryType = Imgproc.THRESH_TOZERO;
//        break;
//        case 4:
//        binaryType = Imgproc.THRESH_TOZERO_INV;
//        break;
//default:
//        break;
//        }
//        // Imgproc.adaptiveThreshold(source, destination, 255, adaptiveMethod, binaryType, blockSize, constantC);
//        Imgproc.threshold(source, destination, 190, 255, Imgproc.THRESH_BINARY);
//        String result = parseCode(destination);
//
//        renderString(response, result);
//
//        }
//
//private static String parseCode(Mat mat) {
//        String resultText = "无法识别！！！";
//        try {
//        MultiFormatReader formatReader = new MultiFormatReader();
//        // if (!file.exists()) {
//        // System.out.println("nofile");
//        // return;
//        // }
//        // BufferedImage image = ImageIO.read(file);
//
//        BufferedImage image = OpenCVUtil.toBufferedImage(mat);
//        LuminanceSource source = new BufferedImageLuminanceSource(image);
//        Binarizer binarizer = new HybridBinarizer(source);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
//
//        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
//        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
//
//        Result result = formatReader.decode(binaryBitmap, hints);
//        StringBuffer sbuffer = new StringBuffer();
//        sbuffer.append("解析结果 = " + result.toString() + "\n");
//        sbuffer.append("二维码格式类型 = " + result.getBarcodeFormat() + "\n");
//        sbuffer.append("二维码文本内容 = " + result.getText() + "\n");
//        resultText = sbuffer.toString();
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
//        return resultText;
//        }
//
//
