package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 21.08.2015.
 */
public class Prediction {

    @SerializedName(Constants.GSON_DESCRIPTION)
    String description;

    public String getDescription() {
        return description;
    }
}
