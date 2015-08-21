package com.lynx.weatherapp;

import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.model.ResponseData;
import com.lynx.weatherapp.model.ResponseQuery;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by WORK on 17.08.2015.
 */
public interface ApiInterface {

    @GET(Constants.RF_WEATHER_PATH)
    ResponseData getWeather(@Query(Constants.RF_QUERY_Q) String city);

    @GET(Constants.RF_WEATHER_PATH)
    ResponseData getWeatherByGPS(@Query(Constants.RF_QUERY_LAT) String lat,
                                 @Query(Constants.RF_QUERY_LON) String lon);

    @GET(Constants.RF_PREDICTIONS_PATH)
    ResponseQuery getPredictionsModel(@Query(Constants.RF_QUERY_INPUT) String input,
                                      @Query(Constants.RF_QUERY_TYPES) String types,
                                      @Query(Constants.RF_QUERY_KEY) String key);

}
