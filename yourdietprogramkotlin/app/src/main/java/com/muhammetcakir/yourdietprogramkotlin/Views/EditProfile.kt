package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetcakir.yourdietprogramkotlin.MainActivity
import com.muhammetcakir.yourdietprogramkotlin.Models.bugununyemekleri
import com.muhammetcakir.yourdietprogramkotlin.Models.yemekList
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityEditProfileBinding

import com.muhammetcakir.yourdietprogramkotlin.suankikullanicilist
import com.muhammetcakir.yourdietprogramkotlin.yenikikullanicilist

import com.squareup.picasso.Picasso
private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
class EditProfile : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        val currentUser = auth.currentUser

            if (suankikullanicilist.isEmpty())
            {
                Picasso.get().load(yenikikullanicilist[0].ImageUrl).into(binding.celalo)
                binding.profilemail.setText(currentUser!!.email.toString())
                binding.profilsifre.setText(yenikikullanicilist[0].sifre)
                binding.profilname.setText(yenikikullanicilist[0].isim)
            }
            else
            {
                Picasso.get().load(suankikullanicilist[0].ImageUrl).into(binding.celalo)
                binding.profilemail.setText(currentUser!!.email.toString())
                binding.profilsifre.setText(suankikullanicilist[0]!!.sifre)
                binding.profilname.setText(suankikullanicilist[0].isim)
            }

        binding.btngNcelle.setOnClickListener {
            kullaniciupdate()
            Toast.makeText(applicationContext,"Bilgileriniz Güncellendi",Toast.LENGTH_SHORT).show()

           // startActivity(Intent(this,NewActivity::class.java))

        }
        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }
        binding.btnsil.setOnClickListener {

            removedatabase(suankikullanicilist[0].id)
        }
        binding.btncikisyap.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun kullaniciupdate() {
        val adminMap = hashMapOf<String, Any>()
        adminMap.put("email",binding.profilemail.text.toString())
        adminMap.put("sifre",binding.profilsifre.text.toString())
        adminMap.put("isim",binding.profilname.text.toString())
        Handler().postDelayed(
            {

                db.collection("Users").document(suankikullanicilist[0].id).update(adminMap).
                addOnCompleteListener{
                    if (it.isComplete && it.isSuccessful) {
                        //back

                        finish()
                        Toast.makeText(getApplicationContext(),"Bilgilerin Güncellendi.",Toast.LENGTH_LONG).show();

                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
                startActivity(Intent(this, NewActivity::class.java))
            },


            8000


        )



    }

    private fun removedatabase(index:String)
    {
         db.collection("Users").document(index)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
                Toast.makeText(applicationContext,"Hesabınız silindi",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }


}

