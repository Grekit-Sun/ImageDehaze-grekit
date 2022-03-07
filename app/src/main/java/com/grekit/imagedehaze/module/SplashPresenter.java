package com.grekit.imagedehaze.module;

import com.grekit.imagedehaze.base.IPresenter;

public class SplashPresenter implements IPresenter {

    private ISplashView mView;

    public SplashPresenter(ISplashView view) {
        mView = view;
        mView.startToMainNow();
    }
}
