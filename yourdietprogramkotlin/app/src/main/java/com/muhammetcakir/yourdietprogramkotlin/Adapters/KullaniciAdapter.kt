package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.kullanicidetay
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.databinding.KullanicilarCardBinding
import com.squareup.picasso.Picasso

private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
class KullaniciAdapter
    (private val kullanicilar: ArrayList<User>,private val onclick: kullanicidetay): RecyclerView.Adapter<KullaniciAdapter.KullaniciHolder>()

{
    class KullaniciHolder(val binding: KullanicilarCardBinding,private val onclick2: kullanicidetay):RecyclerView.ViewHolder(binding.root)
    {
        val kullanicicard:CardView=binding.kullanicicard
        val kullaniciisim: TextView = binding.kullaniciisim
        val kullaniciemail: TextView = binding.kullaniciemail
        val kullanicisifre: TextView = binding.kullanicisifre
        val kullaniciImageView: ImageView = binding.kullanicifoto

        fun bindItems(item: User) {
            kullanicisifre.setText(item.sifre)
            kullaniciemail.setText(item.email)
            kullaniciisim.setText(item.isim)
            Picasso.get().load(item.ImageUrl).into(kullaniciImageView)

            kullanicicard.setOnClickListener {
                onclick2.onclick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KullaniciHolder {
        val binding =KullanicilarCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return KullaniciAdapter.KullaniciHolder(binding,onclick)
    }

    override fun onBindViewHolder(holder: KullaniciHolder, position: Int) {
        holder.bindItems(kullanicilar[position])
    }

    override fun getItemCount(): Int {
        return  kullanicilar.size
    }

}

private fun removedatabase(index:String)
{
    db.collection("Users").document(index)
        .delete()
        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
}