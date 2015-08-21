package com.lynx.weatherapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.model.ResponseQuery;

import retrofit.RestAdapter;

/**
 * Created by WORK on 21.08.2015.
 */
public class QueryAsyncLoader extends AsyncTaskLoader<ResponseQuery> {

    private String query;
    private RestAdapter mRestAdapter;
    private ApiInterface mApiInterface;

    public QueryAsyncLoader(Context context, String query) {
        super(context);
        this.query = query;
        mRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.BASE_QUERY_URL).build();
        mApiInterface = mRestAdapter.create(ApiInterface.class);
    }

    @Override
    public ResponseQuery loadInBackground() {
        return mApiInterface.getPredictionsModel(query, Constants.RF_QUERY_CITIES, Constants.API_KEY);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
