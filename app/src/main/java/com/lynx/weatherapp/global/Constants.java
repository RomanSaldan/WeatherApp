package com.lynx.weatherapp.global;

/**
 * Created by WORK on 17.08.2015.
 */
public class Constants {
    public static final int     WEATHER_LOADER_ID                       = 1;
    public static final int     WEATHER_GPS_LOADER                      = 2;
    public static final int     QUERY_LOADER_ID                         = 3;
    public static final String  BASE_URL                                = "http://api.openweathermap.org";
    public static final String  BASE_QUERY_URL                          = "https://maps.googleapis.com";
    public static final int     REQUEST_CODE_RECOVER_PLAY_SERVICES      = 765;

    public static final String  API_KEY                                 = "AIzaSyDV9bLFPB2dUKGEc-u9_V9OLHg_2TM81Tg";

    /*GSON serialized annotations*/
    public static final String GSON_ALL                 = "all";
    public static final String GSON_LAT                 = "lat";
    public static final String GSON_LON                 = "lon";
    public static final String GSON_TEMP                = "temp";
    public static final String GSON_TEMP_MAIN           = "temp_min";
    public static final String GSON_TEMP_MAX            = "temp_max";
    public static final String GSON_PRESSURE            = "pressure";
    public static final String GSON_SEA_LEVEL           = "sea_level";
    public static final String GSON_GROUND_LEVEL        = "grnd_level";
    public static final String GSON_HUMIDITY            = "humidity";
    public static final String GSON_MESSAGE             = "message";
    public static final String GSON_COUNTRY             = "country";
    public static final String GSON_SUNRISE             = "sunrise";
    public static final String GSON_SUNSET              = "sunset";
    public static final String GSON_DESCRIPTION         = "description";
    public static final String GSON_COORD               = "coord";
    public static final String GSON_SYS                 = "sys";
    public static final String GSON_MAIN                = "main";
    public static final String GSON_BASE                = "base";
    public static final String GSON_DT                  = "dt";
    public static final String GSON_ID                  = "id";
    public static final String GSON_NAME                = "name";
    public static final String GSON_COD                 = "cod";
    public static final String GSON_WIND                = "wind";
    public static final String GSON_WEATHER             = "weather";
    public static final String GSON_CLOUDS              = "clouds";
    public static final String GSON_ICON                = "icon";
    public static final String GSON_SPEED               = "speed";
    public static final String GSON_DEG                 = "deg";

    /*Retrofit query constants*/
    public static final String RF_WEATHER_PATH          = "/data/2.5/weather";
    public static final String RF_PREDICTIONS_PATH      = "/maps/api/place/autocomplete/json";
    public static final String RF_QUERY_Q               = "q";
    public static final String RF_QUERY_LAT             = "lat";
    public static final String RF_QUERY_LON             = "lon";
    public static final String RF_QUERY_INPUT           = "input";
    public static final String RF_QUERY_TYPES           = "types";
    public static final String RF_QUERY_KEY             = "key";
    public static final String RF_QUERY_CITIES          = "(cities)";


    /*Bundle keys*/
    public static final String BUNDLE_KEY_QUERY             = "query";
}
