package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.Kullanicisil
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.diyetsil
import com.muhammetcakir.yourdietprogramkotlin.Models.User
import com.muhammetcakir.yourdietprogramkotlin.Models.YemekKategori
import com.muhammetcakir.yourdietprogramkotlin.databinding.KategoriCardBinding
import com.muhammetcakir.yourdietprogramkotlin.databinding.KullanicilarCardBinding
import com.squareup.picasso.Picasso

private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
class KullaniciAdapter
    (private val kullanicilar: ArrayList<User>,private val onclick: Kullanicisil): RecyclerView.Adapter<KullaniciAdapter.KullaniciHolder>()

{
    class KullaniciHolder(val binding: KullanicilarCardBinding,onclick: Kullanicisil):RecyclerView.ViewHolder(binding.root)
    {
        val clickListener:Kullanicisil=onclick
        val kullaniciisim: TextView = binding.kullaniciisim
        val kullaniciemail: TextView = binding.kullaniciemail
        val kullanicisifre: TextView = binding.kullanicisifre
        val kullaniciImageView: ImageView = binding.kullanicifoto
        val sil: Button =binding.btnkullanicisil
        fun bindItems(item: User) {
            kullanicisifre.setText(item.sifre)
            kullaniciemail.setText(item.email)
            kullaniciisim.setText(item.isim)
            Picasso.get().load(item.ImageUrl).into(kullaniciImageView)

            sil.setOnClickListener{
                removedatabase(item.id)
                clickListener.onclick()
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