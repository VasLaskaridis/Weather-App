package com.example.weatherapp.network.models

data class WeatherResponse (

    //Internal parameter
    val cod: Double,

    //Internal parameter
    val message : Double,

    //Number of timestamps returned by this API call
    val cnt: Double,

    val list: kotlin.collections.List<List>,

    val city: City
)