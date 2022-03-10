package com.example.server

import com.google.gson.Gson
import javax.persistence.GeneratedValue
import javax.persistence.Id

class Mensaje (var texto:String, var usuarioId:String, var time:Long) {

    @Id
    @GeneratedValue
    var int = 0

    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }
}
