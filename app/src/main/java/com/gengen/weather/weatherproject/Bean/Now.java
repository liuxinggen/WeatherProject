package com.gengen.weather.weatherproject.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Bean
 * Create by lxg
 * on 2017/9/19
 * at 13:53
 */
public class Now {


    public String tmp;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;

    }

}
