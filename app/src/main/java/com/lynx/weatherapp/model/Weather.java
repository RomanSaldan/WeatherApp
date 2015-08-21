package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.lynx.weatherapp.global.Constants;

/**
 * Created by WORK on 17.08.2015.
 */
public class Weather {

    @SerializedName(Constants.GSON_ID)
    String id;

    @SerializedName(Constants.GSON_MAIN)
    String main;

    @SerializedName(Constants.GSON_DESCRIPTION)
    String description;

    @SerializedName(Constants.GSON_ICON)
    String icon;

    public String getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
