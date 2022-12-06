package com.muhammetcakir.yourdietprogramkotlin.Adapters

import com.squareup.picasso.Picasso


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.yenile
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek
import com.muhammetcakir.yourdietprogramkotlin.Models.favorilerimList
import com.muhammetcakir.yourdietprogramkotlin.R

import com.muhammetcakir.yourdietprogramkotlin.databinding.CardCellBinding

class FavorilerimAdapter(
    private val favorilerim: List<Yemek>,
    private val clickListener: YemekClickListener,
    private val clickListener2: yenile,


    )
    : RecyclerView.Adapter<FavorilerimAdapter.CardViewHolder>()
{
    class CardViewHolder(private val cardCellBinding:CardCellBinding, private val clickListener: YemekClickListener, private val clickListener2: yenile ) : RecyclerView.ViewHolder(cardCellBinding.root)

    {
        fun bindyemek(yemek: Yemek)
        {
            Picasso.get().load(yemek.ImageUrl).into(cardCellBinding.cover)

            cardCellBinding.title.text = yemek.yemekadi

            cardCellBinding.cardView.setOnClickListener{
                clickListener.onClick(yemek)

            }

            if (favorilerimList.contains(yemek))
            {
                cardCellBinding.favimage.setImageResource(R.drawable.kirmizifav)
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
                    clickListener2.onclick()
                }
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
    {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener,clickListener2)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int)
    {
        holder.bindyemek(favorilerim[position])
    }

    override fun getItemCount(): Int = favorilerim.size
}