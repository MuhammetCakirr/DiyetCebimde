package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.Adapters.MyDietAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.diyetsil
import com.muhammetcakir.yourdietprogramkotlin.Models.Diyet
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMyDietsBinding
import com.muhammetcakir.yourdietprogramkotlin.diyetbelirlemeList
import com.muhammetcakir.yourdietprogramkotlin.suankikullanicilist
import kotlinx.android.synthetic.main.activity_my_diets.*
import kotlinx.android.synthetic.main.mydiet.*


val diyetlerArrayList : ArrayList<Diyet> = ArrayList()

class MyDiets : AppCompatActivity(),diyetsil {
    var adapter : MyDietAdapter? = null



    private lateinit var binding: ActivityMyDietsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyDietsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.senindiyetlerin2.setOnClickListener {
            startActivity(Intent(this@MyDiets, MyDiets::class.java))
        }

        binding.oneridiyetlerin3.setOnClickListener {

            if (suankikullanicilist.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Öneri diyeti almak için yeni diyet oluştur!", Toast.LENGTH_LONG).show();
                startActivity(Intent(this@MyDiets, NewActivity::class.java))
            }
            else
            {
                startActivity(Intent(this@MyDiets, Oneridiyetler::class.java))
            }

        }

        binding.mydietrc.apply {
            layoutManager = LinearLayoutManager(this@MyDiets, LinearLayoutManager.VERTICAL,false)
            adapter = MyDietAdapter(diyetlerArrayList,this@MyDiets)
           binding.mydietrc.adapter = adapter
            binding.mydietrc.layoutManager=layoutManager
        }


    }

    override fun onclick() {
        onDestroy()
        startActivity(Intent(this, MyDiets::class.java))
    }


}