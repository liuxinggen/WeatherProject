package com.gengen.weather.weatherproject.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Utils
 * Create by lxg
 * on 2017/9/19
 * at 17:08
 */
public class GlideImageLoader {

    private static GlideImageLoader glideImageLoader = new GlideImageLoader();

    public static GlideImageLoader init() {
        return glideImageLoader;
    }


    public void displayImage(Context context, Object path, ImageView imageView) {
        if (Util.isOnMainThread()) {
            //Glide 加载图片用法
            Glide.with(context).load(path).into(imageView);
        }
    }
}