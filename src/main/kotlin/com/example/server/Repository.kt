package com.example.server

import org.springframework.data.jpa.repository.JpaRepository

interface Repository : JpaRepository<Usuario,String>
interface AdminRepository : JpaRepository<Admin,String>
