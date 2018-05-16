package com.liukun.imp;

import android.os.Environment;
import java.io.File;

public class GlobalDef {

    static final String PROJECT = "IMPX";
    static final String ROOT = Environment.getExternalStorageDirectory() + File.separator + PROJECT;

    static final String[] EXTENSIONS = {".bin", ".yuv", ".bmp", ".jpeg", ".jpg", ".png"};
    static final String[] YUVs = {"I420", "YV12", "NV12", "NV21"};

    static final Integer PASS = 0;
    static final Integer FAIL = -1;
    static final Integer MAX = 9999;

    static final String IMAGE = "aoisola.jpg";
    static final String SCREENSHOT = ROOT + File.separator + "screen.png";

    static final String FILENAME = PROJECT + "_FILENAME";
    static final String WIDTH = PROJECT + "_WIDTH";
    static final String HEIGHT = PROJECT + "_HEIGHT";
    static final String FORMAT = PROJECT + "_FORMAT";
}
