package com.example.weatherapp;
import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.weatherapp.api.WeatherAppApiService;
import com.example.weatherapp.models.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {

    private WeatherAppApiService weatherAppApiService;
    private static final int LOCATION_PERMISSION_CODE = 100;
    private LocationStatusReceiver locationStatusReceiver;
    private InternetStateReceiver internetStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationStatusReceiver = new LocationStatusReceiver();
        internetStateReceiver = new InternetStateReceiver();

        IntentFilter networkFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetStateReceiver, networkFilter);

        weatherAppApiService = retrofit.create(WeatherAppApiService.class);

        checkLocationPermission();

        checkGpsStatus();

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        locationFilter.addAction("android.location.MODE_CHANGED");
        registerReceiver(locationStatusReceiver, locationFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (locationStatusReceiver != null) {
                unregisterReceiver(locationStatusReceiver);
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            if (internetStateReceiver != null) {
                unregisterReceiver(internetStateReceiver);
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private void fetchWeather(double latitude, double longitude) {
        String apiKey = "aae65083caf4b5bc4661be746033a539";
        String units = "metric";

        weatherAppApiService.getWeatherByCoordinates(latitude, longitude, apiKey, units).enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    String city = weather.name;
                    String temp = Math.round(weather.main.temp) + "°C";
                    String condition = weather.weather[0].description.toLowerCase();

                    updateWeatherIcon(city, temp, condition);

                }
                else {
                    showError("شهر مورد نظر پیدا نشد");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable throwable) {
                showError("خطا در دریافت اطلاعات");
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
        else {
            getCurrentLocationWeather();
        }
    }

    private void getCurrentLocationWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        fetchWeather(latitude, longitude);
                    }
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(this, "خطا در دریافت موقعیت" + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
        ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationWeather();
            }
            else {
                Toast.makeText(this, "برنامه بدون مجوز موقعیت کار نمی کند", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateWeatherIcon(String city, String temp, String condition) {
        TextView CityTitle = findViewById(R.id.CityTitle);
        TextView CityTemp = findViewById(R.id.CityTemp);
        TextView CityDescription = findViewById(R.id.CityDescription);
        ImageView WeatherStatusIcon = findViewById(R.id.WeatherStatusIcon);

        String conditionPersianTranslation = "";
        int iconResource;

        switch (condition.toLowerCase()) {
            case "clear sky":
            case "clear":
                iconResource = R.drawable.sunny;
                conditionPersianTranslation = "صاف";
                break;

            case "few clouds":
            case "scattered clouds":
            case "broken clouds":
                iconResource = R.drawable.broken_clouds;
                conditionPersianTranslation = "ابر های پراکنده";
                break;

            case "overcast clouds":
            case "cloudy":
                iconResource = R.drawable.cloudy;
                conditionPersianTranslation = "ابری";
                break;

            case "windy":
            case "squalls":
                iconResource = R.drawable.windy;
                conditionPersianTranslation = "باد";
                break;

            case "light rain":
            case "moderate rain":
            case "heavy intensity rain":
            case "shower rain":
            case "rain":
                iconResource = R.drawable.rainy;
                conditionPersianTranslation = "بارانی";
                break;

            case "light snow":
            case "snow":
            case "heavy snow":
            case "sleet":
                iconResource = R.drawable.snowy;
                conditionPersianTranslation = "برفی";
                break;

            default:
                iconResource = R.drawable.unknown_weather;
                conditionPersianTranslation = "آب و هوای نامشخص";
        }

        WeatherStatusIcon.setImageResource(iconResource);
        CityTitle.setText(city);
        CityTemp.setText(temp);
        CityDescription.setText(conditionPersianTranslation);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGpsEnabled) {
                Toast.makeText(this, "موقعیت مکانی فعال نیست", Toast.LENGTH_SHORT).show();
            }
        }
    }
}