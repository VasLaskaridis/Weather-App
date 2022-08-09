package com.example.weatherapp.retrofit.models

import com.google.gson.annotations.SerializedName

data class Main(

    //Temperature
    @SerializedName("temp")
    val temp: Double,

    //Minimum temperature at the moment of calculation
    @SerializedName("temp_min")
    val tempMin: Double,

    //Maximum temperature at the moment of calculation
    @SerializedName("temp_max")
    val tempMax: Double,

    //Internal parameter
    @SerializedName("temp_kf")
    val temp_kf:Double
)