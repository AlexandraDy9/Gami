package com.degree.gami.persistence.user.social

import com.degree.gami.model.social.SocialNetwork
import com.degree.gami.persistence.base.TimestampedEntity
import com.degree.gami.persistence.user.UserEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class SocialUser(
        var socialId: String? = null,

        var refreshToken: String? = null,

        @Enumerated(EnumType.STRING)
        var socialNetwork: SocialNetwork? = null,

        var tokenExpiration: Long? = null,

        @OneToOne
        @JoinColumn(name = "user_id", nullable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JsonIgnore
        var user: UserEntity? = null
) : TimestampedEntity()