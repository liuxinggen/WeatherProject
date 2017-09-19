package com.gengen.weather.weatherproject.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Bean
 * Create by lxg
 * on 2017/9/19
 * at 13:48
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }


}
