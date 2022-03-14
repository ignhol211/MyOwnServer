package com.example.server

import com.google.gson.Gson

class MensajeAdministrador(val mensaje:Mensaje, val clave:String) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}