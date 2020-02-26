package com.degree.gami.persistence.userentities.userphoto

import com.degree.gami.persistence.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserPhotoRepository: JpaRepository<UserPhotoEntity, Long>{

    fun deleteByUser(userEntity: UserEntity?)
}