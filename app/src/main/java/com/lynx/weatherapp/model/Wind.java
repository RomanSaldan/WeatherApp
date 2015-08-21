package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class Wind {

    @SerializedName(Constants.GSON_SPEED)
    String speed;

    @SerializedName(Constants.GSON_DEG)
    String deg;

    public String getSpeed() {
        return speed;
    }

    public String getDeg() {
        return deg;
    }

}
