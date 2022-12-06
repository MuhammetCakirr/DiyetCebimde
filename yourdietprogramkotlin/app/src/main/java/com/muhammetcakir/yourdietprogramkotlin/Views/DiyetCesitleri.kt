package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.muhammetcakir.yourdietprogramkotlin.Adapters.DietCardAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.DietClickListener
import com.muhammetcakir.yourdietprogramkotlin.Models.DietCesit
import com.muhammetcakir.yourdietprogramkotlin.Models.DietCesitList
import com.muhammetcakir.yourdietprogramkotlin.Models.Diet_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.R
import kotlinx.android.synthetic.main.activity_diyet_cesitleri.*

class DiyetCesitleri : AppCompatActivity(), DietClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diyet_cesitleri)
        supportActionBar?.hide()


        rcyclerViewDietCesitleri.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = DietCardAdapter(DietCesitList, this@DiyetCesitleri)
        }
    }


    override fun onClick(diet: DietCesit) {
        val intent = Intent(applicationContext, DiyetCesitleriDetay::class.java)
        intent.putExtra(Diet_ID_EXTRA, diet.id)
        startActivity(intent)
    }
}
