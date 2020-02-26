package com.degree.gami.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
     fun findByUser(name: String) : UserEntity
     fun findByEMail(email: String): UserEntity?
}