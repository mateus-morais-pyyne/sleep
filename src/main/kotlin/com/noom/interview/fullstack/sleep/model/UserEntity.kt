package com.noom.interview.fullstack.sleep.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tb_users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    val id: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "age", nullable = false)
    var age: Int,

    @Column(name = "email", nullable = false, unique = true)
    var email: String
) {
}