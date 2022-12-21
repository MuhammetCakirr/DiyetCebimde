package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.*
import com.muhammetcakir.yourdietprogramkotlin.Models.DiyetBelirleme
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityDietAddBinding

import java.util.*


var cinsiyet: Boolean? = null
var spor: Boolean? = null
var hastalik: Boolean? = null
val kullaniciid = UUID.randomUUID()
var boy: Double? = null
var kilo: Int? = null
private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

class DietAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDietAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.backbutton.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }
        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }
        binding.olustur.setOnClickListener {

            if (binding.checkBoxerkek.isChecked()) {
                cinsiyet = true
            } else {
                cinsiyet = false
            }

            if (binding.checkBox2.isActivated == true || binding.checkBox1.isActivated == true || binding.checkBox3.isActivated == true || binding.checkBox4.isActivated == true) {
                spor = true
            } else {
                spor = false
            }
            if (binding.checkBoxseker.isActivated == true &&
                binding.checkBoxTansiyon.isActivated == true &&
                binding.checkBoxdiyabet.isActivated == true &&
                binding.checkBoxcolyak.isActivated == true &&
                binding.checkBoxKanser.isActivated == true &&
                binding.checkBoxOsteoartrit.isActivated == true
            ) {
                hastalik = true
            } else {
                hastalik = false
            }
            kilo = Integer.parseInt(binding.editTextNumberkilo.getText().toString().trim())
            boy = (binding.editTextNumberboy.getText().toString().trim()).toDouble()
            diyetbelirleme()
            if(suankikullanicilist.isNotEmpty())
            {
                suankikullanicilist[0].kilo= kilo.toString()
                suankikullanicilist[0].boy= boy.toString()
                val adminMap = hashMapOf<String, Any>()
                adminMap.put("kilo", kilo.toString())
                adminMap.put("boy", boy.toString())

                db.collection("Users").document(suankikullanicilist[0].id).update(adminMap)
                    .addOnCompleteListener {
                        if (it.isComplete && it.isSuccessful) {
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }


            }else{
                yenikikullanicilist[0].kilo= kilo.toString()
                yenikikullanicilist[0].boy= boy.toString()
                val adminMap = hashMapOf<String, Any>()
                adminMap.put("kilo", yenikikullanicilist[0].kilo.toString())
                adminMap.put("boy", yenikikullanicilist[0].boy.toString())
                println("Kullanıcı İd"+currentUser!!.uid)
                db.collection("Users").document(currentUser!!.uid).update(adminMap)
                    .addOnCompleteListener {
                        if (it.isComplete && it.isSuccessful) {
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
            }
            if (diyetbelirlemeList.isNotEmpty()) {
                kisibki= Math.floor(
                    diyetbelirlemeList[0].Kilo / (Math.pow(
                        diyetbelirlemeList[0].Boy,
                        diyetbelirlemeList[0].Boy
                    ))
                )
            }
            var intent = Intent(this@DietAddActivity, Oneridiyetler::class.java)
            intent.putExtra("bki", kisibki)
            startActivity(intent)

        }
    }

    fun diyetbelirleme() {
        val diyetBelirlemee = DiyetBelirleme(
            kullaniciid.toString(),
            boy!!,
            kilo!!,
            cinsiyet!!,
            hastalik!!,
            spor!!,
        )
        diyetbelirlemeList.add(diyetBelirlemee)

    }

}

