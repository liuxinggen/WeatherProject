package com.gengen.weather.weatherproject.Utils;

import android.util.Log;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Utils
 * Create by lxg
 * on 2017/9/20
 * at 9:36
 */
public class LogUtils {
    /**
     * 日志工具类
     * 在编写一个项目的时候在很多地方都打印了日志，在项目完成之后，
     * 之前用于调试的日志，依旧会打印的，这样不仅会降低程序的运行效率，
     * 还有可能将一些机密性的数据泄漏出去。
     * 因此我们要做到在程序开发阶段就让日志打印出来，等到程序上线之后就把
     * 日志给屏蔽掉。
     * 此工具类中的每一个自定义方法中都有一个if判断，当level的值小于或等于对应日志级别的值
     * 才会将日志给打印出来，我们只需在控制level的级别，来控制日志的打印
     */

    public static final int VERBOSE = 1;//打印所有的日志
    public static final int DEBUG = 2;//打印debug的日志
    public static final int INFO = 3;//info
    public static final int WARN = 4;//warn
    public static final int ERROR = 5;//error
    public static final int NOTHING = 6;//把所有的日志都屏蔽掉
    private static int level = VERBOSE;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }

}
