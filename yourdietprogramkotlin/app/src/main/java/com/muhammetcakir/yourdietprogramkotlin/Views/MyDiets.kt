package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammetcakir.yourdietprogramkotlin.Adapters.MyDietAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.diyetsil
import com.muhammetcakir.yourdietprogramkotlin.Models.Diyet
import com.muhammetcakir.yourdietprogramkotlin.currentUser
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMyDietsBinding
import com.muhammetcakir.yourdietprogramkotlin.suankikullanicilist
import com.muhammetcakir.yourdietprogramkotlin.yenikikullanicilist
import kotlinx.android.synthetic.main.activity_my_diet_add.*


val diyetlerArrayList: ArrayList<Diyet> = ArrayList()
val kisiyeaitdiyetlerArrayList: ArrayList<Diyet> = ArrayList()

class MyDiets : AppCompatActivity(), diyetsil {
    var adapter: MyDietAdapter? = null
    private lateinit var binding: ActivityMyDietsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDietsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.senindiyetlerin2.setOnClickListener {
            startActivity(Intent(this@MyDiets, MyDiets::class.java))
        }
        binding.oneridiyetlerin3.setOnClickListener {

            if (suankikullanicilist.isEmpty()) {
                Toast.makeText(
                    getApplicationContext(),
                    "Öneri diyeti almak için yeni diyet oluştur!",
                    Toast.LENGTH_LONG
                ).show();
                startActivity(Intent(this@MyDiets, NewActivity::class.java))
            } else {
                startActivity(Intent(this@MyDiets, Oneridiyetler::class.java))
            }
        }
        for (diyet in diyetlerArrayList) {
            if (suankikullanicilist.isNotEmpty()) {
                if (diyet.kimeait.toString() == suankikullanicilist[0].id) {
                    kisiyeaitdiyetlerArrayList.add(diyet)
                }
                binding.mydietrc.apply {
                    layoutManager =
                        LinearLayoutManager(this@MyDiets, LinearLayoutManager.VERTICAL, false)
                    adapter = MyDietAdapter(kisiyeaitdiyetlerArrayList, this@MyDiets)
                    binding.mydietrc.adapter = adapter
                    binding.mydietrc.layoutManager = layoutManager
                }
            } else if (yenikikullanicilist.isNotEmpty()) {
                if (diyet.kimeait.toString() == yenikikullanicilist[0].id) {
                    kisiyeaitdiyetlerArrayList.add(diyet)
                }
                binding.mydietrc.apply {
                    layoutManager =
                        LinearLayoutManager(this@MyDiets, LinearLayoutManager.VERTICAL, false)
                    adapter = MyDietAdapter(kisiyeaitdiyetlerArrayList, this@MyDiets)
                    binding.mydietrc.adapter = adapter
                    binding.mydietrc.layoutManager = layoutManager
                }
            }

        }
        if (kisiyeaitdiyetlerArrayList.isEmpty()) {
            binding.diyetkisiyeozel.visibility = View.VISIBLE
        }
        binding.diyetkisiyeozel.setOnClickListener {
            startActivity(Intent(this, mydiyetEkle::class.java))
        }
    }

    override fun onclick() {
        onDestroy()
        startActivity(Intent(this, MyDiets::class.java))
    }
}