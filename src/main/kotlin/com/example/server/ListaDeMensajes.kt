package com.example.server

import com.google.gson.Gson

class ListaDeMensajes (var list:List<Mensaje>) {
    override fun toString():String{
        val gson = Gson()
        return gson.toJson(this)
    }
}