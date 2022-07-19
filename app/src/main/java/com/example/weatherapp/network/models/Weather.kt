package com.example.weatherapp.network.models

data class Weather (

     //Weather condition id
     var id: Double,

     //Group of weather parameters (Rain, Snow, Extreme etc.)
     var main: String,

     //Weather condition within the group.
     var description: String,

     //Weather icon id
     var icon: String,
)