package com.liukun.imp;

import android.os.Environment;
import java.io.File;

public class GlobalDef {

    static final String PROJECT = "IMP";
    static final String ROOT = Environment.getExternalStorageDirectory() + File.separator + PROJECT;

    static final Integer MAX = 6000;
}
