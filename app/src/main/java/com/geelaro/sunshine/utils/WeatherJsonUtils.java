package com.geelaro.sunshine.utils;

import android.text.format.Time;

import com.geelaro.sunshine.beans.WeatherBean;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geelaro on 2017/6/23.
 */

public class WeatherJsonUtils {


    public static String getCity(String jsonStr) {

        JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonElement cityElement = jsonObj.get("city");


        return cityElement.getAsString();
    }

    public static List<WeatherBean> getWeatherInfo(String jsonStr) throws JSONException {
        List<WeatherBean> list = new ArrayList<>();


        return list;
    }

    public static List<WeatherBean> getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {

        List<WeatherBean> weatherBeanList = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        final String OWM_ID="id";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        // Data is fetched in Celsius by default.

        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String weatherDesc;
            int weatherId;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            weatherDesc = weatherObject.getString(OWM_DESCRIPTION);
            weatherId = weatherObject.getInt(OWM_ID);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            long high = formatTemp(temperatureObject.getDouble(OWM_MAX));
            long low = formatTemp(temperatureObject.getDouble(OWM_MIN));

            WeatherBean weather = new WeatherBean();
            weather.setDesc(weatherDesc);
            weather.setMaxTemp(high);
            weather.setMinTemp(low);
            weather.setDate(day);
            weather.setWeatherId(weatherId);
            ShowToast.Short(weatherDesc+day+weatherId);
            weatherBeanList.add(weather);

        }


        return weatherBeanList;
    }

    /**
     * get the weather high/lows temp by round.
     */
    private static Long formatTemp(double temp) {

        long roundedTemp = Math.round(temp);

        return roundedTemp;
    }

    private static String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

}