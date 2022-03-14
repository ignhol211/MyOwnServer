package com.example.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Database {
    @Bean
    fun initDatabase(repository: UsuarioRepository, adminRepository:AdminRepository, mensajeRepository: MensajeRepository):CommandLineRunner{
        return CommandLineRunner{
            val administrador = Admin("DAM2","123456")
            adminRepository.save(administrador)
        }
    }
}