package com.degree.gami.persistence.userentities.userevents

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.event.EventEntity
import com.degree.gami.persistence.user.UserEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "user_event")
class UserEventEntity(
        var rating: Double? = null,

        var review: String? = null,

        @field:NotNull
        @JoinColumn(name = "is_bookmark")
        var isBookmark: Boolean = false,

        @field:NotNull
        @JoinColumn(name = "is_host")
        var isHost: Boolean = false,

        @field:NotNull
        @JoinColumn(name = "is_join")
        var isJoin: Boolean = false,

        @ManyToOne
        @JoinColumn(name = "fk_event")
        var event: EventEntity? = null,

        @ManyToOne
        @JoinColumn(name = "fk_user")
        var user: UserEntity? = null
) : BaseEntity()
