package com.gengen.weather.weatherproject.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Bean
 * Create by lxg
 * on 2017/9/19
 * at 13:56
 */
public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;

    }

}
