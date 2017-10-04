package com.example.unnati.yahoo_weather_app;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.unnati.yahoo_weather_app.R.string.location;

/**
 * Created by Unnati on 9/13/2017.
 */

public class Background_service extends Service implements LocationListener {

    TimerTask timerTask2;
    Timer timer = new Timer();
    Handler handler2 = new Handler();

    protected LocationManager locationManager;
    private Location location;

    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 minute


    private Boolean isGPSEnabled = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {

                        getCurrentLocation();
                       // Toast.makeText(backgroundservice.this,altd + "-" + lntd, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        };



        timer.scheduleAtFixedRate(timerTask2, 1000, 10000);
        return START_STICKY;
    }

    private void getCurrentLocation() {

        //Taking current location lat-long
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            Toast.makeText(Background_service.this, "Please turn on GPS", Toast.LENGTH_LONG).show();
            //showSettingsAlert();
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

                String data="";

                if(locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location!=null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("Lat-Long: ", latitude+" "+longitude);
                        Toast.makeText(Background_service.this,latitude+" "+longitude,Toast.LENGTH_LONG);

                        data = latitude+" "+longitude;

                        sendDataToActivity(data);

                    }

                }

            }
        }

    }

    private void sendDataToActivity(String newData){

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("serviceToActivityAction");
        broadcastIntent.putExtra("latlongData",newData);
        sendBroadcast(broadcastIntent);

    }





    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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
