package com.gengen.weather.weatherproject.net;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.net
 * Create by lxg
 * on 2017/9/18
 * at 17:31
 */
public class OkHttpUtils {

    public static void sendOkHttpRequest(String adress, okhttp3.Callback callBack) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(adress).build();
        client.newCall(request).enqueue(callBack);

    }

}
