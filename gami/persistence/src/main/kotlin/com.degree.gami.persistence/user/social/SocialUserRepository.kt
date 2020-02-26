package com.degree.gami.persistence.user.social

import com.degree.gami.model.social.SocialNetwork
import org.springframework.data.jpa.repository.JpaRepository

interface SocialUserRepository : JpaRepository<SocialUser, Long> {

    fun findBySocialIdAndSocialNetwork(socialId: String, socialNetwork: SocialNetwork): SocialUser?
}