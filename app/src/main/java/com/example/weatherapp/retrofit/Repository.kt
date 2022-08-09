package com.example.weatherapp.retrofit

import com.example.weatherapp.retrofit.models.WeatherResponse
import com.example.weatherapp.util.Resource

class Repository {
    suspend fun getWeatherDataOfCity(apiService: APIService, city:String): Resource<WeatherResponse> {
        return try{
            val response = apiService.getForcast(city)
            val data=response.body()
            if(response.isSuccessful && data!=null){
                Resource.Success(data)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
            Resource.Error(e.message ?: "Error")
        }
    }

    suspend fun getWeatherDataOfLocation(apiService: APIService, lat: Double, lon: Double): Resource<WeatherResponse> {
        return try{
            val response = apiService.getForcastGeo(lat, lon)
            val data=response.body()
            if(response.isSuccessful && data!=null){
                Resource.Success(data)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
            Resource.Error(e.message ?: "Error")
        }
    }
}