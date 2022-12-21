package com.muhammetcakir.yourdietprogramkotlin.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muhammetcakir.yourdietprogramkotlin.Models.*
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityYemekdetayBinding
import com.squareup.picasso.Picasso


class yemekdetay : AppCompatActivity() {

    private lateinit var binding: ActivityYemekdetayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityYemekdetayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val yemekID = intent.getStringExtra(YEMEK_ID_EXTRA)
        val yemek = yemekFromID(yemekID.toString())
        if(yemek != null)
        {
            Picasso.get().load(yemek.ImageUrl).into(binding.cover)
            binding.title.text = yemek.yemekadi
            binding.toplamkalori.text=yemek.toplamkalori
            binding.karbmiktari.text=yemek.karbmiktari
            binding.proteinmiktari.text=yemek.proteinmiktari
            binding.yagmiktari.text=yemek.yagmiktari
            binding.yapimsuresi.text=yemek.yapımsuresi
            binding.icindekiler.text=yemek.icindekiler
            binding.hazirlanisi.text=yemek.acıklamasi

        }
    }

    private fun yemekFromID(yemekID: String): Yemek?
    {
        for(yemek in yemekList)
        {
            if(yemek.id == yemekID)
                return yemek
        }
        return null
    }

}
