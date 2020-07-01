package com.degree.gami.persistence.location

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.event.EventEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "location")
class LocationEntity (
        @field:NotNull
        var longitude: Double? = null,

        @field:NotNull
        var latitude: Double? = null,

        @OneToMany(mappedBy= "location",cascade = [CascadeType.ALL])
        var events: MutableList<EventEntity>? = null
) : BaseEntity()
