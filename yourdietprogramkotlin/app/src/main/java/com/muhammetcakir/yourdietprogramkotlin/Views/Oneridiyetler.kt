package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammetcakir.yourdietprogramkotlin.*
import com.muhammetcakir.yourdietprogramkotlin.Adapters.MyDietAdapter
import com.muhammetcakir.yourdietprogramkotlin.Adapters.OneriDiyetlerAdapter
import com.muhammetcakir.yourdietprogramkotlin.Models.Diyet
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMainBinding
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityOneridiyetlerBinding
import kotlinx.android.synthetic.main.activity_oneridiyetler.*
import java.util.ArrayList

val oneridiyetlerlist : ArrayList<Diyet> = ArrayList()
class Oneridiyetler : AppCompatActivity() {
    private lateinit var binding: ActivityOneridiyetlerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOneridiyetlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.backbutton.setOnClickListener{
            startActivity(Intent(this, NewActivity::class.java))
        }

        binding.senindiyetlerin1.setOnClickListener {
            startActivity(Intent(this@Oneridiyetler, MyDiets::class.java))
        }
        binding.oneridiyetlerin1.setOnClickListener {
            startActivity(Intent(this@Oneridiyetler, Oneridiyetler::class.java))
        }
        println(diyetbelirlemeList.size)

        if (kisibki!!>30)
        {
            oneridiyetlerlist.add(diyetArrayList[0])
            oneridiyetlerlist.add(diyetArrayList[1])
        }
        else{
            if (kisibki!!<20)
            {
                oneridiyetlerlist.add(diyetArrayList[2])
                oneridiyetlerlist.add(diyetArrayList[3])
            }

            if (kisibki!!<30 || kisibki!!>20)
            {
                oneridiyetlerlist.add(diyetArrayList[4])
                oneridiyetlerlist.add(diyetArrayList[5])
                oneridiyetlerlist.add(diyetArrayList[6])
            }

        }


        val vucutkitleendeksi:Double=(intent.getDoubleExtra("bki",25.0)!!)
        binding.bki.setText(kisibki.toString())

        binding.homebutton.setOnClickListener {
            val intent=  Intent(this, NewActivity::class.java)
            intent.putExtra("bki",vucutkitleendeksi)
            startActivity(intent)
        }
        binding.oneridiyetrc.apply {
            layoutManager = LinearLayoutManager(this@Oneridiyetler, LinearLayoutManager.VERTICAL,false)
            adapter = OneriDiyetlerAdapter(oneridiyetlerlist)
            binding.oneridiyetrc.adapter = adapter
            binding.oneridiyetrc.layoutManager=layoutManager
        }
    }

}