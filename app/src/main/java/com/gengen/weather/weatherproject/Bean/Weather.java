package com.gengen.weather.weatherproject.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Bean
 * Create by lxg
 * on 2017/9/19
 * at 14:01
 */
public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;


}
