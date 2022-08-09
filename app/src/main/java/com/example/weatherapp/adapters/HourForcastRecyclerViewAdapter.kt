package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherapp.R
import com.example.weatherapp.retrofit.models.List

class HourForcastRecyclerViewAdapter : RecyclerView.Adapter<HourForcastRecyclerViewAdapter.MyViewHolder>() {

    var hourForcastDataList: ArrayList<List?>? = ArrayList()
    lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.weather_hour_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.time.setText(getTimeTextInFormat(this.hourForcastDataList?.get(position)?.dt_txt))
        val temperatureHolder=this.hourForcastDataList?.get(position)?.main?.temp?.toInt().toString() + "°C"
        holder.temerature.setText(temperatureHolder)

        Glide.with(context)
            .load(
                "https://openweathermap.org/img/wn/" + this.hourForcastDataList?.get(position)?.weather?.get(0)?.icon + "@2x.png")
            .apply(RequestOptions.centerCropTransform())
            .into(holder.weatherConditionImage)
    }

    override fun getItemCount(): Int {
        if (hourForcastDataList != null) {
            return hourForcastDataList!!.size
        }
        return 0
    }

    fun setHourForcastDataList(data: kotlin.collections.List<List>) {
        hourForcastDataList?.clear()
        var j = 0
        for (i: List in data) {
            //the data is given for every three hours
            //i only want to show predictions for the next 24 hours
            //3*8=24
            if (j < 8) {
                hourForcastDataList!!.add(i)
                j++
            } else {
                break
            }
        }
        notifyDataSetChanged()
    }

    fun getTimeTextInFormat(rawDateAndTimeText: String?): String {
        val dateAndTimeArray = rawDateAndTimeText?.split(" ")
        val timeArray = dateAndTimeArray!![1].split(":")
        val time = timeArray[0] + ":" + timeArray[1]
        return time
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView
        var temerature: TextView
        var weatherConditionImage: ImageView

        init {
            time = itemView.findViewById(R.id.time_textView)
            temerature = itemView.findViewById(R.id.temperature_textView)
            weatherConditionImage = itemView.findViewById(R.id.condition_icon_imageView)
        }
    }
}