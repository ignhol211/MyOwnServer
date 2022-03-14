package com.example.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@RestController
class RequestController (private val usuarioRepository: UsuarioRepository, private val adminRepository: AdminRepository, private val mensajeRepository: MensajeRepository) {

    /*
    curl --request POST  --header "Content-type:application/json; charset=utf-8" --data "{\"nombre\":\"U4\",\"pass\":\"1234\"}" localhost:8083/crearUsuario
    curl --request POST  --header "Content-type:application/json; charset=utf-8" --data "{\"nombre\":\"U2\",\"pass\":\"12\"}" localhost:8083/crearUsuario
     */

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody datos:Usuario): Any? {
        val posibleUsuario = usuarioRepository.findById(datos.nombre)
        return if(!posibleUsuario.isPresent){
            val usuario = Usuario(datos.nombre,datos.pass, clave = generarClaveAleatoria())
            usuarioRepository.save(usuario)
            usuario.clave
        }else{
            val usuario = usuarioRepository.getById(datos.nombre)
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
        val posibleUsuario = usuarioRepository.findById(mensaje.usuarioId)
        return if(posibleUsuario.isPresent){
            mensajeRepository.save(mensaje)
            "Success"
        }else{
            val error = Error(2,"Usuario inexistente")
            error
        }
    }

    @GetMapping("descargarMensajes")
    fun descargarMensajes(): ListaDeMensajes {
        return ListaDeMensajes(mensajeRepository.findAll())
    }

    @GetMapping("descargarMensajesFiltrados/{datos}")
    fun descargarMensajesFiltrados(@PathVariable datos:String):ListaDeMensajes{
        return ListaDeMensajes(mensajeRepository.findAll().filter{it.texto.contains(datos)})
    }
    //{"list":[{"texto":"TextoCifrado2","usuarioId":"U2","id":0,"time":1647293675362},{"texto":"TextoCifrado4","usuarioId":"U4","id":1,"time":1647293676450}]}
    //{"list":[{"texto":"Mensaje 1","usuarioId":"U1","id":1,"time":1645181881399},{"texto":"Mensaje 2","usuarioId":"U2","id":2,"time":1645181881401},{"texto":"Mensaje 3","usuarioId":"U3","id":3,"time":1645181881403}]}


    //curl --request GET  --header "Content-type:application/json" --data "{\"nombre\":\"DAM2\",\"pass\":\"123456\"}" localhost:8083/obtenerMensajesYLlaves
    @GetMapping("obtenerMensajesYLlaves")
    fun obtenerMensajesYLlaves(@RequestBody admin:Admin): Any {
        var clave:String
        val posibleAdministrador = adminRepository.findById(admin.nombre)
        if(posibleAdministrador.isPresent){
            val administrador = posibleAdministrador.get()
            return if(admin.pass == administrador.pass){
                val objetoLista = ListaDeMensajesAdministrador(mutableListOf())
                mensajeRepository.findAll().forEach{
                    clave = usuarioRepository.getById(it.usuarioId).clave.toString()
                    objetoLista.lista.add(MensajeAdministrador(it,clave))
                }
                objetoLista
            } else {
                Error(3,"Pass de administrador incorrecta")
            }
        }
        return Error(4,"Pass de administrador incorrecta")
    }

    @GetMapping("obtenerMensajesDescifrados")
    fun obtenerMensajesDescifrados(@RequestBody admin:Admin):Any{
        var clave:String
        val posibleAdministrador = adminRepository.findById(admin.nombre)
        if(posibleAdministrador.isPresent){
            val administrador = posibleAdministrador.get()
            return if(admin.pass == administrador.pass){
                val objetoLista = ListaDeMensajesAdministrador(mutableListOf())
                mensajeRepository.findAll().forEach{
                    clave = usuarioRepository.getById(it.usuarioId).clave.toString()
                    try {
                        objetoLista.lista.add(
                            MensajeAdministrador(
                                (Mensaje(
                                    descifrar(
                                        it.texto,
                                        usuarioRepository.getById(it.usuarioId).clave.toString()
                                    ), it.usuarioId, it.id
                                )), clave
                            )
                        )
                    }catch (e:Exception){
                        return "Texto indescifrable"
                    }
                }
                objetoLista
            } else {
                Error(3,"Pass de administrador incorrecta")
            }
        }
        return Error(4,"Pass de administrador incorrecta")
    }

}

fun generarClaveAleatoria():String{
    var clave = ""
    repeat(20){clave += (0..9).random()}
    return clave
}

@Throws(BadPaddingException::class)
private fun descifrar(textoCifradoYEncodado: String, llaveEnString: String): String {
    val type = "AES/ECB/PKCS5Padding"
    val cipher = Cipher.getInstance(type)
    cipher.init(Cipher.DECRYPT_MODE, getKey(llaveEnString))
    val textCifradoYDencodado = Base64.getUrlDecoder().decode(textoCifradoYEncodado)
    val textDescifradoYDesencodado = String(cipher.doFinal(textCifradoYDencodado))
    return textDescifradoYDesencodado
}

private fun getKey(llaveEnString: String): SecretKeySpec {
    var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
    val sha = MessageDigest.getInstance("SHA-1")
    llaveUtf8 = sha.digest(llaveUtf8)
    llaveUtf8 = llaveUtf8.copyOf(16)
    return SecretKeySpec(llaveUtf8, "AES")
}