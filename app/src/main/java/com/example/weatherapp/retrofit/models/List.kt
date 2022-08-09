package com.example.weatherapp.retrofit.models

import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class List (

    //Time of data forecasted
    @SerializedName("dt")
    val dt:Double,

    @SerializedName("main")
    val main: Main,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("sys")
    val sys:Sys,

    //Time of data forecasted
    @SerializedName("dt_txt")
    val dt_txt:String
    )