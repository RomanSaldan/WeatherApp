package com.lynx.weatherapp;

import com.lynx.weatherapp.model.ResponseData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by WORK on 17.08.2015.
 */
public interface ApiInterface {

    @GET("/data/2.5/weather")
    ResponseData getWeather(@Query("q") String city);

    @GET("/data/2.5/weather")
    ResponseData getWeatherByGPS(@Query("lat") String lat, @Query("lon") String lon);

}
