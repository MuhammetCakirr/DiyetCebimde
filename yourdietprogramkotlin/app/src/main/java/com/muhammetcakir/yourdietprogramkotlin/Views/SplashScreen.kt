package com.muhammetcakir.yourdietprogramkotlin.Views
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetcakir.yourdietprogramkotlin.Models.bugununyemekleri
import com.muhammetcakir.yourdietprogramkotlin.Models.yemekList
import com.muhammetcakir.yourdietprogramkotlin.R
import com.muhammetcakir.yourdietprogramkotlin.suankikullanicilist
import kotlin.random.Random
class SplashScreen : AppCompatActivity() {
    private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        val rastgeleList = List(17) { Random.nextInt(1, 20) }
        auth = Firebase.auth
        Handler().postDelayed(
            {
                bugununyemekleri.clear()
                bugununyemekleri.add(yemekList[rastgeleList[4]])
                bugununyemekleri.add(yemekList[rastgeleList[8]])
                bugununyemekleri.add(yemekList[rastgeleList[3]])
                if (auth.currentUser != null)
                {
                    for (user in KullanicilarArrayList)
                    {
                        if (user.email==auth.currentUser!!.email.toString())
                        {
                            suankikullanicilist.add(user)
                        }
                    }
                    if (auth.currentUser!!.email.toString()=="mami1@gmail.com")
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
            5000
        )
    }
}
