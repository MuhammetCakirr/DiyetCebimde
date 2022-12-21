package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.Models.YEMEK_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek
import com.muhammetcakir.yourdietprogramkotlin.Models.yemekList
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityYemekGuncelleBinding
private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
class YemekGuncelle : AppCompatActivity() {
    private lateinit var binding:ActivityYemekGuncelleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityYemekGuncelleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val yemekID = intent.getStringExtra(YEMEK_ID_EXTRA)
        val yemek = yemekFromID(yemekID.toString())
        if(yemek != null)
        {
            binding.adminyemekadi.setText(yemek.yemekadi)
            binding.adminyemektoplamkalori.setText(yemek.toplamkalori)
            binding.adminyemekkarbonhidrat.setText( yemek.karbmiktari)
            binding.adminyemekprotein.setText(yemek.proteinmiktari)
            binding.adminyemekyag.setText(yemek.yagmiktari)
            binding.adminyemekhazirlamasuresi.setText(yemek.yapımsuresi)
            binding.adminyemekicindekiler.setText(yemek.icindekiler)
            binding.adminaciklamasi.setText(yemek.acıklamasi)
            binding.adminyemekkategoriid.setText(yemek.kategorisi)

        }


        binding.btnyemekguncelle.setOnClickListener {
            val YemekMap = hashMapOf<String, Any>()
            YemekMap.put("aciklamasi",binding.adminaciklamasi.text.toString())
            YemekMap.put("icindekiler",binding.adminyemekicindekiler.text.toString())
            YemekMap.put("karbmiktari",binding.adminyemekkarbonhidrat.text.toString())
            YemekMap.put("kategoriid",binding.adminyemekkategoriid.text.toString())
            YemekMap.put("proteinmiktari",binding.adminyemekprotein.text.toString())
            YemekMap.put("toplamkalori",binding.adminyemektoplamkalori.text.toString())
            YemekMap.put("yagmiktari",binding.adminyemekyag.text.toString())
            YemekMap.put("yapimsuresi",binding.adminyemekhazirlamasuresi.text.toString())
            YemekMap.put("yemekismi",binding.adminyemekadi.text.toString())
            Handler().postDelayed(
                {

                    db.collection("Yemekler").document(yemek!!.id).update(YemekMap).
                    addOnCompleteListener{
                        if (it.isComplete && it.isSuccessful) {
                            //back
                            Toast.makeText(getApplicationContext(),"Yemek Güncellendi.", Toast.LENGTH_LONG).show();

                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                    startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
                },

                4000


            )
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