package com.muhammetcakir.yourdietprogramkotlin.Adapters

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.muhammetcakir.yourdietprogramkotlin.Models.Diyet
import com.muhammetcakir.yourdietprogramkotlin.databinding.OneridiyetlerBinding

class OneriDiyetlerAdapter(private  val diyetler:ArrayList<Diyet>) : RecyclerView.Adapter<OneriDiyetlerAdapter.PostHolder>()

{
    class PostHolder(val binding: OneridiyetlerBinding): RecyclerView.ViewHolder(binding.root) {

        val sabahogun1: TextView =binding.sabahogundiyet1
        val sabahogun2: TextView =binding.sabahogundiyet2
        val sabahogun3: TextView =binding.sabahogundiyet3

        val ogleogun1: TextView =binding.ogleogundiyet1
        val ogleogun2: TextView =binding.ogleogundiyet2
        val ogleogun3: TextView =binding.ogleogundiyet3

        val aksamogun1: TextView =binding.aksamogundiyet1
        val aksamogun2: TextView =binding.aksamogundiyet2
        val aksamogun3: TextView =binding.aksamogundiyet3

        val araogun1: TextView =binding.Araogundiyet1
        val araogun2: TextView =binding.Araogundiyet2
        val not: TextView =binding.notdiyet1

        val hangigun: TextView =binding.hangigun


        fun bindItems(item: Diyet) {
            sabahogun1.setText("-" + item.sabahogun1)
            sabahogun2.setText("-" + item.sabahogun2)
            sabahogun3.setText("-" + item.sabahogun3)
            ogleogun1.setText("-" + item.ogleogun1)
            ogleogun2.setText("-" + item.ogleogun2)
            ogleogun3.setText("-" + item.ogleogun3)
            aksamogun1.setText("-" + item.aksamogun1)
            aksamogun2.setText("-" + item.aksamogun2)
            aksamogun3.setText("-" + item.aksamogun3)
            araogun1.setText("-" + item.araogun1)
            araogun2.setText("-" + item.araogun2)
            not.setText("-" + item.not)
            hangigun.setText(item.hangigun.uppercase())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = OneridiyetlerBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return OneriDiyetlerAdapter.PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

            holder.bindItems(diyetler[position])

    }

    override fun getItemCount(): Int {
        return  diyetler.size
    }

}


