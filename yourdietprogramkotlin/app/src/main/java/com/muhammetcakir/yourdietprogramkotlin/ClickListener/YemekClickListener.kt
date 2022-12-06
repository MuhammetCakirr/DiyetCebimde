package com.muhammetcakir.yourdietprogramkotlin.ClickListener


import com.muhammetcakir.yourdietprogramkotlin.Models.Yemek

interface YemekClickListener
{
    fun onClick(yemek: Yemek)
}

interface YemekGuncelleClickListener
{
    fun onClick2(yemek: Yemek)
}

