package com.degree.gami.service.social.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.social.facebook.connect.FacebookConnectionFactory

@Configuration
@ConfigurationProperties(prefix = "spring.social.facebook")
class FacebookConfiguration {
    lateinit var appId: String
    lateinit var secret: String

    @Bean
    fun facebookConnectionFactory(): FacebookConnectionFactory {
        println("Creating FacebookConnectionFactory with $appId and $secret")
        return FacebookConnectionFactory(appId, secret)
    }
}