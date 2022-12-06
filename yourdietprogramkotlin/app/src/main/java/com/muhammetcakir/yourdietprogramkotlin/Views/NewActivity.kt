package com.muhammetcakir.yourdietprogramkotlin.Views

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.*
import com.muhammetcakir.yourdietprogramkotlin.Adapters.bugununyemekleriadapter
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.Models.*
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityNewBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_new.*


class NewActivity : AppCompatActivity(), YemekClickListener {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var abinding: ActivityNewBinding
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        abinding = ActivityNewBinding.inflate(layoutInflater)
        val view = abinding.root
        setContentView(view)
        getKategoriFromFirestore()
        diyetgetir()
        supportActionBar?.hide()

        abinding.bugununmenusurc.apply {
            layoutManager =
                LinearLayoutManager(this@NewActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = bugununyemekleriadapter(bugununyemekleri, this@NewActivity)
        }

        abinding.dietcesitleri.setOnClickListener {
            startActivity(Intent(this@NewActivity, DiyetCesitleri::class.java))
        }
        abinding.Yemekler.setOnClickListener {
            startActivity(Intent(this@NewActivity, yemekler::class.java))
        }
        abinding.Diyetekle.setOnClickListener {
            startActivity(Intent(this@NewActivity, MyDietAdd::class.java))
        }

        abinding.editprofile.setOnClickListener {
            startActivity(Intent(this@NewActivity, EditProfile::class.java))
        }
        abinding.diyetlerim.setOnClickListener {
            startActivity(Intent(this@NewActivity, MyDiets::class.java))

        }
        abinding.favagit.setOnClickListener {
            startActivity(Intent(this@NewActivity, FavorilerimActivity::class.java))
        }
        abinding.dietadd.setOnClickListener {
            startActivity(Intent(this@NewActivity, DietAddActivity
            ::class.java))
        }

        if (suankikullanicilist.isNotEmpty()) {
            Picasso.get().load(suankikullanicilist[0].ImageUrl).into(abinding.kullanicifoto)
            abinding.gNaydNmesaj.setText("Günaydın, " + suankikullanicilist[0].isim)
        } else {
            abinding.gNaydNmesaj.setText("Günaydın, " + yenikikullanicilist[0].isim)
            Picasso.get().load(yenikikullanicilist[0].ImageUrl).into(abinding.kullanicifoto)
        }

        if (suankikullanicilist.isNotEmpty()) {
            kisibki = Math.floor(
                Integer.parseInt(suankikullanicilist[0].kilo) / (Math.pow(
                    suankikullanicilist[0].boy.toDouble(),
                    suankikullanicilist[0].boy.toDouble()
                ))
            )
            println(kisibki)
            abinding.kackarb.visibility = View.VISIBLE
            abinding.kacprotein.visibility = View.VISIBLE
            abinding.kacyag.visibility = View.VISIBLE
            abinding.gNlKalmangereken.text = "Günlük Alman Gereken Değerler!"

            abinding.yagdegeri.setText((Math.floor(kisibki!! + 78).toString() + " g"))
            abinding.proteindegeri.setText((Math.floor(kisibki!! * 10 + 121).toString() + " g"))
            abinding.Karbdegeri.setText(Math.floor(kisibki!! * 7 + 48).toString() + " g")
            abinding.Bugunalmangerekenkalori.setText(
                "Bugün Alman Gerken Kalori: " + (Math.floor(
                    kisibki!!
                ) + 2094).toString() + " kcal"
            )
        } else if (yenikikullanicilist[0].kilo == "0" || yenikikullanicilist[0].boy == "0") {
            abinding.Bugunalmangerekenkalori.visibility = View.GONE
            abinding.Karbdegeri.visibility = View.GONE
            abinding.proteindegeri.visibility = View.GONE
            abinding.yagdegeri.visibility = View.GONE
            abinding.kackarb.visibility = View.GONE
            abinding.kacprotein.visibility = View.GONE
            abinding.kacyag.visibility = View.GONE
            abinding.gNlKalmangereken.text =
                "Günlük alman gereken kalori miktarını ve değerlerini görmek için Diyet Oluştur!"

        } else if (yenikikullanicilist.isNotEmpty()) {
            abinding.kackarb.visibility = View.VISIBLE
            abinding.kacprotein.visibility = View.VISIBLE
            abinding.kacyag.visibility = View.VISIBLE
            abinding.gNlKalmangereken.text = "Günlük Alman Gereken Değerler!"
            abinding.yagdegeri.setText((Math.floor(kisibki!! + 78).toString() + " g"))
            abinding.proteindegeri.setText((Math.floor(kisibki!! * 10 + 121).toString() + " g"))
            abinding.Karbdegeri.setText(Math.floor(kisibki!! * 7 + 48).toString() + " g")
            abinding.Bugunalmangerekenkalori.setText(
                "Bugün Alman Gerken Kalori: " + (Math.floor(
                    kisibki!!
                ) + 2094).toString() + " kcal"
            )
        }
        if (timeTurkey != null) {
            abinding.buguneozeloneriler.setText(timeTurkey!!.dayOfWeek)
        } else {
            abinding.buguneozeloneriler.setText("Bugün'e Özel Öneriler")

        }
    }

    fun getKategoriFromFirestore() {
        db.collection("Kategoriler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    kategoriArrayList.add(
                        YemekKategori(
                            document.getId(),
                            document.getString("Kategoriisim").toString(),
                            document.getString("kategoriresim").toString()
                        )
                    )
                    println(kategoriArrayList.size)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }


    }

    fun diyetgetir() {
        db.collection("Diyetler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")


                    diyetlerArrayList.add(
                        Diyet(
                            document.getId().toString(),
                            document.getString("aksamogun1").toString(),
                            document.getString("aksamogun2").toString(),
                            document.getString("aksamogun3").toString(),
                            document.getString("araogun1").toString(),
                            document.getString("araogun2").toString(),
                            document.getString("not").toString(),
                            document.getString("ogleogun1").toString(),
                            document.getString("ogleogun2").toString(),
                            document.getString("ogleogun3").toString(),
                            document.getString("sabahogun1").toString(),
                            document.getString("sabahogun2").toString(),
                            document.getString("sabahogun3").toString(),
                            document.getString("hangigun").toString()
                        )
                    )


                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }
    }

    override fun onClick(yemek: Yemek) {
        val intent = Intent(applicationContext, yemekdetay::class.java)
        intent.putExtra(YEMEK_ID_EXTRA, yemek.id.toString())
        startActivity(intent)
    }


}

