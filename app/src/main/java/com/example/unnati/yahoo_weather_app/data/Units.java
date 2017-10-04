package com.example.unnati.yahoo_weather_app.data;

import org.json.JSONObject;

/**
 * Created by Unnati on 9/2/2017.
 */

public class Units implements JSONPopulator{

    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {

        temperature = data.optString("temperature");

    }
}
