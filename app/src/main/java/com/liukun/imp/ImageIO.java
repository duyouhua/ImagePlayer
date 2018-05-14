package com.liukun.imp;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageIO {

    private static final Integer MAX = 9999;

    public static void convertJPEGtoBin(String jpeg, String format) {
        Mat image = Imgcodecs.imread(jpeg, Imgcodecs.IMREAD_COLOR);
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
        writeBinToFile(output, new File("/sdcard/xxxx.bin"));
    }

    private static Mat readBinFromFile(File file) {
        Mat image = new Mat(MAX,MAX,CvType.CV_8UC4);
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