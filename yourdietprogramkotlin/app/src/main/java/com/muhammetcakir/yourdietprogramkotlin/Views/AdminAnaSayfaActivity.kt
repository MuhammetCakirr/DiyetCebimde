package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.Models.Admin
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.R
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityAdminAnaSayfaBinding
import kotlinx.android.synthetic.main.activity_new.*

class AdminAnaSayfaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminAnaSayfaBinding
    private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminAnaSayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        getAdminFromFirestore()
        binding.AdminSayfasi.setOnClickListener {

            val intent = Intent(applicationContext, AdminSayfa::class.java)
            startActivity(intent)
        }
        binding.UrunEkle.setOnClickListener {
            val intent = Intent(applicationContext, YemekEkle::class.java)
            startActivity(intent)
        }
        binding.Urunlerigor.setOnClickListener {
            startActivity(Intent(this@AdminAnaSayfaActivity, yemekler::class.java))
        }

        binding.kullanicilarigor.setOnClickListener {
            val intent = Intent(applicationContext, KullanicilariGor::class.java)
            startActivity(intent)
        }
    }

    fun getAdminFromFirestore() {
        db.collection("Admin")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    adminArrayList.add( Admin(document.getId().toString(),document.getString("ad").toString(),document.getString("sifre").toString(),document.getString("adsoyad").toString()))

                        print(adminArrayList[0].id)
                        print(adminArrayList[0].email)
                         print(adminArrayList[0].sifre)
                    /* kategoriArrayList.add(
                         YemekKategori(document.getId(),
                         document.getString("Kategoriisim").toString(),document.getString("kategoriresim").toString())
                     )*/


                }

            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }



    }

}