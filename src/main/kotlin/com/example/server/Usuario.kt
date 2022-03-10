package com.example.server

import com.google.gson.Gson
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Usuario (@Id val nombre:String, var pass:String,var mensajes:ArrayList<Mensaje>? = null, val clave:String) {

    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }

}
