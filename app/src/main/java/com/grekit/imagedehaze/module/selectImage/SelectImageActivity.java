package com.grekit.imagedehaze.module.selectImage;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grekit.imagedehaze.R;
import com.grekit.imagedehaze.base.BaseActivity;
import com.grekit.imagedehaze.helper.DialogFactory;
import com.grekit.imagedehaze.helper.SimpleDialog;
import com.grekit.imagedehaze.module.dehaze.ImageActivity;
import com.grekit.imagedehaze.utils.BitmapUtils;
import com.grekit.imagedehaze.utils.ImageDehaze;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectImageActivity extends BaseActivity<SelectImagePresenter, ISelectImageView> implements ISelectImageView {

    @BindView(R.id.btn_select_image)
    Button mBtnSelectImage;

    public static final int GET_IMAGE = 10;
    private SimpleDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_select_image)
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_image:
                //进入相册选择图片
                enterAlbum();
                break;
        }
    }

    /**
     * 进入相册
     */
    public void enterAlbum() {
        //intent可以应用于广播和发起意图，其中属性有：ComponentName,action,data等
        Intent intent = new Intent();
        intent.setType("image/*");
        //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
        //类型的内容给你选择
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
        startActivityForResult(intent, GET_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                BitmapUtils.saveBitmap2file(bitmap,"oriImage");
//                image.setImageBitmap(bitmap);
//                //开启线程做网络操作
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mImageadler.sendEmptyMessage(0x1);
//                        //获取清晰图片
//                        clearBitmap = ImageDehaze.getInstance().getDehazeImage(oriBitmap);
//                        if (clearBitmap != null && oriBitmap != null) {
//                            mImageadler.sendEmptyMessage(0x2);
//                        }
//                    }
//                }).start();
                Intent intent = new Intent(SelectImageActivity.this, ImageActivity.class);
                startActivity(intent);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else {
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
            Toast.makeText(this, "操作异常！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_selectimage_layout;
    }

    @Override
    protected Class<ISelectImageView> getViewClass() {
        return ISelectImageView.class;
    }

    @Override
    protected Class<SelectImagePresenter> getPresenterClass() {
        return SelectImagePresenter.class;
    }
}
