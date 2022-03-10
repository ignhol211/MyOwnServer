package com.example.server

import com.google.gson.Gson
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RequestController (private val repository: Repository) {

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody datos:Usuario):Any{
        val posibleUsuario = repository.findById(datos.nombre)
        return if(!posibleUsuario.isPresent){
            val usuario = Usuario(datos.nombre,datos.pass, clave = generarClaveAleatoria())
            repository.save(usuario)
            usuario.clave
        }else{
            val usuario = repository.getById(datos.nombre)
            if (usuario.pass != datos.pass){
                val error = Error(1,"Contraseña inválida")
                error.toString()
            }else{
                usuario.clave
            }
        }
    }

    @PostMapping("crearMensaje")
    fun crearMensaje(@RequestBody datos:Mensaje):Any{
        //val gson = Gson()
        //val dataFromJson = gson.fromJson(datos,Mensaje::class.java)
        val posibleUsuario = repository.findById(datos.usuarioId)

        return if(!posibleUsuario.isPresent){
            val error = Error(2,"Usuario inexistente")
            error
        }else{
            val usuario = repository.getById(datos.usuarioId)
            val mensaje = Mensaje(datos.texto,datos.usuarioId,System.currentTimeMillis())
            usuario.mensajes?.add(mensaje)
            return "Success"
        }
    }

    @PostMapping("descargarMensajes")
    fun descargarMensajes(@RequestBody datos:String):Any{
       return repository.findAll().filter { it.mensajes != null }
    }

    @PostMapping("descargarMensajesFiltrados")
    fun descargarMensajesFiltrados(@RequestBody datos:String):Any{
        return 0
    }


}

fun generarClaveAleatoria():String{
    var clave = ""
    repeat(20){
        clave += (0..9).random()
    }
    return clave
}