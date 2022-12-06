package com.muhammetcakir.yourdietprogramkotlin.Views


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.muhammetcakir.yourdietprogramkotlin.Adapters.FavorilerimAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.yenile
import com.muhammetcakir.yourdietprogramkotlin.Models.YEMEK_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek


import com.muhammetcakir.yourdietprogramkotlin.Models.favorilerimList

import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityFavorilerimBinding

class FavorilerimActivity : AppCompatActivity(),YemekClickListener,yenile {
    private lateinit var binding: ActivityFavorilerimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavorilerimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        binding.favorilerimrc.apply {
            layoutManager = GridLayoutManager(applicationContext, 1)
            adapter = FavorilerimAdapter(favorilerimList, this@FavorilerimActivity,this@FavorilerimActivity)
        }

        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }



    }

    override fun onClick(yemek: Yemek) {
        val intent = Intent(applicationContext, yemekdetay::class.java)
        intent.putExtra(YEMEK_ID_EXTRA, yemek.id.toString())
        onclick()
        startActivity(intent)

    }

    override fun onclick() {
        binding.favorilerimrc.apply {
            layoutManager = GridLayoutManager(applicationContext, 1)
            adapter = FavorilerimAdapter(favorilerimList, this@FavorilerimActivity,this@FavorilerimActivity)
        }
    }
}




