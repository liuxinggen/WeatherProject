package com.gengen.weather.weatherproject.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.widget.Toast;

import com.gengen.weather.weatherproject.Bean.Weather;
import com.gengen.weather.weatherproject.Utils.Constans;
import com.gengen.weather.weatherproject.Utils.GlideImageLoader;
import com.gengen.weather.weatherproject.Utils.LogUtils;
import com.gengen.weather.weatherproject.net.OkHttpUtils;
import com.gengen.weather.weatherproject.net.Utility;
import com.gengen.weather.weatherproject.ui.activity.WeatherActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    int i = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//8小时
//        int anHour = 3000;//3秒
//        LogUtils.i("sevice", "输出： " + i++);
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新每日一图
     */
    private void updateBingPic() {
        OkHttpUtils.sendOkHttpRequest(Constans.IMAGE_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this)
                                .edit();
                editor.putString("bingpic", responseText);
                editor.apply();
            }
        });

    }

    /**
     * 更新天气
     */
    private void updateWeather() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
        String weatherString = sp.getString("weather", null);

        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = Constans.URL + "?cityid=" + weatherId + "&key=" + Constans.KEY;
            OkHttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String respinseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(respinseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor =
                                PreferenceManager
                                        .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", respinseText);
                        editor.apply();
                    } else {
                        Toast.makeText(AutoUpdateService.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
