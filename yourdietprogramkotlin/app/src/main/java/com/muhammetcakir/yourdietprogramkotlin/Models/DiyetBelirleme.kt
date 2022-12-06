package com.muhammetcakir.yourdietprogramkotlin.Models

class DiyetBelirleme(
    var Id:String,
    var Boy:Double,
    var Kilo:Int,
    var Cinsiyet:Boolean,
    var Hastalik:Boolean,
    var Spor:Boolean,
    var Bki: Double = (Kilo/(Boy*Boy))/10

){
}