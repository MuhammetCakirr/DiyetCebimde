package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetcakir.yourdietprogramkotlin.MainActivity
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.Models.bugununyemekleri
import com.muhammetcakir.yourdietprogramkotlin.Models.yemekList
import com.muhammetcakir.yourdietprogramkotlin.R
import com.muhammetcakir.yourdietprogramkotlin.suankikullanicilist
import com.muhammetcakir.yourdietprogramkotlin.yenikikullanicilist
import kotlin.random.Random
val rastgeleList = List(17) { Random.nextInt(1, 20) }
class SplashScreen : AppCompatActivity() {
    private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        Handler().postDelayed(
            {
                bugununyemekleri.add(yemekList[rastgeleList[7]])
                bugununyemekleri.add(yemekList[rastgeleList[3]])
                bugununyemekleri.add(yemekList[rastgeleList[8]])
                if (currentUser != null)
                {
                    for (user in KullanicilarArrayList)
                    {
                        if (user.email==currentUser.email.toString())
                        {
                            suankikullanicilist.add(user)
                        }
                        else
                        {
                            val intent = Intent(applicationContext, NewActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    if (currentUser.email.toString()=="mami1@gmail.com")
                    {
                        val intent = Intent(applicationContext, AdminAnaSayfaActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        startActivity(Intent(this, NewActivity()::class.java))
                    }
                }

            },
            6000
        )
    }
}
