package com.example.unnati.yahoo_weather_app;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unnati.yahoo_weather_app.data.Channel;
import com.example.unnati.yahoo_weather_app.service.WeatherServiceCallback;
import com.example.unnati.yahoo_weather_app.service.YahooWeatherService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.example.unnati.yahoo_weather_app.R.string.location;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback, LocationListener {

    private ImageView weatherIcon;
    private TextView temperatureTeaxtView;
    private TextView conditionTeaxtView;
    private TextView locationTeaxtView;

    private YahooWeatherService service;


    private Context context;

    TimerTask timerTask2;
    Timer timer = new Timer();
    Handler handler2 = new Handler();

    private ProgressDialog dialog;

    protected LocationManager locationManager;
    private Location location;

    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 minute


    private Boolean isGPSEnabled = false;
    private static Boolean flag=false;

    private String latlongData;
    public static String cityName;


    //use of volley
    RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIcon = (ImageView) findViewById(R.id.weather_icon_imageView);
        temperatureTeaxtView = (TextView) findViewById(R.id.temperaturetextView);
        conditionTeaxtView = (TextView) findViewById(R.id.conditiontextView);
        locationTeaxtView = (TextView) findViewById(R.id.locationtextView);

        service = new YahooWeatherService(this);

        requestQueue = Volley.newRequestQueue(this);

        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("thread time ", "called");

                        getCurrentLocation();
                        // Toast.makeText(backgroundservice.this,altd + "-" + lntd, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        };


        timer.scheduleAtFixedRate(timerTask2, 1000, 10000);

        //service.refreshWeather("Nome, AK");



    }


    private void getCurrentLocation() {

        //Taking current location lat-long
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            flag=false;
            location=null;
            Toast.makeText(MainActivity.this, "Please turn on GPS", Toast.LENGTH_LONG).show();

        } else {

            if (location == null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS Enabled", "GPS Enabled");

                if(locationManager!=null){

                    Log.d("LM", "Enter in LM");

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("Lat-Long: ", latitude+" "+longitude);
                        //Toast.makeText(MainActivity.this,latitude+" "+longitude,Toast.LENGTH_LONG).show();

                        if(!flag) {

                            //flag=true;
                            latlongData = "(" + latitude + "," + longitude + ")";
                            Log.d("Passed Data: ", latlongData);
                            //service.refreshWeather(latlongData);

                            String YQLcity = String.format("select location.city from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")",latlongData);
                            String urlcity = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQLcity));

                            /////////////////////

                            JsonObjectRequest jsonreq = new JsonObjectRequest(urlcity,
                                    null,

                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {

                                                Log.d(VolleyLog.TAG, "onResponse:city= "+response.getJSONObject("query")
                                                        .getJSONObject("results")
                                                        .getJSONObject("channel")
                                                        .getJSONObject("location").getString("city"));


                                                cityName =response.getJSONObject("query")
                                                        .getJSONObject("results")
                                                        .getJSONObject("channel")
                                                        .getJSONObject("location").getString("city");

                                                service.refreshWeather(cityName);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    },

                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            VolleyLog.d(VolleyLog.TAG, "Error: " + error.getMessage());
                                        }
                                    }

                            );

                            requestQueue.add(jsonreq);

                            //////////////////////

                        }

                    }

                }

            }
        }

    }



    @Override
    public void serviceSuccess(Channel channel) {

        int resourceId = getResources().getIdentifier("drawable/icon_"+channel.getItem().getCondition().getCode(),null,getPackageName());

        Log.d(TAG, "serviceSuccess: "+resourceId);

        @SuppressWarnings("deprecation") Drawable weatherIconDrawable = getResources().getDrawable(resourceId);

        weatherIcon.setImageDrawable(weatherIconDrawable);

        temperatureTeaxtView.setText(channel.getItem().getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        locationTeaxtView.setText(service.getLocation());
        conditionTeaxtView.setText(channel.getItem().getCondition().getDescription());

    }

    @Override
    public void serviceFailure(Exception exception) {

        Toast.makeText(MainActivity.this,""+exception,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        getCurrentLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
