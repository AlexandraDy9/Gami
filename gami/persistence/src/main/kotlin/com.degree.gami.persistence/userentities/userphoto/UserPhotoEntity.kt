package com.degree.gami.persistence.userentities.userphoto

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.user.UserEntity
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity(name = "user_photo")
class UserPhotoEntity (
    @field:NotEmpty
    @Column(length = 1000)
    var image: String? = null,

    @ManyToOne
    @JoinColumn(name = "fk_user")
    var user: UserEntity? = null
) : BaseEntity()