package com.example.android.stormy.weather

class Forecast {

    var current: CurrentWeather? = null
    var hourlyForecast: ArrayList<Hour> = ArrayList()
    var dailyForecast: ArrayList<Day> = ArrayList()
}