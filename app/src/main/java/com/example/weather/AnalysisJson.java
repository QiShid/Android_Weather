package com.example.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnalysisJson {
    public static City getWeather(String json){
        City city = new City();
        String province = null,cityID = null,cityName = null,weather = null,refreshTime = null,temperature = null,humidity = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("lives");
            for(int i=0;i<array.length();i++){
                province = array.getJSONObject(i).getString("province");
                cityID = array.getJSONObject(i).getString("adcode");
                cityName = array.getJSONObject(i).getString("city");
                weather = array.getJSONObject(i).getString("weather");
                refreshTime = array.getJSONObject(i).getString("reporttime");
                temperature = array.getJSONObject(i).getString("temperature");
                humidity = array.getJSONObject(i).getString("humidity");
            }
            if(cityID==null){
                return null;
            }
            else{
                // 构建City对象
                city.setProvince(province);
                city.setCityId(cityID);
                city.setCityName(cityName);
                city.setWeather(weather);
                city.setRefreshTime(refreshTime);
                city.setTemperature(temperature);
                city.setHumidity(humidity);
                return city;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<City> getProvince(String json) throws JSONException {
        List<City> cityList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray array = jsonObject.getJSONArray("districts");
        for(int i=0;i<1;i++){
            array = array.getJSONObject(i).getJSONArray("districts");
        }
        for(int i=0;i<array.length();i++){
            String province = array.getJSONObject(i).getString("name");
            String cityID = array.getJSONObject(i).getString("adcode");
            City city = new City();
            city.setProvince(province);
            city.setCityId(cityID);
            cityList.add(city);
        }
        return cityList;
    }

    public static List<City> getcitys(String json) throws JSONException {
        List<City> cityList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray array = jsonObject.getJSONArray("districts");
        for(int i=0;i<1;i++){
            array = array.getJSONObject(i).getJSONArray("districts");
        }
        for(int i=0;i<array.length();i++){
            String cityName = array.getJSONObject(i).getString("name");
            String cityID = array.getJSONObject(i).getString("adcode");
            City city = new City();
            city.setCityName(cityName);
            city.setCityId(cityID);
            cityList.add(city);
        }
        return cityList;
    }
}
