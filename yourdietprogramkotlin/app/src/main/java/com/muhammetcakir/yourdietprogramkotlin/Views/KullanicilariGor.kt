package com.muhammetcakir.yourdietprogramkotlin.Views


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.Adapters.KullaniciAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.Kullanicisil
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.kullanicidetay
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.Models.YEMEK_ID_EXTRA
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityKullanicilariGorBinding

val KullanicilarArrayList : ArrayList<User> = ArrayList()
class KullanicilariGor : AppCompatActivity(),Kullanicisil,kullanicidetay {
    var adapter : KullaniciAdapter? = null
    private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding:ActivityKullanicilariGorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityKullanicilariGorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.kullanicirc.apply {
            layoutManager = LinearLayoutManager(this@KullanicilariGor, LinearLayoutManager.VERTICAL,false)
            adapter = KullaniciAdapter(KullanicilarArrayList,this@KullanicilariGor)
            binding.kullanicirc.adapter = adapter
            binding.kullanicirc.layoutManager=layoutManager
        }

        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
        }
    }

    override fun onclick() {
        onDestroy()
        Toast.makeText(getApplicationContext(),"Kullanıcı Silindi", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
    }

    override fun onclick(user: User) {
        val intent = Intent(applicationContext, KullaniciDetay::class.java)
        intent.putExtra(YEMEK_ID_EXTRA, user.id.toString())
        startActivity(intent)
    }


}