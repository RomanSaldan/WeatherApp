package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class Main {

    @SerializedName(Constants.GSON_TEMP)
    String temp;

    @SerializedName(Constants.GSON_TEMP_MAIN)
    String temp_min;

    @SerializedName(Constants.GSON_TEMP_MAX)
    String temp_max;

    @SerializedName(Constants.GSON_PRESSURE)
    String pressure;

    @SerializedName(Constants.GSON_SEA_LEVEL)
    String sea_level;

    @SerializedName(Constants.GSON_GROUND_LEVEL)
    String grnd_level;

    @SerializedName(Constants.GSON_HUMIDITY)
    String humidity;

    public String getTemp() {
        return temp;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getPressure() {
        return pressure;
    }

    public String getSea_level() {
        return sea_level;
    }

    public String getGrnd_level() {
        return grnd_level;
    }

    public String getHumidity() {
        return humidity;
    }
}
