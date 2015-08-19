package com.lynx.weatherapp;

import android.app.Activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.model.ResponseData;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.Date;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<ResponseData>,
                                                        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageView   ivWeather_AM;
    private TextView    tvCityName_AM;
    private TextView    tvSpeed_AM;
    private TextView    tvDegree_AM;
    private TextView    tvTemperature_AM;
    private TextView    tvHumidity_AM;
    private TextView    tvSunrise_AM;
    private TextView    tvSunset_AM;

    private SearchView swCity_AM;

    private GoogleApiClient     mGoogleApiClient;
    private Location            mLastLocation;

    private String mCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if(savedInstanceState == null) {
            Bundle initialBundle = new Bundle();
            initialBundle.putString(getString(R.string.key_city_name), getString(R.string.default_city));
            getLoaderManager().initLoader(Constants.WEATHER_LOADER_ID, initialBundle, this);
        } else if(savedInstanceState.containsKey(getString(R.string.key_city_name))) {
            Bundle initialBundle = new Bundle();
            initialBundle.putString(getString(R.string.key_city_name), savedInstanceState.getString(getString(R.string.key_city_name)));
            getLoaderManager().initLoader(Constants.WEATHER_LOADER_ID, initialBundle, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        swCity_AM   = (SearchView) menu.findItem(R.id.search).getActionView();
        swCity_AM   .setQueryHint(getString(R.string.sw_hint));
        swCity_AM   .setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                if(checkGooglePlayServices()) {
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        swCity_AM.clearFocus();
        Bundle nameBundle = new Bundle();
        nameBundle.putString(getString(R.string.key_city_name), query);
        getLoaderManager().restartLoader(Constants.WEATHER_LOADER_ID, nameBundle, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /*Init views*/
    private void initUI() {
        ivWeather_AM        = (ImageView)   findViewById(R.id.ivWeather_AM);
        tvCityName_AM       = (TextView)    findViewById(R.id.tvCityName_AM);
        tvSpeed_AM          = (TextView)    findViewById(R.id.tvSpeed_AM);
        tvDegree_AM         = (TextView)    findViewById(R.id.tvDegree_AM);
        tvTemperature_AM    = (TextView)    findViewById(R.id.tvTemperature_AM);
        tvHumidity_AM       = (TextView)    findViewById(R.id.tvHumidity_AM);
        tvSunrise_AM        = (TextView)    findViewById(R.id.tvSunrise_AM);
        tvSunset_AM         = (TextView)    findViewById(R.id.tvSunset_AM);
    }

    @Override
    public Loader<ResponseData> onCreateLoader(int id, Bundle args) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new ProgressDialogFragment().show(getFragmentManager(), getString(R.string.progress_dialog_tag));
            }
        });
        switch (id) {
            case Constants.WEATHER_LOADER_ID:
                Log.d("myLogs", "Loader with city name created");       // delete in release
                return new MyAsyncLoader(getApplicationContext(),
                        args.getString(getString(R.string.key_city_name)));
            case Constants.WEATHER_GPS_LOADER:
                Log.d("myLogs", "Loader with location created");        // delete in release
                return new MyAsyncLoader(getApplicationContext(),
                        args.getString(getString(R.string.key_lat)),
                        args.getString(getString(R.string.key_lon)));
        }
        return null; // never
    }

    @Override
    public void onLoadFinished(Loader<ResponseData> loader, ResponseData data) {
        Log.d("myLogs", "onLoadFinished(). Cod = " + data.getCod());    // delete in release

        if(data.getCod().equals(String.valueOf(HttpURLConnection.HTTP_OK))) {
            mCityName = data.getCityName();
            String imgUrl = String.format(getString(R.string.FORMAT_IMG), data.getWeathers()[0].getIcon());
            Picasso.with(this).load(imgUrl).into(ivWeather_AM);
            tvCityName_AM.setText(String.format(getString(R.string.FORMAT_CITY_NAME), data.getCityName()));
            tvSpeed_AM.setText(String.format(getString(R.string.FORMAT_WIND_SPEED), data.getWind().getSpeed()));
            tvDegree_AM.setText(String.format(getString(R.string.FORMAT_DEGREE),data.getWind().getDeg()));
            tvTemperature_AM.setText(String.format(getString(R.string.FORMAT_TEMP), convertKtoC(data.getMain().getTemp())));
            tvHumidity_AM.setText(String.format(getString(R.string.FORMAT_HUMIDITY),data.getMain().getHumidity()));
            tvSunrise_AM.setText(String.format(getString(R.string.FORMAT_SUNRISE),new Date(Long.parseLong(data.getSys().getSunrise()) * 1000).toString()));
            tvSunset_AM.setText(String.format(getString(R.string.FORMAT_SUNSET), new Date(Long.parseLong(data.getSys().getSunset()) * 1000).toString()));
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if(getFragmentManager().findFragmentByTag(getString(R.string.progress_dialog_tag)) != null)
                    ((ProgressDialogFragment) getFragmentManager().findFragmentByTag(getString(R.string.progress_dialog_tag))).dismiss();
                }
            });
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new ErrorDialogFragment().show(getFragmentManager(), getString(R.string.ERR_DIALOG_TAG));
                }
            });
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if(getFragmentManager().findFragmentByTag(getString(R.string.progress_dialog_tag)) != null)
                    ((ProgressDialogFragment) getFragmentManager().findFragmentByTag(getString(R.string.progress_dialog_tag))).dismiss();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<ResponseData> loader) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        swCity_AM.clearFocus();
    }

    /*Convert temperature from Kelvins to Celsius*/
    private int convertKtoC(String k) {
        double kelvin = Double.parseDouble(k);
        double celsius = kelvin - 273.15;
        return (int)celsius;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_RECOVER_PLAY_SERVICES) {
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.toast_err_gps,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*Check availability of Google Play Services*/
    private boolean checkGooglePlayServices(){
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, Constants.REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            return false;
        }
        return true;
    }

    /*Prepare Google API Client*/
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        final Bundle locationBundle = new Bundle();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            locationBundle.putString(getString(R.string.key_lat), String.valueOf(mLastLocation.getLatitude()));
            locationBundle.putString(getString(R.string.key_lon), String.valueOf(mLastLocation.getLongitude()));
            getLoaderManager().restartLoader(Constants.WEATHER_GPS_LOADER, locationBundle, this);
        } else {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            Toast.makeText(this, R.string.toast_err_need_gps, Toast.LENGTH_LONG).show();

            LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationBundle.putString(getString(R.string.key_lat), String.valueOf(location.getLatitude()));
                    locationBundle.putString(getString(R.string.key_lon), String.valueOf(location.getLongitude()));
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    getLoaderManager().restartLoader(Constants.WEATHER_GPS_LOADER, locationBundle, MainActivity.this);
                }
            };

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.key_city_name), mCityName);
        super.onSaveInstanceState(outState);
    }
}
