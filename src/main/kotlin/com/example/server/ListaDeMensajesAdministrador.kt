package com.example.server

import com.google.gson.Gson

class ListaDeMensajesAdministrador(var lista:MutableList<MensajeAdministrador>) {
    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }
}