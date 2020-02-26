package com.degree.gami.service.social

import com.degree.gami.model.UserDao
import com.degree.gami.model.social.SocialAuthRequest
import com.degree.gami.model.social.SocialUserDto
import com.degree.gami.persistence.user.UserRepository
import com.degree.gami.persistence.user.social.SocialUser
import com.degree.gami.persistence.user.social.SocialUserRepository
import com.degree.gami.service.user.UserConverter
import com.degree.gami.service.user.UserService
import org.springframework.social.ApiBinding
import org.springframework.social.oauth2.AccessGrant
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

abstract class AbstractSocialService : SocialService {
    abstract val userService: UserService
    abstract val userRepository: UserRepository
    abstract val socialUserRepository: SocialUserRepository
    private val userConverter: UserConverter = UserConverter()

    override fun socialSignUp(socialAuthRequest: SocialAuthRequest): UserDao {
        val accessGrant = getAccessGrant(socialAuthRequest)
        val socialUserDto = getUserInfo(accessGrant)
        val registeredUser = register(socialUserDto)
        updateSocialUser(accessGrant, registeredUser, socialUserDto)
        return registeredUser
    }

    //    Protected Area
    /**
     * Updates the [SocialUser] information for the currently registered user
     *
     * @return the updated [SocialUser]
     */
    protected fun updateSocialUser(oldAccessGrant: AccessGrant, registeredUser: UserDao, socialUserDto: SocialUserDto): SocialUser {
        val socialUser = socialUserRepository.findBySocialIdAndSocialNetwork(socialUserDto.socialId/*userConverter.convertFromDao(registeredUser)*/, socialUserDto.network)
                ?: SocialUser()

        with(socialUser) {
            socialId = socialUserDto.socialId
            refreshToken = oldAccessGrant.refreshToken ?: oldAccessGrant.accessToken
            tokenExpiration = oldAccessGrant.expireTime ?: 1000000
            socialNetwork = socialUserDto.network
            user = userRepository.findByEMail(registeredUser.eMail!!)
            created = socialUser.created ?: LocalDateTime.now()
            updated = LocalDateTime.now()
        }

        return socialUserRepository.save(socialUser)
    }

    // todo add test to check @Valid annotation
    /**
     * Checks the database for an existing user and, if not found, proceeds to the signUp process using a random password
     *
     * @return the [UserDao] registered
     */
    protected fun register(@Valid userInfo: SocialUserDto): UserDao {
        val email = userInfo.email
        var userDao = userConverter.convertToDao(userRepository.findByEMail(email = email))
        if (userDao?.eMail == null) {
            userDao = UserDao(
                    eMail = email,
                    firstname = userInfo.firstName,
                    lastname = userInfo.lastName,
                    pass = UUID.randomUUID().toString(),
                    birthdate = userInfo.birthDate,
                    description = " ",
                    user = userInfo.email
            )
            userService.register(userDao)
        }

        return userDao!!
    }

    /**
     * Creates an [AccessGrant] from the provided social request
     *
     * Default implementation only uses the [SocialAuthRequest.token], for custom logic override this
     */
    protected fun getAccessGrant(socialAuthRequest: SocialAuthRequest): AccessGrant {
        val userAccessToken = socialAuthRequest.token
                ?: throw InvalidParameterException() //ApplicationException..invalidParameterException("User access token must be provided!")
        return AccessGrant(userAccessToken)
    }

    /**
     * Returns an [UserEntity][com.codegile.hobi.persistence.user.UserEntity] pre-filled with social data
     */
    abstract fun getUserInfo(@NotNull accessGrant: AccessGrant): SocialUserDto

    abstract fun getSocialApi(@NotNull accessGrant: AccessGrant): ApiBinding
}