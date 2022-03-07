package com.grekit.imagedehaze.module.dehaze;

import com.grekit.imagedehaze.helper.HttpSubscriber;
import com.grekit.imagedehaze.manager.RetrofitManager;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DehazeModel {

    private DehazeService mDehazeService;

    public DehazeModel() {
        mDehazeService = RetrofitManager.getInstance().getDefaultRetrofit().create(DehazeService.class);
    }

    public void getToken(HttpSubscriber httpSubscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        map.put("client_id", "IwQct0tQg73avAuMSI1zzGb6");
        map.put("client_secret", "d46M7hS73ZeaAr8PLsYXrNVze0izANqD");
        mDehazeService
                .getToken(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //绑定生命周期
                .subscribe(httpSubscriber);
    }

    public void getDehazeImg(HttpSubscriber httpSubscriber, String access_token, String img) {
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("image", img);
        mDehazeService
                .getDehazeImg(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //绑定生命周期
                .subscribe(httpSubscriber);
    }
}
