package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class Cordinate {

    @SerializedName(Constants.GSON_LAT)
    String lat;

    @SerializedName(Constants.GSON_LON)
    String lon;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
