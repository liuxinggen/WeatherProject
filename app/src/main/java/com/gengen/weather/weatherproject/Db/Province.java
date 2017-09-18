package com.gengen.weather.weatherproject.Db;

import org.litepal.crud.DataSupport;

/**
 * project name WeatherProject
 * package name com.gengen.weather.weatherproject.Db
 * Create by lxg
 * on 2017/9/18
 * at 16:48
 */
public class Province extends DataSupport {

    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
