package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.Adapters.CardAdapter
import com.muhammetcakir.yourdietprogramkotlin.Adapters.KategoriCardAdapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.KategoriClickListener
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekGuncelleClickListener
import com.muhammetcakir.yourdietprogramkotlin.Models.*
import com.muhammetcakir.yourdietprogramkotlin.currentUser
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityYemeklerBinding
import kotlin.collections.ArrayList


var kategoriid:String="0"
val kategoriArrayList : ArrayList<YemekKategori> = ArrayList()
val hangiyemek : ArrayList<Yemek> = ArrayList()
val arananyemekler : ArrayList<Yemek> = ArrayList()
class yemekler : AppCompatActivity(), YemekClickListener,KategoriClickListener,YemekGuncelleClickListener {
    private  var auth : FirebaseAuth=FirebaseAuth.getInstance()
    private  var db : FirebaseFirestore=FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityYemeklerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYemeklerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        arananyemekler.addAll(yemekList)
        val mainActivity = this
        binding.rcyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 1)
            adapter = CardAdapter(yemekList, this@yemekler,this@yemekler)
        }

        binding.homebutton.setOnClickListener {
            if (currentUser!!.email.toString()=="mami@gmail.com")
            {
                startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
            }
            else
            {
                startActivity(Intent(this, NewActivity::class.java))
            }
        }
        binding.kategorirc.apply {
            layoutManager = LinearLayoutManager(this@yemekler,LinearLayoutManager.HORIZONTAL,false)
            println("KATEGORİLER BOYUT"+ kategoriArrayList.size)
            adapter = KategoriCardAdapter(kategoriArrayList,this@yemekler)
            binding.kategorirc.adapter = adapter
            binding.kategorirc.layoutManager=layoutManager
        }

        binding.search.clearFocus()
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arananyemekler.clear()
                for(yemek in yemekList)
                {
                    if (newText != null) {
                        if(yemek.yemekadi.toLowerCase().contains(newText.toLowerCase()))
                        {
                            arananyemekler.add(yemek)
                        }

                    }
                    else{
                        binding.rcyclerView.apply {
                            layoutManager= GridLayoutManager(context,1)
                            adapter=CardAdapter(yemekList ,this@yemekler,this@yemekler)
                        }
                    }

                }
                binding.rcyclerView.apply {
                    layoutManager= GridLayoutManager(context,1)
                    adapter=CardAdapter(arananyemekler ,this@yemekler,this@yemekler)
                }
                return true
                }

           })

    }

    override fun onClick(yemek: Yemek) {
        val intent = Intent(applicationContext, yemekdetay::class.java)
        intent.putExtra(YEMEK_ID_EXTRA, yemek.id.toString())
        startActivity(intent)
    }

    override fun onClick(yemekKategori: YemekKategori) {

            hangiyemek.clear()
            for(yemek in yemekList)
            {
                print(yemek.id)
                print(yemek.yemekadi)
                print(yemek.kategorisi)
                if (yemek.kategorisi== yemekKategori.id)
                {
                     hangiyemek.add(yemek)
                }
            }

            kategoriid ="0"
            binding.rcyclerView.apply {
                layoutManager = GridLayoutManager(applicationContext, 1)
                adapter = CardAdapter(hangiyemek, this@yemekler,this@yemekler)
            }
        }

    override fun onClick2(yemek: Yemek) {
        val intent = Intent(applicationContext, YemekGuncelle::class.java)
        intent.putExtra(YEMEK_ID_EXTRA, yemek.id.toString())
        startActivity(intent)
    }


}










