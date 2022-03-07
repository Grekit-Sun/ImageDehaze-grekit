package com.grekit.imagedehaze.module.dehaze;


import android.util.Base64;
import android.util.Log;

import com.grekit.imagedehaze.base.IPresenter;
import com.grekit.imagedehaze.helper.HttpSubscriber;
import com.grekit.imagedehaze.module.AuthBean;
import com.grekit.imagedehaze.module.DehazeBean;

import io.reactivex.disposables.Disposable;


public class DehazePresenter implements IPresenter {

    private IDehazeView mView;
    private DehazeModel mDehazeModel;
    public static final String TAG = "base64";

    public DehazePresenter(IDehazeView view) {
        mView = view;
        mDehazeModel = new DehazeModel();
    }

    public void dehazeImage(byte[] imageData) {
        if (imageData != null) {
            String base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP);
            mDehazeModel.getToken(new HttpSubscriber<AuthBean>() {
                @Override
                public void onNext(AuthBean authBean) {
                    if (authBean != null) {
                        String token = authBean.access_token;
                        Log.e(TAG, "---------------------------------------");
                        Log.e(TAG, "access_token:" + token);
                        Log.e(TAG, "---------------------------------------");
                        Log.e(TAG, "---------------------------------------");
                        Log.e(TAG, "base64Image:" + base64Image);
                        Log.e(TAG, "---------------------------------------");
                        mDehazeModel.getDehazeImg(new HttpSubscriber<DehazeBean>() {
                            @Override
                            public void onNext(DehazeBean dehazeBean) {
                                byte[] decode = Base64.decode(dehazeBean.image, Base64.NO_WRAP);
                                mView.getDehazeImage(decode);
                            }
                        }, token, base64Image);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        } else {
            mView.getDehazeImage(null);
        }
    }
}