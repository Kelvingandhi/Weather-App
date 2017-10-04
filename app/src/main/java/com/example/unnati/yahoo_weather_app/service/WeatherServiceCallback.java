package com.example.unnati.yahoo_weather_app.service;

import com.example.unnati.yahoo_weather_app.data.Channel;

/**
 * Created by Unnati on 9/2/2017.
 */

public interface WeatherServiceCallback {

    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);

}
