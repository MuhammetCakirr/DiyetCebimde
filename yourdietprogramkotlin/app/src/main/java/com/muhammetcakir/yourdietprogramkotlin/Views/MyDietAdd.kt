package com.muhammetcakir.yourdietprogramkotlin.Views


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.currentUser

import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMyDietAddBinding


class MyDietAdd : AppCompatActivity() {
     var db : FirebaseFirestore=FirebaseFirestore.getInstance()
 private lateinit var binding:ActivityMyDietAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyDietAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.mydiyetEkle.setOnClickListener()
        {
            verileriyukle()
           Toast.makeText(getApplicationContext(),"Diyetin Eklendi.Diyetlerim Kısmından Görebilirsin.",Toast.LENGTH_LONG).show();
            startActivity(Intent(this@MyDietAdd, NewActivity::class.java))
        }

    }
    fun verileriyukle() {

        if (binding.sabahogun1.text != null || binding.sabahogun2.text != null || binding.sabahogun3.text != null ||
            binding.ogleogun1.text != null || binding.ogleogun2.text != null || binding.ogleogun3.text != null
        )
        {
            val diyetMap = hashMapOf<String, String>()
            diyetMap.put("sabahogun1", binding.sabahogun1.text.toString())
            diyetMap.put("sabahogun2", binding.sabahogun2.text.toString())
            diyetMap.put("sabahogun3", binding.sabahogun3.text.toString())
            diyetMap.put("ogleogun1", binding.ogleogun1.text.toString())
            diyetMap.put("ogleogun2", binding.ogleogun2.text.toString())
            diyetMap.put("ogleogun3", binding.ogleogun3.text.toString())
            diyetMap.put("aksamogun1", binding.aksamogun1.text.toString())
            diyetMap.put("aksamogun2", binding.aksamogun2.text.toString())
            diyetMap.put("aksamogun3", binding.aksamogun3.text.toString())
            diyetMap.put("araogun1", binding.araogun1.text.toString())
            diyetMap.put("araogun2", binding.araogun2.text.toString())
            diyetMap.put("not", binding.not.text.toString())
            diyetMap.put("hangigun",binding.hangigun1.text.toString())
            diyetMap.put("kimeait", currentUser?.uid.toString())
            db.collection("Diyetler").add(diyetMap).addOnCompleteListener { task ->

                if (task.isComplete && task.isSuccessful) {
                    //back
                    finish()
                    //    Toast.makeText(getApplicationContext(),"Diyetin Eklendi.Diyetlerim Kısmından Görebilirsin.",Toast.LENGTH_LONG).show();
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }

        }
    }


}

