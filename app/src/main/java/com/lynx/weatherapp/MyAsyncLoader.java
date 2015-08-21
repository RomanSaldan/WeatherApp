package com.lynx.weatherapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.model.ResponseData;

import retrofit.RestAdapter;


/**
 * Created by WORK on 18.08.2015.
 */
public class MyAsyncLoader extends AsyncTaskLoader<ResponseData> {

    private String mCityName;
    private String mLat;
    private String mLon;
    private RestAdapter mRestAdapter;
    private ApiInterface mApiInterface;

    private ResponseData mData;

    /*Overloaded for city name*/
    public MyAsyncLoader(Context context, String cityName) {
        super(context);
        this.mCityName = cityName;
        mRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.BASE_URL).build();
        mApiInterface = mRestAdapter.create(ApiInterface.class);
    }

    /*Overloaded for coordinates*/
    public MyAsyncLoader(Context context, String lat, String lon) {
        super(context);
        this.mLat = lat;
        this.mLon = lon;
        mRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.BASE_URL).build();
        mApiInterface = mRestAdapter.create(ApiInterface.class);
    }

    @Override
    public ResponseData loadInBackground() {    // magic is here
        if(mCityName != null) {
            mData = mApiInterface.getWeather(mCityName);
            return mData;
        }
            mData = mApiInterface.getWeatherByGPS(mLat, mLon);
        return mData;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
