package com.example.weatherapp.ui

import androidx.lifecycle.*
import com.example.weatherapp.network.models.WeatherResponse
import com.example.weatherapp.network.models.List
import com.example.weatherapp.network.RetrofitInstance
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch


class MainActivityViewModel() : ViewModel() {

    val weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

    fun weatherDataObserver(): MutableLiveData<Resource<WeatherResponse>> {
        return weatherData
    }

    fun requestWeatherDataOfCity(CITY: String) {
        viewModelScope.launch {
            weatherData.postValue(Resource.Loading())
            val response = RetrofitInstance.api.getForcast(CITY)
            if (response.isSuccessful) {
                response.body()?.let {
                    weatherData.postValue(Resource.Success(it))
                }
            } else {
                weatherData.postValue(Resource.Error(response.message()))
            }
        }
    }

    fun requestWeatherDataOfLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherData.postValue(Resource.Loading())
            val response = RetrofitInstance.api.getForcastGeo(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    weatherData.postValue(Resource.Success(it))
                }
            } else {
                weatherData.postValue(Resource.Error(response.message()))
            }
        }
    }

    fun getDatesOfForecastedWeather(dataFromResponse: kotlin.collections.List<List>): ArrayList<String> {
        val listOfDates: ArrayList<String> = ArrayList()
        val date = dataFromResponse.get(0).dt_txt.split(" ").toMutableList()
        listOfDates.add(date[0])
        for (i in dataFromResponse) {
            val dateHolder = i.dt_txt.split(" ")
            if (date[0] != dateHolder[0]) {
                date[0] = dateHolder[0]
                listOfDates.add(date[0])
            }
        }
        return listOfDates
    }


    fun getMinMaxTemperaturesOfDates(dataFromResponse: kotlin.collections.List<List>): ArrayList<String> {
        val minMaxTempList: ArrayList<String> = ArrayList()
        //dateAndTimeArray[0] holds the date
        //dateAndTimeArray[1] holds the time
        val dateAndTimeArray = dataFromResponse.get(0).dt_txt.split(" ").toMutableList()
        var minTemp: Double = dataFromResponse.get(0).main.tempMin
        var maxTemp: Double = dataFromResponse.get(0).main.tempMax
        for (i in dataFromResponse) {
            val dateHolder = i.dt_txt.split(" ")
            if (dateAndTimeArray[0] != dateHolder[0]) {
                minMaxTempList.add("" + minTemp.toInt() + "° - " + maxTemp.toInt() + "°")
                minTemp = i.main.tempMin
                maxTemp = i.main.tempMax
                dateAndTimeArray[0] = dateHolder[0]
            } else {
                if (minTemp > i.main.tempMin) {
                    minTemp = i.main.tempMin
                }
                if (maxTemp < i.main.tempMax) {
                    maxTemp = i.main.tempMax
                }
            }
        }
        return minMaxTempList
    }

    fun getWeatherConditionIconIdOfDates(dataFromResponse: kotlin.collections.List<List>): ArrayList<String> {
        val iconIdList: ArrayList<String> = ArrayList()
        val date = dataFromResponse.get(0).dt_txt.split(" ").toMutableList()
        for (i in dataFromResponse) {
            val dateHolder = i.dt_txt.split(" ")
            if (date[0] != dateHolder[0]) {
                iconIdList.add(i.weather.get(0).icon.substring(0, 2) + "d")
                date[0] = dateHolder[0]
            }
        }
        return iconIdList
    }
}

class MainActivityViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
