package com.example.server

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serial
import javax.persistence.Id


//@Serializable
data class Mensaje(
    var texto:String,
    @SerializedName ("usuarioId")
    val usuarioId:String,
    @Id val id:Int,
    var time: Long = System.currentTimeMillis())
    {
        override fun toString():String{
            val gson = Gson()
            return gson.toJson(this)
        }
    }