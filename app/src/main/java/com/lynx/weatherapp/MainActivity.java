package com.lynx.weatherapp;

import android.app.Activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.lynx.weatherapp.global.Constants;
import com.lynx.weatherapp.model.ResponseData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    private SearchView  swCity_AM;

    private GoogleApiClient     mGoogleApiClient;
    private Location            mLastLocation;

    private String mCityName;
    private ArrayList<String> predictions;
    private SimpleCursorAdapter adapter;

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
        buildGoogleApiClient();
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[] {getString(R.string.column_name)},
                new int[] {android.R.id.text1},
                SimpleCursorAdapter.FLAG_AUTO_REQUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        swCity_AM   = (SearchView) menu.findItem(R.id.search).getActionView();
        swCity_AM   .setQueryHint(getString(R.string.sw_hint));
        swCity_AM   .setOnQueryTextListener(this);
        swCity_AM   .setSuggestionsAdapter(adapter);
        swCity_AM.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor c = ((Cursor)swCity_AM.getSuggestionsAdapter().getItem(position));
                String name = c.getString(c.getColumnIndex(getString(R.string.column_name)));
                String[] parts = name.split(",");
                swCity_AM.setQuery(parts[0], true);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                if(checkGooglePlayServices()) {
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
    public boolean onQueryTextChange(final String newText) {
        new Thread(new Runnable() {
            public void run() {
                if(newText.length()>2) {
                    predictions = getPredictions(newText);   // @NULLABLE
                    final MatrixCursor cursor = new MatrixCursor(new String[] {getString(R.string.column_id), getString(R.string.column_name)});
                    int i = 1;
                    if(predictions != null) for(String s : predictions) {
                        cursor.addRow(new Object[] {i++, s});
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.swapCursor(cursor);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
        return false;
    }

    /*Get cities predictions base in passed text*/
    private ArrayList<String> getPredictions(String text) {
        ArrayList<String> result = new ArrayList<>();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        String strURL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                + text + "&types=(cities)&&key=AIzaSyDV9bLFPB2dUKGEc-u9_V9OLHg_2TM81Tg";
        URL url;
        /*Get JSON*/
        try {
            url = new URL(strURL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        /*Parse JSON*/
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray(getString(R.string.json_tag_predictions));
            for (int i = 0; i < predsJsonArray.length(); i++) {
                result.add(predsJsonArray.getJSONObject(i).getString(getString(R.string.json_tag_description)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
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
                return new MyAsyncLoader(getApplicationContext(),
                        args.getString(getString(R.string.key_city_name)));
            case Constants.WEATHER_GPS_LOADER:
                return new MyAsyncLoader(getApplicationContext(),
                        args.getString(getString(R.string.key_lat)),
                        args.getString(getString(R.string.key_lon)));
        }
        return null; // never
    }

    @Override
    public void onLoadFinished(Loader<ResponseData> loader, ResponseData data) {
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
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
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
