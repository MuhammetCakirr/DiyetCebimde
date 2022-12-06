package com.muhammetcakir.yourdietprogramkotlin.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muhammetcakir.yourdietprogramkotlin.Models.DietCesit
import com.muhammetcakir.yourdietprogramkotlin.Models.DietCesitList
import com.muhammetcakir.yourdietprogramkotlin.Models.Diet_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.R
import kotlinx.android.synthetic.main.activity_diyet_cesitleri_detay.*

class DiyetCesitleriDetay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diyet_cesitleri_detay)
        supportActionBar?.hide()
        val DietID = intent.getIntExtra(Diet_ID_EXTRA, -1)
        val diet = DietCesitFromID(DietID)
        if(diet != null)
        {
            Foto.setImageResource(diet.cover)
            isim.text = diet.Adı
            acıklama.text = diet.Acıklaması
            kısaacıklama.text = diet.KısaAcıklama
        }
    }

    private fun DietCesitFromID(DietCesitId: Int): DietCesit?
    {
        for(dc in DietCesitList)
        {
            if(dc.id == DietCesitId)
                return dc
        }
        return null
    }
}