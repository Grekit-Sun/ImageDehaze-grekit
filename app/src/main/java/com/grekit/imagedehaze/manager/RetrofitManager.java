package com.grekit.imagedehaze.manager;

import com.grekit.imagedehaze.constant.NetConstant;

import java.util.concurrent.TimeUnit;

import interceptor.LogInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitManager（单例）
 */
public class RetrofitManager<T> {

    private static RetrofitManager mRetrofitManager;

    private RetrofitManager() {
    }

    public static RetrofitManager getInstance() {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null) {
                    mRetrofitManager = new RetrofitManager();
                }
            }
        }
        return mRetrofitManager;
    }


    private static OkHttpClient initOkHttp() {
        OkHttpClient mClient = new OkHttpClient.Builder()
                //添加公共查询参数
                //.addInterceptor(new CommonQueryParamsInterceptor())
                //.addInterceptor(new MutiBaseUrlInterceptor())
                //添加header
//                .addInterceptor(new HeaderInterceptor())
//                .addInterceptor(new LogInterceptor())//添加请求拦截(可以在此处打印请求信息和响应信息)
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .connectTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NetConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //添加https证书,如果有srca.cer的证书，则可以通过sslSocketFactory()配置
                //.sslSocketFactory(getSSLSocketFactory(context, "srca.cer"))
                .build();
        return mClient;
    }

    public static Retrofit getDefaultRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .client(initOkHttp())
                .baseUrl(NetConstant.baseUrl)
                //添加GSON解析：返回数据转换成GSON类型
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rxjava支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public static Retrofit getSpecialUrlRetrofit(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .client(initOkHttp())
                .baseUrl(url)
                //添加GSON解析：返回数据转换成GSON类型
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rxjava支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

}
