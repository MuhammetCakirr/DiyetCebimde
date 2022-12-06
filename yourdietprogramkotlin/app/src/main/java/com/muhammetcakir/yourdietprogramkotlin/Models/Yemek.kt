package com.muhammetcakir.yourdietprogramkotlin.Models


var yemekList = ArrayList<Yemek>()
var bugununyemekleri= ArrayList<Yemek>()
var favorilerimList= mutableListOf<Yemek>()


val YEMEK_ID_EXTRA = "bookExtra"

class Yemek(
    val id: String,
    var ImageUrl: String,
    var yemekadi: String,
    var acıklamasi: String,
    var icindekiler: String,
    var karbmiktari:String,
    var proteinmiktari:String,
    var yagmiktari:String,
    var toplamkalori:String,
    var yapımsuresi:String,
    var kategorisi:String,
)

