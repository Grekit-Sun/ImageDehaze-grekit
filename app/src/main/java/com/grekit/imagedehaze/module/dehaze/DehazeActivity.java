package com.grekit.imagedehaze.module.dehaze;


import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.grekit.imagedehaze.R;
import com.grekit.imagedehaze.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DehazeActivity extends BaseActivity<DehazePresenter, IDehazeView> implements IDehazeView {

    @BindView(R.id.preview_layout)
    FrameLayout mPreviewLayout;
    @BindView(R.id.take_pic)
    ImageView mTakePic;
    @BindView(R.id.open_album)
    ImageView mOpenAlbum;

    public static final String KEY_IMAGE_PATH = "imagePath";
    /**
     * 闪光灯
     */
    private ImageView mFlashButton;
    /**
     * 拍照按钮
     */
    private ImageView mPhotoButton;
    /**
     * 取消保存按钮
     */
    private ImageView mCancleSaveButton;
    /**
     * 保存按钮
     */
    private ImageView mSaveButton;
    /**
     * 聚焦视图
     */
    private OverCameraView mOverCameraView;
    /**
     * 相机类
     */
    private Camera mCamera;
    /**
     * Handle
     */
    private Runnable mRunnable;
    /**
     * 是否开启闪光灯
     */
    private boolean isFlashing;
    /**
     * 图片流暂存
     */
    private byte[] imageData;
    /**
     * 拍照标记
     */
    private boolean isTakePhoto;
    /**
     * 是否正在聚焦
     */
    private boolean isFoucing;

    private static final int REQUEST_CODE_GET_IMAGE_ALBUM = 1;
    private final int REQUEST_CODE_PERMISSION = 33;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
//        mCamera = Camera.open();
//        CameraPreview preview = new CameraPreview(this, mCamera);
//        mOverCameraView = new OverCameraView(this);
//        mPreviewLayout.addView(preview);
//        mPreviewLayout.addView(mOverCameraView);
        File mediaFile = null;
        String cameraPath;
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath()
                    + File.separator
                    + "Pictures/temp.jpg");//注意这里需要和filepaths.xml中配置的一样
            cameraPath = mediaFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(mediaFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick({R.id.take_pic, R.id.open_album})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_pic:
                takePhoto();
                break;
            case R.id.open_album:
                openAlbum();
                break;
        }
    }

    @Override
    public void getDehazeImage(byte[] dehazeImageData) {
        if (dehazeImageData != null && dehazeImageData.length > 0) {
            savePhoto(dehazeImageData);
        }
        mPreviewLayout.addView(mOverCameraView);
    }

    /**
     * 注释：拍照并保存图片到相册
     * 时间：2019/3/1 0001 15:37
     * 作者：郭翰林
     */
    private void takePhoto() {
        isTakePhoto = true;
        //调用相机拍照
        mCamera.takePicture(null, null, null, (data, camera1) -> {
            //视图动画
//            mPhotoLayout.setVisibility(View.GONE);
//            mConfirmLayout.setVisibility(View.VISIBLE);
//            AnimSpring.getInstance(mConfirmLayout).startRotateAnim(120, 360);
//            mPresenter.dehazeImage(data);
            //停止预览
            mCamera.stopPreview();
            savePhoto(data);
            mPreviewLayout.removeAllViews();
            mPreviewLayout.addView(mOverCameraView);
        });
    }

    /**
     * 注释：取消保存
     * 时间：2019/3/1 0001 16:31
     * 作者：郭翰林
     */
    private void cancleSavePhoto() {
//        mPhotoLayout.setVisibility(View.VISIBLE);
//        mConfirmLayout.setVisibility(View.GONE);
//        AnimSpring.getInstance(mPhotoLayout).startRotateAnim(120, 360);
        //开始预览
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = () -> {
                    Toast.makeText(this, "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_SHORT).show();
                    isFoucing = false;
                    mOverCameraView.setFoucuing(false);
                    mOverCameraView.disDrawTouchFocusRect();
                };
                //设置聚焦超时
                handleEventDelay(mRunnable, 3000);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 注释：自动对焦回调
     * 时间：2019/3/1 0001 10:02
     * 作者：郭翰林
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };

    /**
     * 注释：保持图片
     * 时间：2019/3/1 0001 16:32
     * 作者：郭翰林
     */
    private void savePhoto(byte[] imageData) {
        FileOutputStream fos = null;
        String cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
        //相册文件夹
        File cameraFolder = new File(cameraPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //保存的图片文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
        File imageFile = new File(imagePath);
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    Intent intent = new Intent();
                    intent.putExtra(KEY_IMAGE_PATH, imagePath);
                    setResult(RESULT_OK, intent);
                } catch (IOException e) {
                    setResult(RESULT_FIRST_USER);
                    e.printStackTrace();
                }
            }
            finish();
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE_ALBUM);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_dehaze_layout;
    }

    @Override
    protected Class<IDehazeView> getViewClass() {
        return IDehazeView.class;
    }

    @Override
    protected Class<DehazePresenter> getPresenterClass() {
        return DehazePresenter.class;
    }

}
