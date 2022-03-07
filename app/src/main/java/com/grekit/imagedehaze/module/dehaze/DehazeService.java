package com.grekit.imagedehaze.module.dehaze;

import com.grekit.imagedehaze.module.AuthBean;
import com.grekit.imagedehaze.module.DehazeBean;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface DehazeService {

    @POST("oauth/2.0/token")
    Observable<AuthBean> getToken(@QueryMap HashMap<String, String> map);

    @POST("rest/2.0/image-process/v1/dehaze")
    Observable<DehazeBean> getDehazeImg(@QueryMap HashMap<String, String> map);

}
