package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekGuncelleClickListener
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek
import com.muhammetcakir.yourdietprogramkotlin.Models.favorilerimList
import com.muhammetcakir.yourdietprogramkotlin.R
import com.muhammetcakir.yourdietprogramkotlin.currentUser
import com.muhammetcakir.yourdietprogramkotlin.databinding.CardCellBinding


import com.squareup.picasso.Picasso
private  var db : FirebaseFirestore = FirebaseFirestore.getInstance()
private  var auth1 : FirebaseAuth= FirebaseAuth.getInstance()
class CardAdapter(
    private val yemekler: ArrayList<Yemek>,
    private val clickListener: YemekClickListener,
    private val clickListener2: YemekGuncelleClickListener

    )
        : RecyclerView.Adapter<CardAdapter.CardViewHolder>()
{
    class CardViewHolder(private val cardCellBinding:CardCellBinding, private val clickListener: YemekClickListener, private val clickListener2: YemekGuncelleClickListener) : RecyclerView.ViewHolder(cardCellBinding.root)

    {
        fun bindyemek(yemek: Yemek)
        {
            if(auth1.currentUser!!.email.toString()=="mami1@gmail.com")
            {
                cardCellBinding.cardbuttonsil.visibility=View.VISIBLE
                cardCellBinding.cardbutton.visibility=View.GONE
                cardCellBinding.cardbuttonguncelle.visibility=View.VISIBLE
            }

            cardCellBinding.cardbuttonguncelle.setOnClickListener {
               clickListener2.onClick2(yemek)
            }

            Picasso.get().load(yemek.ImageUrl).into(cardCellBinding.cover)
            cardCellBinding.title.text = yemek.yemekadi
           // cardCellBinding.author.text = yemek.acıklamasi

            cardCellBinding.cardView.setOnClickListener{
                clickListener.onClick(yemek)
            }

            if (favorilerimList.contains(yemek))
            {
                cardCellBinding.favimage.setImageResource(R.drawable.kirmizifav)
            }

            cardCellBinding.cardbuttonsil.setOnClickListener {
                removedatabase(yemek.id)
            }
            cardCellBinding.cardbutton.setOnClickListener{
                if (favorilerimList.contains(yemek)==false)
                {
                    favorilerimList.add(yemek!!)
                    cardCellBinding.favimage.setImageResource(R.drawable.kirmizifav)
                }
                else
                {
                    favorilerimList.remove(yemek)
                    cardCellBinding.favimage.setImageResource(R.drawable.favoriteicibos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
    {
        val from = LayoutInflater.from(parent.context)
        val binding = com.muhammetcakir.yourdietprogramkotlin.databinding.CardCellBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener,clickListener2)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int)
    {
        holder.bindyemek(yemekler[position])
    }

    override fun getItemCount(): Int = yemekler.size
}

private fun removedatabase(index:String)
{
    db.collection("Yemekler").document(index)
        .delete()
        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
}
