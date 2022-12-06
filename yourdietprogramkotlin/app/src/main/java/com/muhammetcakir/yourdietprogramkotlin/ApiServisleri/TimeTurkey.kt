package com.muhammetcakir.yourdietprogramkotlin.ApiServisleri

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TimeTurkey {
    @SerializedName("day_of_week")
    @Expose
    var dayOfWeek:String = ""

    @SerializedName("utc_datetime")
    @Expose
    var dateTime: String? = null

}