package com.liukun.imp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileChooserDialog.FileCallback {

    private static final String TAG = GlobalDef.PROJECT;

    private ImageView mainScreen = null;
    private View controlTab = null;

    private EditText widthInput = null;
    private EditText heightInput = null;
    private Spinner formatInput = null;

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

        // 获取应用使用次数
        Integer iRes = SPUtils.getInstance(GlobalDef.PROJECT).getInt(GlobalDef.PROJECT,GlobalDef.FAIL);
        if (iRes.equals(GlobalDef.FAIL)) {
            // 释放测试资源到SD卡
            copyAssetsToSD();
            iRes = 1;
        } else {
            iRes += 1;
        }
        // 写入应用使用次数进行记录
        SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.PROJECT,iRes);

        // 隐藏导航虚拟键
        BarUtils.setNavBarImmersive(this);

        // 加载UI界面
        setContentView(R.layout.activity_main);

        // 获取显示控件
        mainScreen = findViewById(R.id.ui_screen);

        // 获取操作控件
        controlTab = findViewById(R.id.layout_settings);

        // 初始化OpenCV库
        OpenCVLoader.initDebug();
    }

    /** * * 判断是否有长按动作发生 *
     * @param lastX 按下时X坐标 *
     * @param lastY 按下时Y坐标 * * *
     * @param thisX * 移动时X坐标 * *
     * @param thisY * 移动时Y坐标 * *
     * @param lastDownTime * 按下时间 * *
     * @param thisEventTime * 移动时间 * *
     * @param longPressTime * 判断长按时间的阀值 */
    static boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime, long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }


    boolean mIsLongPressed = false;
    float mLastMotionX = 0, mLastMotionY = 0;
    long lastDownTime = 0;
    long eventTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                lastDownTime = event.getDownTime();
                mIsLongPressed = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                eventTime = event.getEventTime();
                if(!mIsLongPressed){
                    mIsLongPressed = isLongPressed(mLastMotionX, mLastMotionY, x, y, lastDownTime, eventTime,1500);
                }
                if(mIsLongPressed){
                    mIsLongPressed = true;
                    if (controlTab.getVisibility() == View.VISIBLE){
                        controlTab.setVisibility(View.GONE);
                    } else {
                        controlTab.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsLongPressed = false;
                break;


        }
        return true;
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
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(DialogHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d(permissionsGranted);
                        ToastUtils.showShort("Open " + file.getName());
                        SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.FILENAME, file.getPath());
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
    }

    private void openFileSelectorDialog() {
        new FileChooserDialog.Builder(this)
                .initialPath(Environment.getExternalStorageDirectory().getPath())
                .extensionsFilter(GlobalDef.EXTENSIONS)
                .show(this);
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

    // 显示图片
    private void showImage(float scale) {
        // 读取输入文件全名
        String strFilename = SPUtils.getInstance(GlobalDef.PROJECT).getString(GlobalDef.FILENAME);
        // 判断输入格式是否支持
        Integer iFormat = SPUtils.getInstance(GlobalDef.PROJECT).getInt(GlobalDef.FORMAT);
        // 判断输入宽高是否合法
        String strWidth =  SPUtils.getInstance(GlobalDef.PROJECT).getString(GlobalDef.WIDTH);
        Integer iWidth = Integer.parseInt(strWidth);
        String strHeight =  SPUtils.getInstance(GlobalDef.PROJECT).getString(GlobalDef.HEIGHT);
        Integer iHeight = Integer.parseInt(strHeight);
        // 图片格式转化
        if (iFormat>=0 && iFormat<=3) {
            byte[] bytesBuf = FileIOUtils.readFile2BytesByStream(strFilename);
            bmImage = ImageIO.convertYUVsToBitmap(bytesBuf, iWidth, iHeight, iFormat);
        } else if (iFormat>=7 && iFormat<=9) {
            bmImage = ImageIO.convertImagesToBitmap(strFilename);
        }
        // 图片缩放
        if(bmImage!=null) bmImage = ImageUtils.scale(bmImage, scale, scale);
        // 显示图片
        if(mainScreen!=null && bmImage!=null) mainScreen.setImageBitmap(bmImage);
        // 隐藏导航虚拟键
        BarUtils.setNavBarImmersive(this);
    }

    public void btnSettings(View view) {
        // 弹窗
        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.settings)
                        .customView(R.layout.dialog_settings, true)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> showImage(1.0f))
                        .build();
        // 宽度设置
        widthInput = dialog.getCustomView().findViewById(R.id.width);
        widthInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.WIDTH, s.toString().trim());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
        // 高度设置
        heightInput = dialog.getCustomView().findViewById(R.id.height);
        heightInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.HEIGHT, s.toString().trim());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
        // 格式选择
        formatInput = dialog.getCustomView().findViewById(R.id.format);
        formatInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.FORMAT, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 显示窗口
        dialog.show();
    }

    float mScale = 1.0f;
    public void btnZoomIn(View view) {
        mScale *= 2.0f;
        showImage(mScale);
    }

    public void btnZoomOut(View view) {
        mScale *= 0.5f;
        showImage(mScale);
    }

    public void btnExport(View view) {
        if (bmImage != null) {
            ImageUtils.save(bmImage, GlobalDef.SCREENSHOT, Bitmap.CompressFormat.PNG);
        } else {
            ToastUtils.showShort("保存失败");
        }
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

    // 拷贝Assets资源到SD卡
    private void copyAssetsToSD() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(DialogHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d(permissionsGranted);
                        FileUtils.createOrExistsDir(GlobalDef.ROOT);
                        ResourceUtils.copyFileFromAssets(GlobalDef.IMAGE,GlobalDef.ROOT+File.separator+GlobalDef.IMAGE);
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

    // 根据文件扩展名估计文件格式
    private void autoSetFormat(String filename) {
        String extension = FileUtils.getFileExtension(filename);

        extension = "." + extension;

        /* bin yuv */
        if(extension.equals(GlobalDef.EXTENSIONS[0]) || extension.equals(GlobalDef.EXTENSIONS[1])){
            SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.FORMAT, 0);
            return;
        }

        /* bmp jpeg jpg png */
        if(extension.equals(GlobalDef.EXTENSIONS[2]) || extension.equals(GlobalDef.EXTENSIONS[3]) || extension.equals(GlobalDef.EXTENSIONS[4]) || extension.equals(GlobalDef.EXTENSIONS[5])){
            SPUtils.getInstance(GlobalDef.PROJECT).put(GlobalDef.FORMAT, 7);
            return;
        }

    }

    // 根据文件名估计分辨率
    private void autoSetResolution(String filename) {

    }

}
