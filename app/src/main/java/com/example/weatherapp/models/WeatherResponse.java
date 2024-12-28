package com.example.weatherapp.models;

public class WeatherResponse {
    public String name;
    public Main main;
    public Weather[] weather;

    public class Main {
        public double temp;
    }

    public class Weather {
        public String description;
    }
}
