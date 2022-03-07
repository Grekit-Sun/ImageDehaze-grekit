package com.grekit.imagedehaze.module.dehaze;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.grekit.imagedehaze.R;
import com.grekit.imagedehaze.base.BaseActivity;
import com.grekit.imagedehaze.module.AuthBean;
import com.grekit.imagedehaze.module.DehazeBean;
import com.grekit.imagedehaze.utils.BitmapUtils;
import com.grekit.imagedehaze.utils.ImageDehaze;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.grekit.imagedehaze.utils.ImageDehaze.CLIENT_ID;
import static com.grekit.imagedehaze.utils.ImageDehaze.CLIENT_SECRET;
import static com.grekit.imagedehaze.utils.ImageDehaze.GRANT_TYPE;
import static com.grekit.imagedehaze.utils.ImageDehaze.URL_ACCESSTOKEN;
import static com.grekit.imagedehaze.utils.ImageDehaze.URL_DEHAZE;

public class ImageActivity extends BaseActivity<ImagePresenter, IImageView> implements IImageView {

    @BindView(R.id.tv_img_type)
    TextView mImgType;
    @BindView(R.id.img)
    ImageView mImage;

    private Bitmap oriImage;
    private Bitmap dehazeImage;

    private boolean isOri = true;

//    Handler imageHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 0x01:
//                    if(dehazeImage!=null){
//
//                    }
//                    break;
//            }
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        imageDehaze();
    }

    /**
     * 图像去雾
     */
    private void imageDehaze() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (oriImage == null) {
                    return;
                }
                //获取token
                HashMap<String, String> map_token = new HashMap<>();
                HashMap<String, String> map_dehaze = new HashMap<>();
                map_token.put("grant_type", GRANT_TYPE);
                map_token.put("client_id", CLIENT_ID);
                map_token.put("client_secret", CLIENT_SECRET);

                Call call = ImageDehaze.getInstance().post(map_token, URL_ACCESSTOKEN);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();
                        AuthBean authBean = null;
                        if (response != null) {
                            String string = response.body().string();
//                            Log.e("respones:",response.body().string());
                            authBean = gson.fromJson(string, AuthBean.class);
                        }

                        if (authBean != null) {
                            map_dehaze.put("image", BitmapUtils.bitmaoToBase64(oriImage));
                            map_dehaze.put("access_token", authBean.access_token);
                            Call call1 = ImageDehaze.getInstance().post(map_dehaze, URL_DEHAZE);
                            call1.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    DehazeBean dehazeBean = null;
                                    if (response != null) {
                                        String string = response.body().string();
                                        dehazeBean = gson.fromJson(string, DehazeBean.class);
                                    }
                                    if (dehazeBean != null) {
                                        dehazeImage = BitmapUtils.base64ToBitmap(dehazeBean.image);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    private void initView() {
        ButterKnife.bind(this);
        oriImage = BitmapUtils.getBitmapFromFile("oriImage");
        if (oriImage != null) {
            mImage.setImageBitmap(oriImage);
            mImgType.setText("原图");
        }
    }

    @OnClick(R.id.img)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:
                if (isOri) {
                    if (dehazeImage != null) {
                        mImgType.setText("去雾图像");
                        mImage.setImageBitmap(dehazeImage);
                        isOri = false;
                    }
                } else {
                    if (oriImage != null) {
                        mImgType.setText("原图");
                        mImage.setImageBitmap(oriImage);
                        isOri = true;
                    }
                }
                break;
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_image_layout;
    }

    @Override
    protected Class<IImageView> getViewClass() {
        return IImageView.class;
    }

    @Override
    protected Class<ImagePresenter> getPresenterClass() {
        return ImagePresenter.class;
    }
}
