package com.degree.gami.persistence.agerange

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.event.EventEntity
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity(name = "age_range")
@Inheritance(strategy = InheritanceType.JOINED)
class AgeRangeEntity (
        @field:NotNull
        var ageMin: Int? = null,

        @field:NotNull
        var ageMax: Int? = null,

        @OneToMany(mappedBy = "ageRange", cascade = [CascadeType.ALL])
        var events: MutableList<EventEntity>? = null
): BaseEntity()

