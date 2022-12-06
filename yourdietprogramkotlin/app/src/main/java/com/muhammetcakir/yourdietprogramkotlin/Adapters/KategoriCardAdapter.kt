package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammetcakir.yourdietprogramkotlin.ClickListener.KategoriClickListener

import com.muhammetcakir.yourdietprogramkotlin.Models.YemekKategori

import com.muhammetcakir.yourdietprogramkotlin.databinding.KategoriCardBinding
import com.squareup.picasso.Picasso

class KategoriCardAdapter(
    private val yemekKategori: ArrayList<YemekKategori>,
    private val clickListener: KategoriClickListener
)
    : RecyclerView.Adapter<KategoriCardAdapter.KategoriHolder>()
{
    class KategoriHolder(val binding:KategoriCardBinding,private val clickListener: KategoriClickListener ) : RecyclerView.ViewHolder(binding.root) {
        val categoryName: TextView = binding.categoryName
        val categoryImageView: ImageView = binding.ivCategoryImage


        fun bindItems(item: YemekKategori) {

            categoryName.setText(item.kategoriadı)
            Picasso.get().load(item.ImageUrl).into(categoryImageView)
            binding.kategoricard.setOnClickListener {
                clickListener.onClick(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriHolder
    {
        val from = LayoutInflater.from(parent.context)
        val binding = KategoriCardBinding.inflate(from, parent, false)
        return KategoriCardAdapter.KategoriHolder(binding, clickListener)
      //  val binding =KategoriCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       //  return KategoriHolder(binding,clickListener)
    }

    override fun getItemCount(): Int {
      return  yemekKategori.size
    }

    override fun onBindViewHolder(holder: KategoriHolder, position: Int) {
        holder.bindItems(yemekKategori[position])

    }
}