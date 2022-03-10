package com.example.server

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Admin(@Id val nombre:String, val pass:String) {
}