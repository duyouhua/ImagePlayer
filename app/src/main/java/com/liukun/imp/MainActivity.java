package com.liukun.imp;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileChooserDialog.FileCallback {

    private static final String TAG = "OCVSample::Activity";

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

        // 实现全屏 隐藏状态栏/标题栏/导航虚拟键
        BarUtils.setStatusBarVisibility(this,false);
        BarUtils.setNavBarImmersive(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 加载界面
        setContentView(R.layout.activity_main);

        // 欢迎语
        ToastUtils.showShort(R.string.welcome);

        OpenCVLoader.initDebug();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void doSetResolution(View view) {
        showList(R.array.resolution);
    }

    public void doSetFormat(View view) {
        showList(R.array.format);
    }

    public void showList(@ArrayRes int itemsRes) {
        new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .items(itemsRes)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        ToastUtils.showShort("xxx");
                    }
                })
                .show();
    }

    public void doOpen(View view) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(final ShouldRequest shouldRequest) {
                        DialogHelper.showRationaleDialog(shouldRequest);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        openFileSelectorDialog();
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
                })
                .request();
    }

    private void openFileSelectorDialog() {
        new FileChooserDialog.Builder(this)
                .initialPath("/sdcard")  // changes initial path, defaults to external storage directory
                .mimeType("image/*") // Optional MIME type filter
                .extensionsFilter(".png", ".jpg") // Optional extension filter, will override mimeType()
                .tag("optional-identifier")
                .goUpLabel("Up") // custom go up label, default label is "..."
                .show(this); // an AppCompatActivity which implements FileCallback
    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {
        // TODO
        final String tag = dialog.getTag(); // gets tag set from Builder, if you use multiple dialogs
        ToastUtils.showShort(tag);
        ImageIO.convertJPEGtoBin(file.toString(), "I420");
    }

    @Override
    public void onFileChooserDismissed(@NonNull FileChooserDialog dialog) {
        // TODO
    }

    public void doReverseWH(View view) {

    }

    public void showAbout(View view) {

    }

}
