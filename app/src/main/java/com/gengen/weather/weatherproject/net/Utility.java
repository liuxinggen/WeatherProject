package com.gengen.weather.weatherproject.net;

import android.text.TextUtils;

import com.gengen.weather.weatherproject.Bean.Weather;
import com.gengen.weather.weatherproject.Db.City;
import com.gengen.weather.weatherproject.Db.County;
import com.gengen.weather.weatherproject.Db.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.net
 * Create by lxg
 * on 2017/9/19
 * at 9:29
 */
public class Utility {
    /**
     * 解析和处理服务器端返回的省级数据
     *
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provenceObject = allProvinces.getJSONObject(i);
                    Province provence = new Province();
                    provence.setProvinceName(provenceObject.getString("name"));
                    provence.setProvinceCode(provenceObject.getInt("id"));
                    provence.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理返回的市级数据
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务端返回的县区级数据
     *
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {

                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        return false;
    }

    /**
     * 返回的json数据解析为Weather实体类
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent, Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        return null;
    }


}
