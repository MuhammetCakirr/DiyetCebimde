package com.muhammetcakir.yourdietprogramkotlin.Models

var DietCesitList = ArrayList<DietCesit>()

val Diet_ID_EXTRA = "dietExtra"

class DietCesit(
    var cover: Int,
    var KısaAcıklama: String,
    var Adı: String,
    var Acıklaması: String,
    val id: Int? = DietCesitList.size
)

