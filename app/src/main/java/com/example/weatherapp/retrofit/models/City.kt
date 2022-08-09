package com.example.weatherapp.retrofit.models

import com.google.gson.annotations.SerializedName

data class City (

    // City name
    @SerializedName("name")
    val name:String,

    // Country code (GB, JP etc.)
    @SerializedName("country")
    val country:String
)