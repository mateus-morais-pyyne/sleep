package com.noom.interview.fullstack.sleep.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

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