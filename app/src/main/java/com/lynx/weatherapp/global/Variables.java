package com.lynx.weatherapp.global;

import android.graphics.drawable.Drawable;

/**
 * Created by WORK on 17.08.2015.
 */
public class Variables {

    private static Drawable     image;
    private static String       currentLat;
    private static String       currentLng;
    private static String       cityName;

    public static Drawable getImage() {
        return image;
    }

    public static void setImage(Drawable image) {
        Variables.image = image;
    }

    public static String getCurrentLat() {
        return currentLat;
    }

    public static void setCurrentLat(String currentLat) {
        Variables.currentLat = currentLat;
    }

    public static String getCurrentLng() {
        return currentLng;
    }

    public static void setCurrentLng(String currentLng) {
        Variables.currentLng = currentLng;
    }

    public static String getCityName() {
        return cityName;
    }

    public static void setCityName(String cityName) {
        Variables.cityName = cityName;
    }
}
