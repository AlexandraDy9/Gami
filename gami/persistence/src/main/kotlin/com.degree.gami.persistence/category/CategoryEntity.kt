package com.degree.gami.persistence.category

import com.degree.gami.persistence.userentities.usercategory.UserCategoryEntity
import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.event.EventEntity
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity(name = "category")
@Inheritance(strategy = InheritanceType.JOINED)
class CategoryEntity(
        @Column(unique = true)
        @field:NotEmpty
        var name: String? = null,

        @field:NotEmpty
        var image: String? = null,

        @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
        var events: MutableList<EventEntity>? = null,

        @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
        var users: MutableList<UserCategoryEntity>? = null
) : BaseEntity()
