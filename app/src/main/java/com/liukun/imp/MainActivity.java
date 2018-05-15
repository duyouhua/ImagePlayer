package com.liukun.imp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileChooserDialog.FileCallback {

    private static final String TAG = GlobalDef.PROJECT;

    private ImageView mainScreen = null;
    private View positiveAction;
    private EditText widthInput;
    private EditText heightInput;
    private Spinner formatInput;


    private int imageWidth = 480;
    private int imageHeight = 640;
    private int imageFormat = 0;

    private byte[] bytesBuf= null;

    private Bitmap bmImage = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BlankJ工具初始化
        Utils.init(this);

        // 隐藏导航虚拟键
        BarUtils.setNavBarImmersive(this);

        // 释放测试资源到SD卡
        copyAssetsToSD();

        // 加载界面
        setContentView(R.layout.activity_main);

        //
        mainScreen = findViewById(R.id.ui_screen);

        // 欢迎语
        ToastUtils.showShort(R.string.welcome);

        OpenCVLoader.initDebug();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {

        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(DialogHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {

                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d(permissionsGranted);
                        if (file != null) {
                            ToastUtils.showShort("Open " + file.getPath());
                            bytesBuf = FileIOUtils.readFile2BytesByStream(file);
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            DialogHelper.showOpenAppSettingDialog();
                        }
                        LogUtils.d(permissionsDeniedForever, permissionsDenied);
                    }

                }).request();
    }

    @Override
    public void onFileChooserDismissed(@NonNull FileChooserDialog dialog) {
        // TODO
    }

    private void openFileSelectorDialog() {
        new FileChooserDialog.Builder(this)
                .initialPath("/sdcard")  // changes initial path, defaults to external storage directory
                .mimeType("*/*") // Optional MIME type filter
                .extensionsFilter(".bin") // Optional extension filter, will override mimeType()
                .tag("optional-identifier")
                .goUpLabel("...") // custom go up label, default label is "..."
                .show(this); // an AppCompatActivity which implements FileCallback
    }

    public void btnOpen(View view) {

        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(DialogHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {

                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d(permissionsGranted);
                        openFileSelectorDialog();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            DialogHelper.showOpenAppSettingDialog();
                        }
                        LogUtils.d(permissionsDeniedForever, permissionsDenied);
                    }

                }).request();
    }

    private void showImage() {

        imageWidth = Integer.getInteger(widthInput.getText().toString().trim(), 0);
        imageHeight = Integer.getInteger(heightInput.getText().toString().trim(), 0);


        ToastUtils.showShort("param " + imageWidth + "x" + imageHeight + " " + imageFormat);


        bmImage = ImageIO.convertI420toBitmap(bytesBuf, 2340, 2880);

        mainScreen.setImageBitmap(bmImage);
    }

    public void btnSettings(View view) {

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.settings)
                        .customView(R.layout.dialog_settings, true)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> showImage())
                        .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        widthInput = dialog.getCustomView().findViewById(R.id.width);
        widthInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        heightInput = dialog.getCustomView().findViewById(R.id.height);
        heightInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        formatInput = dialog.getCustomView().findViewById(R.id.format);
        formatInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageFormat = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }



    public void btnZoomIn(View view) {

        Bitmap bm = ImageUtils.scale(bmImage, 2340/2, 2880/2);
        mainScreen.setImageBitmap(bm);
    }

    public void btnZoomOut(View view) {

    }

    public void btnbtnSaveAs(View view) {

    }

    public void btnAbout(View view) {
        new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .content(R.string.useGoogleLocationServicesPrompt, true)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.accent)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .show();
    }

    // 拷贝预置图片资源到SD卡
    private void copyAssetsToSD() {
        // 查看是否具备读写权限
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(DialogHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        FileUtils.createOrExistsDir(GlobalDef.ROOT);
                        ResourceUtils.copyFileFromAssets("aoisola.jpg", GlobalDef.ROOT + File.separator+ "aoisola.jpg");
                        LogUtils.d(permissionsGranted);
                    }
                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            DialogHelper.showOpenAppSettingDialog();
                        }
                        LogUtils.d(permissionsDeniedForever, permissionsDenied);
                    }
                }).request();
    }

}
