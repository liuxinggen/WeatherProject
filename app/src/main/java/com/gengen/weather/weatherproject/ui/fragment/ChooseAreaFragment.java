package com.gengen.weather.weatherproject.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gengen.weather.weatherproject.Db.City;
import com.gengen.weather.weatherproject.Db.County;
import com.gengen.weather.weatherproject.Db.Province;
import com.gengen.weather.weatherproject.MainActivity;
import com.gengen.weather.weatherproject.R;
import com.gengen.weather.weatherproject.Utils.Constans;
import com.gengen.weather.weatherproject.net.OkHttpUtils;
import com.gengen.weather.weatherproject.net.Utility;
import com.gengen.weather.weatherproject.ui.activity.WeatherActivity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVENCE = 1;
    private static final int LEVEL_CITY = 2;
    private static final int LEVEL_COUNTY = 3;

    private ProgressDialog progressDialog;

    private TextView tvTitle;
    private Button btnBack;
    private ListView lvView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 三级列表显示
     */
    private List<Province> listProvince;
    private List<City> listCity;
    private List<County> listCounty;

    /**
     * 选中的三级
     */
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    /**
     * 选中的级别
     */
    private int currentLevel;

    private View viewroot;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewroot = inflater.inflate(R.layout.choose_area, container, false);
        tvTitle = (TextView) viewroot.findViewById(R.id.title_text);
        btnBack = (Button) viewroot.findViewById(R.id.back_button);
        lvView = (ListView) viewroot.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, dataList);
        lvView.setAdapter(adapter);
        return viewroot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lvView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVENCE) {
                    selectedProvince = listProvince.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = listCity.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = listCounty.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();

                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();

                }
            }
        });
        queryProvince();
    }

    /**
     * 查询所有的省，优先从数据库中查询，数据库中没有就从服务器上查询
     */
    private void queryProvince() {
        tvTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        listProvince = DataSupport.findAll(Province.class);
        if (listProvince.size() > 0) {
            dataList.clear();
            for (Province province : listProvince) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_PROVENCE;
        } else {
            String address = Constans.WEATHER_URL;
            queryFromServer(address, "province");
        }


    }

    /**
     * 查询所有的城市，优先从数据库中查询，如果没有就从服务器上查询
     */
    private void queryCities() {
        tvTitle.setText(selectedProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        listCity = DataSupport.where("provinceid=?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        if (listCity.size() > 0) {
            dataList.clear();
            for (City city : listCity) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else {
            int provinceId = selectedProvince.getProvinceCode();
            String address = Constans.WEATHER_URL + "/" + provinceId;
            queryFromServer(address, "city");
        }


    }

    /**
     * 查询所有的县区数据，优先从数据库中查询，如果数据库中没有就从服务器查询
     */
    private void queryCounties() {
        tvTitle.setText(selectedCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        listCounty = DataSupport.where("cityid=?",
                String.valueOf(selectedCity.getId())).find(County.class);
        if (listCounty.size() > 0) {
            dataList.clear();
            for (County county : listCounty) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = Constans.WEATHER_URL + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }

    }

    /**
     * 根据传入的地址和类型从服务器获取数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        OkHttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse
                            (responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse
                            (responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });

                }

            }
        });


    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    /**
     * 开启进度条
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
