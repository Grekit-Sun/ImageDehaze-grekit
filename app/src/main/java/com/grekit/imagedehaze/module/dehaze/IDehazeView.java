package com.grekit.imagedehaze.module.dehaze;

import com.grekit.imagedehaze.base.IBaseView;

public interface IDehazeView extends IBaseView {

    void getDehazeImage(byte[] dehazeImage);
}
