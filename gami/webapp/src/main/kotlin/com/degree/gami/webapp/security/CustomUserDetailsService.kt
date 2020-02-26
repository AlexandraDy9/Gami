package com.degree.gami.webapp.security

import com.degree.gami.persistence.user.UserEntity
import com.degree.gami.persistence.user.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserEntity {
        return userRepository.findByUser(username)
    }
}