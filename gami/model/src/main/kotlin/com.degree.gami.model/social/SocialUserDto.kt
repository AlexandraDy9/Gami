package com.degree.gami.model.social

import com.degree.gami.model.social.SocialNetwork
import java.time.LocalDate

data class SocialUserDto(
        val email: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val socialId: String = "",
        val birthDate: LocalDate? = null,
        val network: SocialNetwork = SocialNetwork.FACEBOOK
)