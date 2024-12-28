package com.example.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.widget.Toast;

public class LocationStatusReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_GPS_STATUS = "gpsStatus";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean previousGpsStatus = prefs.getBoolean(KEY_GPS_STATUS, true);

        if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled && previousGpsStatus) {
                Toast.makeText(context, "موقعیت مکانی فعال نیست", Toast.LENGTH_SHORT).show();
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_GPS_STATUS, isGpsEnabled);
            editor.apply();

        }
    }
}
