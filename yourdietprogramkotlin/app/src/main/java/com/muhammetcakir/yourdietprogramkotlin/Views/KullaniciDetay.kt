package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.MainActivity
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.Models.YEMEK_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityKullaniciDetayBinding
import com.squareup.picasso.Picasso
var db10 : FirebaseFirestore = FirebaseFirestore.getInstance()
class KullaniciDetay : AppCompatActivity() {
    private lateinit var binding:ActivityKullaniciDetayBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityKullaniciDetayBinding.inflate(layoutInflater)

        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val KullaniciFromID = intent.getStringExtra(YEMEK_ID_EXTRA)
        val user = KullaniciFromID(KullaniciFromID.toString())

        if(user != null)
        {
            Picasso.get().load(user.ImageUrl).into(binding.celalo)
            binding.profilemail.setText(user.email)
            binding.profilname.setText(user.isim)
            binding.profilsifre.setText(user.sifre)
        }
        binding.btnsil.setOnClickListener{
            removedatabase(user!!.id)
        }
        binding.btngNcelle.setOnClickListener {
            val Kullanicimap = hashMapOf<String, Any>()
            Kullanicimap.put("email",binding.profilemail.text.toString())
            Kullanicimap.put("sifre",binding.profilsifre.text.toString())
            Kullanicimap.put("isim",binding.profilname.text.toString())
            Handler().postDelayed(
                {
                    db10.collection("Users").document(user!!.id).update(Kullanicimap).
                    addOnCompleteListener{
                        if (it.isComplete && it.isSuccessful) {
                            //back
                            finish()
                            Toast.makeText(getApplicationContext(),"Kullanıcı Güncellendi.", Toast.LENGTH_LONG).show();
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                    startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
                },
                8000
            )
        }
    }
    private fun KullaniciFromID(yemekID: String): User?
    {
        for(user in KullanicilarArrayList)
        {
            if(user.id == yemekID)
                return user
        }
        return null
    }
    private fun removedatabase(index:String)
    {
        db10.collection("Users").document(index)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(getApplicationContext(),"Kullanıcı Silindi.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }
}