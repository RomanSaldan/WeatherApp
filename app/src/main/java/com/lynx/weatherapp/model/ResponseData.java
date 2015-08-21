package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class ResponseData {

    @SerializedName(Constants.GSON_COORD)
    Cordinate cordinate;

    @SerializedName(Constants.GSON_SYS)
    System sys;

    @SerializedName(Constants.GSON_MAIN)
    Main main;

    @SerializedName(Constants.GSON_BASE)
    String base;

    @SerializedName(Constants.GSON_DT)
    String dt;

    @SerializedName(Constants.GSON_ID)
    String id;

    @SerializedName(Constants.GSON_NAME)
    String cityName;

    @SerializedName(Constants.GSON_COD)
    String cod;

    @SerializedName(Constants.GSON_WIND)
    Wind wind;

    @SerializedName(Constants.GSON_WEATHER)
    Weather[] weathers;

    @SerializedName(Constants.GSON_CLOUDS)
    Clouds clouds;

    public Weather[] getWeathers() {
        return weathers;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public Cordinate getCordinate() {
        return cordinate;
    }

    public System getSys() {
        return sys;
    }

    public Main getMain() {
        return main;
    }

    public String getBase() {
        return base;
    }

    public String getDt() {
        return dt;
    }

    public String getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCod() {
        return cod;
    }
}
