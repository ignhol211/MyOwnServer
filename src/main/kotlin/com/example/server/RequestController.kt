package com.example.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RequestController (private val repository: Repository) {

    /*
    curl --request POST  --header "Content-type:application/json; charset=utf-8" --data "{\"nombre\":\"U4\",\"pass\":\"1234\"}" localhost:8083/crearUsuario
    curl --request POST  --header "Content-type:application/json; charset=utf-8" --data "{\"nombre\":\"U2\",\"pass\":\"12\"}" localhost:8083/crearUsuario
     */

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody datos:Usuario): Any? {
        val posibleUsuario = repository.findById(datos.nombre)
        return if(!posibleUsuario.isPresent){
            val usuario = Usuario(datos.nombre,datos.pass, clave = generarClaveAleatoria())
            repository.save(usuario)
            usuario.clave
        }else{
            val usuario = repository.getById(datos.nombre)
            if (usuario.pass != datos.pass){
                val error = Error(1,"Contraseña inválida")
                error
            }else{
                usuario.clave
            }
        }
    }

    /*
    curl --request POST  --header "Content-type:application/json" --data "{\"texto\":\"TextoCifrado2\",\"usuarioId\":\"U2\",\"id\":0}" localhost:8083/crearMensaje
    curl --request POST  --header "Content-type:application/json" --data "{\"texto\":\"TextoCifrado4\",\"usuarioId\":\"U4\",\"id\":1}" localhost:8083/crearMensaje
     */

    @PostMapping("crearMensaje")
    fun crearMensaje(@RequestBody mensaje:Mensaje):Any{
        val posibleUsuario = repository.findById(mensaje.usuarioId)
        return if(posibleUsuario.isPresent){
            val usuario = posibleUsuario.get()
            usuario.mensajes.add(mensaje)
            repository.save(usuario)
            "Success"
        }else{
            val error = Error(2,"Usuario inexistente")
            error
        }
    }

    @GetMapping("descargarMensajes")
    fun descargarMensajes():ArrayList<Mensaje>{
        val listToReturn = ListaDeMensajes(ArrayList())
        (repository.findAll().filter { it.mensajes.isNotEmpty() }).forEach { it1 ->
            listToReturn.list.addAll(it1.mensajes)
        }
        return listToReturn.list
    }

    @PostMapping("descargarMensajesFiltrados")
    fun descargarMensajesFiltrados(@RequestBody datos:String):Any{
        return 0
    }

}

fun generarClaveAleatoria():String{
    var clave = ""
    repeat(20){clave += (0..9).random()}
    return clave
}