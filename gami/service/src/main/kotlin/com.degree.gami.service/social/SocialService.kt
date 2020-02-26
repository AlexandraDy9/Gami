package com.degree.gami.service.social

import com.degree.gami.model.UserDao
import com.degree.gami.model.social.SocialAuthRequest
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@Validated
interface SocialService {

    /**
     * Starts the social registration process
     *
     * @param socialAuthRequest the POJO carrying the socialLogin information
     * @return the registered [User]
     */
    fun socialSignUp(@NotNull socialAuthRequest: SocialAuthRequest): UserDao
}