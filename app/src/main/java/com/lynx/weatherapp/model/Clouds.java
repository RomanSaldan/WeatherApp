package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class Clouds {

    @SerializedName(Constants.GSON_ALL)
    String all;

    public String getAll() {
        return all;
    }
}
