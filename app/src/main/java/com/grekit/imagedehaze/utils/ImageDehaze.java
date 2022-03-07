package com.grekit.imagedehaze.utils;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.grekit.imagedehaze.module.AuthBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 单例
 */
public class ImageDehaze {

    public static final String GRANT_TYPE = "client_credentials";
    public static final String CLIENT_ID = "IwQct0tQg73avAuMSI1zzGb6";
    public static final String CLIENT_SECRET = "d46M7hS73ZeaAr8PLsYXrNVze0izANqD";

    public static final String URL_ACCESSTOKEN = "https://aip.baidubce.com/oauth/2.0/token";
    public static final String URL_DEHAZE = "https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze";


    private static ImageDehaze mImageDehaze = null;

    private ImageDehaze() {
    }

    public static ImageDehaze getInstance() {
        if (mImageDehaze == null) {
            synchronized (ImageDehaze.class) {
                if (mImageDehaze == null) {
                    mImageDehaze = new ImageDehaze();
                }
            }
        }
        return mImageDehaze;
    }

    /**
     * 获取去雾后的图片
     */
    public void getDehazeImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        //获取token
        HashMap<String, String> map_token = new HashMap<>();
        HashMap<String, String> map_dehaze = new HashMap<>();
        map_token.put("grant_type", GRANT_TYPE);
        map_token.put("client_id", CLIENT_ID);
        map_token.put("client_secret", CLIENT_SECRET);

        Call call = post(map_token, URL_ACCESSTOKEN);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                AuthBean authBean = gson.fromJson(response.toString(), AuthBean.class);
                if (authBean != null) {
                    map_dehaze.put("image", BitmapUtils.bitmaoToBase64(bitmap));
                    map_dehaze.put("access_token", authBean.access_token);
                    Call call1 = post(map_dehaze, URL_DEHAZE);
                    call1.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            BitmapUtils.base64ToBitmap(response.toString());
                        }
                    });
                }
            }
        });
    }


    public Call post(Map<String, String> map, String url) {
        Call call = null;
        if (map == null) {
            return call;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder()
                .post(builder.build())
                .url(url)
                .build();
        //调用okhttpClient对象实现callbasck方法
        return client.newCall(request);

    }

}
