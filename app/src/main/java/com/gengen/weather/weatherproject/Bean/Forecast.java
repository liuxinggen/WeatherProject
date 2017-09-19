package com.gengen.weather.weatherproject.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Bean
 * Create by lxg
 * on 2017/9/19
 * at 13:59
 */
public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;
        public String min;

    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }


}
