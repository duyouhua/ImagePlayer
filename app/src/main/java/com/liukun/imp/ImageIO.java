package com.liukun.imp;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageIO {

    // YUV类型数据转化后显示
    public static Bitmap convertYUVsToBitmap(byte[] data, int w, int h, int format) {
        // YUV数据byte[]格式转Mat格式
        Mat image = new Mat(h * 3 / 2, w, CvType.CV_8UC1);
        image.put(0,0,data);
        // YUV数据转RGB数据
        Mat imageRGB = new Mat(h,w,CvType.CV_8UC3);
        switch (format){
            case 0: /* I420 */
                Imgproc.cvtColor(image, imageRGB, Imgproc.COLOR_YUV2RGB_I420);
                break;
            case 1: /* YV12 */
                Imgproc.cvtColor(image, imageRGB, Imgproc.COLOR_YUV2RGB_YV12);
                break;
            case 2: /* NV12 */
                Imgproc.cvtColor(image, imageRGB, Imgproc.COLOR_YUV2RGB_NV12);
                break;
            case 3: /* NV21 */
                Imgproc.cvtColor(image, imageRGB, Imgproc.COLOR_YUV2RGB_NV21);
                break;
            default:
                break;
        }
        // 创建Bitmap图像
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageRGB, bm);
        // 返回用于显示
        return bm;
    }

    // 常见压缩封装格式图片转换显示
    public static Bitmap convertImagesToBitmap(String file) {
        // 读取图片
        Mat image = Imgcodecs.imread(file, Imgcodecs.IMREAD_COLOR);
        // BGR转RGB格式
        Mat output = image.clone();
        Imgproc.cvtColor(image, output, Imgproc.COLOR_BGR2RGB);
        // 创建Bitmap图像
        Bitmap bm = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(output, bm);
        // 返回用于显示
        return bm;
    }

}