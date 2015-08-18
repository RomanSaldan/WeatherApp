package com.lynx.weatherapp;

import android.app.Activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import com.google.android.gms.location.LocationServices;
import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.global.Variables;
import com.lynx.weatherapp.model.ResponseData;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.Date;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<ResponseData>,
                                                        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ProgressDialog pbProgress_AM;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if(Variables.getImage() != null) ivWeather_AM.setImageDrawable(Variables.getImage());   // restore image
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
        Variables.setCityName(query);
        getLoaderManager().restartLoader(Constants.WEATHER_LOADER_ID, null, this);
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
        pbProgress_AM = ProgressDialog.show(this, getString(R.string.progress_dialog_title), getString(R.string.progress_dialog_msg), true);
        switch (id) {
            case Constants.WEATHER_LOADER_ID:
                Log.d("myLogs", "Loader with city name created");       // delete in release
                return new MyAsyncLoader(this, Variables.getCityName());
            case Constants.WEATHER_GPS_LOADER:
                Log.d("myLogs", "Loader with location created");        // delete in release
                return new MyAsyncLoader(this, Variables.getCurrentLat(), Variables.getCurrentLng());
        }
        return null; // never
    }

    @Override
    public void onLoadFinished(Loader<ResponseData> loader, ResponseData data) {
        Log.d("myLogs", "onLoadFinished(). Cod = " + data.getCod());    // delete in release

        if(data.getCod().equals(String.valueOf(HttpURLConnection.HTTP_OK))) {
            Picasso.with(this).load(
                    String.format(getString(R.string.FORMAT_IMG), data.getWeathers()[0].getIcon())).into(ivWeather_AM);
            tvCityName_AM.setText(String.format(getString(R.string.FORMAT_CITY_NAME), data.getCityName()));
            tvSpeed_AM.setText(String.format(getString(R.string.FORMAT_WIND_SPEED), data.getWind().getSpeed()));
            tvDegree_AM.setText(String.format(getString(R.string.FORMAT_DEGREE),data.getWind().getDeg()));
            tvTemperature_AM.setText(String.format(getString(R.string.FORMAT_TEMP), convertKtoC(data.getMain().getTemp())));
            tvHumidity_AM.setText(String.format(getString(R.string.FORMAT_HUMIDITY),data.getMain().getHumidity()));
            tvSunrise_AM.setText(String.format(getString(R.string.FORMAT_SUNRISE),new Date(Long.parseLong(data.getSys().getSunrise()) * 1000).toString()));
            tvSunset_AM.setText(String.format(getString(R.string.FORMAT_SUNSET), new Date(Long.parseLong(data.getSys().getSunset()) * 1000).toString()));
            pbProgress_AM.dismiss();
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new ErrorDialogFragment().show(getFragmentManager(), getString(R.string.ERR_DIALOG_TAG));
                }
            });
            pbProgress_AM.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<ResponseData> loader) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        swCity_AM.clearFocus();
        Variables.setImage(ivWeather_AM.getDrawable());
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Variables.setCurrentLat(String.valueOf(mLastLocation.getLatitude()));
            Variables.setCurrentLng(String.valueOf(mLastLocation.getLongitude()));
        }
        getLoaderManager().restartLoader(Constants.WEATHER_GPS_LOADER, null, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
