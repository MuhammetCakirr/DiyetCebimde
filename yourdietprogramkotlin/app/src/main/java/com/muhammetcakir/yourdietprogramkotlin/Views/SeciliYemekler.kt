package com.muhammetcakir.yourdietprogramkotlin.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muhammetcakir.yourdietprogramkotlin.R
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityNewBinding
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivitySeciliYemeklerBinding

class SeciliYemekler : AppCompatActivity() {
    private lateinit var abinding: ActivitySeciliYemeklerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secili_yemekler)
    }
}