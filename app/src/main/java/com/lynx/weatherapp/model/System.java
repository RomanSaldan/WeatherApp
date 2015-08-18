package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class System {

    @SerializedName(Constants.GSON_MESSAGE)
    String msg;

    @SerializedName(Constants.GSON_COUNTRY)
    String country;

    @SerializedName(Constants.GSON_SUNRISE)
    String sunrise;

    @SerializedName(Constants.GSON_SUNSET)
    String sunset;

    public String getMsg() {
        return msg;
    }

    public String getCountry() {
        return country;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }
}
