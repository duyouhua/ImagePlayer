package com.liukun.imp;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageIO {

    public static void convertJPEGtoBin(File jpeg, String format) {
        Mat image = Imgcodecs.imread(jpeg.toString(), Imgcodecs.IMREAD_COLOR);
        Mat output = image.clone();
        switch (format) {
            case "I420":
                Imgproc.cvtColor(image, output, Imgproc.COLOR_BGR2YUV_I420);
                break;
            case "YV21":
                Imgproc.cvtColor(image, output, Imgproc.COLOR_BGR2YUV_YV12);
                break;
            default:break;
        }
        String fnOutput = jpeg.getParent() + File.separator + "image_" + output.width() + "x" + output.height() + "_" + format + ".bin";
        writeBinToFile(output, new File(fnOutput));
    }

    public static Bitmap convertI420toBitmap(byte[] data, int w, int h) {

        Mat imageI420 = new Mat(h * 3 / 2, w, CvType.CV_8UC1);
        imageI420.put(0,0,data);

        Mat imageBGR = new Mat(h,w,CvType.CV_8UC3);
        Imgproc.cvtColor(imageI420, imageBGR, Imgproc.COLOR_YUV2RGB_I420);

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageBGR, bm);

        return bm;
    }

    public static Bitmap convertYV12toBitmap(byte[] data, int w, int h) {

        Mat imageI420 = new Mat(h * 3 / 2, w, CvType.CV_8UC1);
        imageI420.put(0,0,data);

        Mat imageBGR = new Mat(h,w,CvType.CV_8UC3);
        Imgproc.cvtColor(imageI420, imageBGR, Imgproc.COLOR_YUV2RGB_YV12);

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageBGR, bm);

        return bm;
    }

//    public static Bitmap convertRGBtoBitmap(Mat image) {
//
//    }
//
//    public static Bitmap convertPGMtoBitmap(Mat image) {
//
//    }
//
//    public static Bitmap convertFOLATtoBitmap(Mat image) {
//
//    }

    private static Mat readBinFromFile(File file) {
        Mat image = new Mat(GlobalDef.MAX, GlobalDef.MAX,CvType.CV_8UC4);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[fis.available()];
            fis.read(data,0,data.length);
            fis.close();
            image.put(0,0,data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private static void writeBinToFile(Mat image, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = new byte[image.cols() * image.rows() * (int)image.elemSize()];
            image.get(0,0,data);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}