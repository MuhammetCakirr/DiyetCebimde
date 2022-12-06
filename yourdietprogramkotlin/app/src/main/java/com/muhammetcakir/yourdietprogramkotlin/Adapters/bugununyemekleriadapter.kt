package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.YemekClickListener
import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek
import com.muhammetcakir.yourdietprogramkotlin.databinding.BugununYemekleriBinding
import com.squareup.picasso.Picasso

class bugununyemekleriadapter(
    private val yemekler: ArrayList<Yemek>,
    private val clickListener: YemekClickListener
)
    : RecyclerView.Adapter<bugununyemekleriadapter.CardViewHolder>()
{
    class CardViewHolder(private val cardCellBinding: BugununYemekleriBinding, private val clickListener: YemekClickListener) : RecyclerView.ViewHolder(cardCellBinding.root)

    {
        fun bindyemek(yemek: Yemek)
        {
            Picasso.get().load(yemek.ImageUrl).into(cardCellBinding.yemekfoto)

            cardCellBinding.yemekisim.text = yemek.yemekadi
            // cardCellBinding.author.text = yemek.acıklamasi

            cardCellBinding.cardyemek.setOnClickListener{
                clickListener.onClick(yemek)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
    {
        val from = LayoutInflater.from(parent.context)
        val binding = BugununYemekleriBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int)
    {
        holder.bindyemek(yemekler[position])
    }

    override fun getItemCount(): Int = yemekler.size

}