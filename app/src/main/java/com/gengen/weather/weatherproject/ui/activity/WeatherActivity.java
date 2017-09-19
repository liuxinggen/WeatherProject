package com.gengen.weather.weatherproject.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gengen.weather.weatherproject.Bean.Forecast;
import com.gengen.weather.weatherproject.Bean.Weather;
import com.gengen.weather.weatherproject.R;
import com.gengen.weather.weatherproject.Utils.Constans;
import com.gengen.weather.weatherproject.Utils.GlideImageLoader;
import com.gengen.weather.weatherproject.net.OkHttpUtils;
import com.gengen.weather.weatherproject.net.Utility;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carwashText;
    private TextView sportText;
    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;
    private Button navBtn;

    private ImageView bingpicIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //背景图片和状态栏融合
        if (Build.VERSION.SDK_INT >= 25) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        String bingPic = sp.getString("bingpic", null);
        if (bingPic != null) {
            GlideImageLoader.init()
                    .displayImage(WeatherActivity.this, bingPic, bingpicIm);
//            Glide.with(WeatherActivity.this).load(bingPic).into(bingpicIm);
        } else {
            loadBingPic();
        }

        final String weatherId;
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存的式去服务器查询数据
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    /**
     * 每日一图
     */
    private void loadBingPic() {
        OkHttpUtils.sendOkHttpRequest(Constans.IMAGE_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                                .edit();
                editor.putString("bingpic", responseText);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlideImageLoader.init().displayImage(WeatherActivity.this,
                                responseText, bingpicIm);
//                        Glide.with(WeatherActivity.this).load(responseText).into(bingpicIm);
                    }
                });


            }
        });


    }

    /**
     * 从服务器获取数据
     *
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = Constans.URL + "?cityid=" + weatherId + "&key=" + Constans.KEY;
        OkHttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respinseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(respinseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager
                                            .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", respinseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }

                });


            }
        });
        loadBingPic();


    }

    /**
     * 处理并展示天气信息
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carwashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carwashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navBtn = (Button) findViewById(R.id.nav_btn);
        bingpicIm = (ImageView) findViewById(R.id.bing_pic_img);
        //
        swipeRefresh.setColorSchemeColors
                (getResources().getColor(R.color.colorAccent),
                        getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.colorPrimaryDark));


    }
}
