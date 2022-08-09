package com.example.weatherapp.retrofit

import com.example.weatherapp.retrofit.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface APIService {

    @GET("data/2.5/forecast?units=metric&appid=01f9aada8314feb90c464117e5bbff2f")
    suspend fun getForcast(
        @Query("q") cityName: String
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast?units=metric&appid=01f9aada8314feb90c464117e5bbff2f")
    suspend fun getForcastGeo(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Response<WeatherResponse>
}

