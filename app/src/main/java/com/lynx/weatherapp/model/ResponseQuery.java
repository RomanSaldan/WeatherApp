package com.lynx.weatherapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WORK on 21.08.2015.
 */
public class ResponseQuery {

    @SerializedName("predictions")
    Prediction[] predictions;

    public Prediction[] getPredictions() {
        return predictions;
    }
}
