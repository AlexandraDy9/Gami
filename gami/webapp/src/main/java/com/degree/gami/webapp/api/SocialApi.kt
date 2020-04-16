package com.degree.gami.webapp.api

import com.degree.gami.model.UserDao
import com.degree.gami.model.social.SocialAuthRequest
import com.degree.gami.model.social.SocialNetwork
import com.degree.gami.service.social.SocialService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = ["api/social"])
class SocialApi(
        @Qualifier(value = "facebookSocialService")
        val facebookSocialService: SocialService
) {

    @PostMapping(value = ["signup"])
    fun socialSignup(@RequestHeader authorization: String, @Valid @RequestBody socialAuthRequest: SocialAuthRequest,
                     response: HttpServletResponse): UserDao {
        val user = getSocialService(socialAuthRequest.network!!).socialSignUp(socialAuthRequest)
        response.addHeader("Authorization", authorization)
        return user
    }

    private fun getSocialService(socialNetwork: SocialNetwork): SocialService {
        return when (socialNetwork) {
            SocialNetwork.FACEBOOK -> facebookSocialService
            else -> throw Exception("$socialNetwork social network is not yet supported!")
        }
    }
}