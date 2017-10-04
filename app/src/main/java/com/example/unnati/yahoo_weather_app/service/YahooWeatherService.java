package com.example.unnati.yahoo_weather_app.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.unnati.yahoo_weather_app.data.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Unnati on 9/2/2017.
 */

public class YahooWeatherService {

    private WeatherServiceCallback callback;
    private String location;
    private Exception error;

    public YahooWeatherService(WeatherServiceCallback callback) {
        this.callback = callback;
    }

    public String getLocation() {
        return location;
    }

    public void refreshWeather(String loc){

        this.location = loc;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                //String temp="(40.72107367,-74.07537043)";

                //String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")",temp);
                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")",location);
                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

                Log.d(TAG, "Query: "+endpoint);

                try {
                    URL url = new URL(endpoint);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    StringBuilder result = new StringBuilder();

                    while ((line=reader.readLine())!= null)
                    {
                        result.append(line);
                    }

                    return result.toString();

                } catch (Exception e) {
                    error = e;
                }


                return null;
            }

            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);

                if(res == null && error != null)
                {
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    JSONObject data = new JSONObject(res);

                    JSONObject queryData = data.optJSONObject("query");

                    int count = queryData.optInt("count");

                    if(count == 0)
                    {
                        callback.serviceFailure(new locationException("Information unavailable for location "+location));
                        return;
                    }

                    Channel channel = new Channel();
                    channel.populate(queryData.optJSONObject("results").optJSONObject("channel"));

                    callback.serviceSuccess(channel);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(location);

    }

    public class locationException extends Exception{
        public locationException(String message) {
            super(message);
        }
    }

}
