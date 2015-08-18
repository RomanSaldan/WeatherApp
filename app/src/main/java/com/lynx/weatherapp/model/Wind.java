package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 17.08.2015.
 */
public class Wind {

    @SerializedName("speed")
    String speed;

    @SerializedName("deg")
    String deg;

    public String getSpeed() {
        return speed;
    }

    public String getDeg() {
        return deg;
    }

}
