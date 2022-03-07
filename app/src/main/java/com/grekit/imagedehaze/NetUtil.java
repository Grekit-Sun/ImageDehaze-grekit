package com.grekit.imagedehaze;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class NetUtil {
//    https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze
//    public String getToken() {
//        String url = "https://aip.baidubce.com/oauth/2.0/token ";
//        Map map = new HashMap<String,String>();
//        map.put("grant_type","client_credentials");
//        map.put("client_id","IwQct0tQg73avAuMSI1zzGb6");
//        map.put("client_secret","d46M7hS73ZeaAr8PLsYXrNVze0izANqD");
//        post(map,url);
//    }

    public void dehaze(String img){
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze";
        String param = "image=" + img;
        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
//        String accessToken = getToken()
//        String result = post(url, accessToken, param);;
    }

    public void post(Map<String, String> map, String para) {
        try {
            URL url = new URL(para);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);//表示从服务器获取数据
            connection.setDoOutput(true);//表示向服务器写数据
            connection.setRequestMethod("POST");
            //是否使用缓存
            connection.setUseCaches(false);
            //表示设置请求体的类型是文本类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
