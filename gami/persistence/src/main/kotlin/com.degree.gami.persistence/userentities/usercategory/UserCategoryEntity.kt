package com.degree.gami.persistence.userentities.usercategory

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.category.CategoryEntity
import com.degree.gami.persistence.user.UserEntity
import javax.persistence.*

@Entity(name = "user_category")
@Inheritance(strategy = InheritanceType.JOINED)
class UserCategoryEntity(
        @ManyToOne
        @JoinColumn(name = "fk_category")
        var category: CategoryEntity? = null,

        @ManyToOne
        @JoinColumn(name = "fk_user")
        var user: UserEntity? = null
) : BaseEntity()
