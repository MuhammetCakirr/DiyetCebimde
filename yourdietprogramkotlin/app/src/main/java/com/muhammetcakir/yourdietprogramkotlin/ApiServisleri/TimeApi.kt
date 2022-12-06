package com.muhammetcakir.yourdietprogramkotlin.ApiServisleri

import retrofit2.Call
import retrofit2.http.GET

interface TimeApi
{
    @GET("Europe/Istanbul")
    fun getTime():Call<TimeTurkey>

}