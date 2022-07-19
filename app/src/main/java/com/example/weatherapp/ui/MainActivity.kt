package com.example.weatherapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherapp.adapters.HourForcastRecyclerViewAdapter
import com.example.weatherapp.databinding.MainActivityBinding
import com.example.weatherapp.network.models.WeatherResponse
import com.example.weatherapp.util.Resource
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewModelFactory: MainActivityViewModelFactory

    var weatherResponse: WeatherResponse? =null

    val MY_PERMISSIONS_REQUEST_INTERNET = 0
    val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private lateinit var locationRequest:LocationRequest

    private var requestingLocationUpdates=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = MainActivityViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        permissionsCheck()

        binding.loadingProgressBar.visibility= View.VISIBLE
        binding.mainLayout.visibility=View.GONE

        val hourForcastRecyclerViewAdapter=HourForcastRecyclerViewAdapter()
        binding.hourForecastRecyclerview.setAdapter(hourForcastRecyclerViewAdapter)

        viewModel.weatherDataObserver().observe(this, object : Observer<Resource<WeatherResponse>> {
                override fun onChanged(weatherResponse: Resource<WeatherResponse>) {
                    when(weatherResponse){
                        is Resource.Success->{
                            weatherResponse.data?.let{
                                binding.loadingProgressBar.visibility= View.GONE

                                binding.mainLayout.visibility=View.VISIBLE

                                this@MainActivity.weatherResponse = weatherResponse.data

                                hourForcastRecyclerViewAdapter.setHourForcastDataList(weatherResponse.data.list)

                                binding.cityNameTextview.setText(weatherResponse.data.city.name)
                                val temperatureHolder=weatherResponse.data.list.get(0).main.temp.toInt().toString()+"°C"
                                binding.temperatureTextview.setText(temperatureHolder)
                                binding.conditionTextview.setText(weatherResponse.data.list.get(0).weather.get(0).description)

                                val datesOfForecastList=viewModel.getDatesOfForecastedWeather(weatherResponse.data.list)
                                binding.forecast1DateTextview.setText(datesOfForecastList[1])
                                binding.forecast2DateTextview.setText(datesOfForecastList[2])
                                binding.forecast3DateTextview.setText(datesOfForecastList[3])

                                val temperatureRangeList=viewModel.getMinMaxTemperaturesOfDates(weatherResponse.data.list)
                                binding.forecast1TempTextview.setText(temperatureRangeList[1])
                                binding.forecast2TempTextview.setText(temperatureRangeList[2])
                                binding.forecast3TempTextview.setText(temperatureRangeList[3])
                                //i dont pass all date in iconlist because
                                //i want the condition icon for today be for day or night
                                //and the condition icons for the next days only for day
                                setConditionIconImage(weatherResponse.data.list.get(0).weather.get(0).icon,binding.conditionImageview)
                                val conditionIconIdList=viewModel.getWeatherConditionIconIdOfDates(weatherResponse.data.list)
                                setConditionIconImage(conditionIconIdList.get(1),binding.forecast1ConditionImageview)
                                setConditionIconImage(conditionIconIdList.get(2),binding.forecast2ConditionImageview)
                                setConditionIconImage(conditionIconIdList.get(3),binding.forecast3ConditionImageview)
                            }
                        }
                        is Resource.Error->{
                            binding.loadingProgressBar.visibility= View.GONE
                            binding.mainLayout.visibility=View.VISIBLE
                            Snackbar.make(binding.mainLayout, "Sorry, something went wrong",  Snackbar.LENGTH_SHORT).show()
                        }
                        is Resource.Loading->{
                            binding.loadingProgressBar.visibility= View.VISIBLE
                            binding.mainLayout.visibility=View.GONE
                        }
                    }
                }
            })

        binding.citySearchButton.setOnClickListener{
            val cityInputText:String=binding.citySearchTextinputedittext.text.toString()
            val temp:String=cityInputText.trim()
            if(binding.citySearchTextinputedittext.text != null && temp != ""){
                viewModel.requestWeatherDataOfCity(cityInputText)
                binding.citySearchTextinputedittext.setText("")
            }else{
                Snackbar.make(binding.mainLayout, "Please type the name of a city",  Snackbar.LENGTH_SHORT).show()
            }
        }

        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(2000)

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getCurrentLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        if (requestingLocationUpdates) stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }


    fun setConditionIconImage(icon:String, image: ImageView){
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/"+icon+"@2x.png")
            .apply(RequestOptions.centerCropTransform())
            .into(image)
    }


    fun  permissionsCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), MY_PERMISSIONS_REQUEST_INTERNET)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== MY_PERMISSIONS_REQUEST_INTERNET) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                this.finish()
            }
            return
        }
        if (requestCode== MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                unableToFindLocation()
            } else{
                getCurrentLocation()
            }
            return
        }
    }

    fun unableToFindLocation(){
        viewModel.requestWeatherDataOfCity("London")
        Snackbar.make(binding.mainLayout, "Could not find your location",  Snackbar.LENGTH_SHORT).show()
    }

    fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isGPSEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener{location->
                    if (location != null) {
                        viewModel.requestWeatherDataOfLocation(location.latitude, location.longitude)
                    } else {
                        startLocationUpdates()
                    }
                }
            } else {
                turnOnGPS()
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun turnOnGPS() {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@MainActivity, 112)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 112) {
            if (resultCode == RESULT_OK) {
                turnOnGPS()
            }else{
                unableToFindLocation()
            }
        }
    }

    fun startLocationUpdates() {
        requestingLocationUpdates=true
         locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    if(location!=null) {
                        requestingLocationUpdates = false
                        stopLocationUpdates()
                        getCurrentLocation()
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}
