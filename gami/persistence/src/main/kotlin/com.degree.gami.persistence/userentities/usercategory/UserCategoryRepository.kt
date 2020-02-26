package com.degree.gami.persistence.userentities.usercategory

import com.degree.gami.persistence.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCategoryRepository: JpaRepository<UserCategoryEntity, Long>{

    fun deleteByUser(userEntity: UserEntity?)
}