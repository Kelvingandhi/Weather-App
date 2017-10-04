package com.example.unnati.yahoo_weather_app.data;

import org.json.JSONObject;

/**
 * Created by Unnati on 9/2/2017.
 */

public class Item implements JSONPopulator {

    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populate(JSONObject data) {

        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));

    }
}
