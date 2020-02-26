package com.degree.gami.service.social

import com.degree.gami.model.social.SocialNetwork
import com.degree.gami.model.social.SocialUserDto
import com.degree.gami.persistence.user.UserRepository
import com.degree.gami.persistence.user.social.SocialUserRepository
import com.degree.gami.service.user.UserService
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.User
import org.springframework.social.facebook.connect.FacebookConnectionFactory
import org.springframework.social.oauth2.AccessGrant
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.validation.constraints.NotNull

@Service(value = "facebookSocialService")
class FacebookServiceBean(
        val facebookConnectionFactory: FacebookConnectionFactory,
        override val userService: UserService,
        override val userRepository: UserRepository,
        override val socialUserRepository: SocialUserRepository
) : AbstractSocialService() {

    override fun getSocialApi(@NotNull accessGrant: AccessGrant): Facebook {
        val facebookConnection = facebookConnectionFactory.createConnection(accessGrant)
        return facebookConnection.api
    }

    override fun getUserInfo(accessGrant: AccessGrant): SocialUserDto {
        var facebookUser: User = getFacebookUser(accessGrant) //facebookUser =
        return SocialUserDto(
                email = facebookUser.email,
                firstName = facebookUser.firstName,
                lastName = facebookUser.lastName,
                socialId = facebookUser.id,
                birthDate = if (!facebookUser.birthday.isNullOrBlank()) LocalDate.parse(changeDateOrder(facebookUser.birthday)) else LocalDate.parse("2010-10-10"),
                network = SocialNetwork.FACEBOOK
        )
    }

    private fun changeDateOrder(inDate: String): String {
        val inSDF = SimpleDateFormat("mm/dd/yyyy")
        val outSDF = SimpleDateFormat("yyyy-mm-dd")
        var date: Date = inSDF.parse(inDate);
        return outSDF.format(date)
    }

    private fun getFacebookUser(accessGrant: AccessGrant): User {
        val facebook = getSocialApi(accessGrant)
        val fields = arrayOf("id", "email", "first_name", "last_name", "birthday")
        return facebook.fetchObject("me", User::class.java, *fields)
    }
}