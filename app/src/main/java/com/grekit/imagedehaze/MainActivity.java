package com.grekit.imagedehaze;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.takePhoto)
    ImageView mTakePhoto;
    @BindView(R.id.openAlbum)
    ImageView mOpenAlbum;
    @BindView(R.id.camera)
    ImageView mCamera;
//    SurfaceView mCamera;

    private Camera camera;

    private static final int REQUEST_CODE_GET_IMAGE_ALBUM = 1;
    private final int REQUEST_CODE_PERMISSION = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestAllPower2();
        openCamera();
    }

    private void openCamera() {
//        SurfaceHolder holder = mCamera.getHolder();
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                camera = Camera.open(0);
                camera.setDisplayOrientation(90);
                try {
                    camera.setPreviewDisplay(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        };
//        holder.addCallback(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                boolean isGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    //有一个失败则失败。
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    }
                }
                if (isGranted) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void requestAllPower2() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //申请
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    REQUEST_CODE_PERMISSION);
        }
    }

    @OnClick({R.id.openAlbum, R.id.takePhoto})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
//                takePhoto();
                openAlbum();
//                Bitmap bitmap =  ((BitmapDrawable)mHazeImg.getDrawable()).getBitmap();
//                ImageDehaze dehaze = new ImageDehaze();
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray = baos.toByteArray();
//                    Object[] dehaze_image = dehaze.run_cnn(1,byteArray);
                } catch (Exception e) {

                } finally {
//                    dehaze.dispose();
                }
                break;
            case R.id.openAlbum:
                break;
            case R.id.camera:
                mCamera.setImageResource(R.drawable.dehaze);
                break;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //图库
            case REQUEST_CODE_GET_IMAGE_ALBUM:
                if (data != null) {
                    Uri uri = data.getData();
                    //uri转换成file
                    String[] arr = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(uri, arr, null, null, null);
                    int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String img_path = cursor.getString(img_index);
                    File file = new File(img_path);
                    if (file != null) {
                        String absolutePath = file.getAbsolutePath();
                        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
//                        mHazeImg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }
}
