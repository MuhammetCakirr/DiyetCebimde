package com.muhammetcakir.yourdietprogramkotlin.Views
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetcakir.yourdietprogramkotlin.MainActivity
import com.muhammetcakir.yourdietprogramkotlin.Models.Admin
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityAdminSayfaBinding
import com.muhammetcakir.yourdietprogramkotlin.timeTurkey
import com.muhammetcakir.yourdietprogramkotlin.timeTurkey2

val adminArrayList : ArrayList<Admin> = ArrayList()

class AdminSayfa : AppCompatActivity() {
    private lateinit var binding:ActivityAdminSayfaBinding
    private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminSayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.adminisim.setText(adminArrayList[0].isim)
        binding.adminemail.setText(adminArrayList[0].email)
        binding.adminsifre.setText(adminArrayList[0].sifre)
        binding.tarih.setText(timeTurkey2!!.dateTime!!.split("T")[0]+" "+timeTurkey!!.dayOfWeek!!.split("G")[0])

        binding.btngNcelle.setOnClickListener {
            adminupdate()
            Toast.makeText(getApplicationContext(),"Bilgilerin Güncellendi.Profilim Kısmından Görebilirsin.",Toast.LENGTH_LONG).show()
            onDestroy()
            startActivity(Intent(this@AdminSayfa, AdminAnaSayfaActivity::class.java))
        }
        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
        }

        binding.btnadmincikisyap.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun adminupdate() {
        val adminMap = hashMapOf<String, Any>()
        adminMap.put("ad",binding.adminemail.text.toString())
        adminMap.put("sifre",binding.adminsifre.text.toString())
        adminMap.put("adsoyad",binding.adminisim.text.toString())

        db.collection("Admin").document(adminArrayList[0].id).update(adminMap).
        addOnCompleteListener{
            if (it.isComplete && it.isSuccessful) {
                //back
                finish()
                   Toast.makeText(getApplicationContext(),"Diyetin Eklendi.Diyetlerim Kısmından Görebilirsin.",Toast.LENGTH_LONG).show();

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                .show()
        }

    }



}





