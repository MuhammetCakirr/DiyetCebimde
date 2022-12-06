package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.DietClickListener

import com.muhammetcakir.yourdietprogramkotlin.Models.DietCesit
import com.muhammetcakir.yourdietprogramkotlin.databinding.CardCellBinding
import com.muhammetcakir.yourdietprogramkotlin.databinding.DietcesitlericardBinding

class DietCardAdapter (
    private val diets: List<DietCesit>,
    private val clickListener: DietClickListener
    )
    : RecyclerView.Adapter<DietCardAdapter.DietCardViewHolder>()
{
    class DietCardViewHolder(
        private val cardCellBinding: DietcesitlericardBinding,

        private val clickListener: DietClickListener
    ) : RecyclerView.ViewHolder(cardCellBinding.root)
    {
        fun binddiet(diet: DietCesit)
        {
            //cardCellBinding.cardView.visibility= View.GONE
            cardCellBinding.cover.setImageResource(diet.cover)
            cardCellBinding.title.text = diet.Adı

            cardCellBinding.cardView.setOnClickListener{
                clickListener.onClick(diet)
            }
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietCardViewHolder
        {
            val from = LayoutInflater.from(parent.context)
            val binding = DietcesitlericardBinding.inflate(from, parent, false)
            return DietCardViewHolder(binding, clickListener)
        }

        override fun onBindViewHolder(holder: DietCardViewHolder, position: Int)
        {
            holder.binddiet(diets[position])
        }

        override fun getItemCount(): Int = diets.size
    }
