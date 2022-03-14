package com.example.server

import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario,String>
interface AdminRepository : JpaRepository<Admin,String>
interface MensajeRepository : JpaRepository<Mensaje,Int>
