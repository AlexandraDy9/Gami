package com.degree.gami.persistence.event

import com.degree.gami.persistence.agerange.AgeRangeEntity
import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.category.CategoryEntity
import com.degree.gami.persistence.location.LocationEntity
import com.degree.gami.persistence.timerange.TimeRangeEntity
import com.degree.gami.persistence.userentities.userevents.UserEventEntity
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity(name = "event")
class EventEntity(
        @Column(unique = true)
        @field:NotEmpty
        var name: String? = null,

        @field:NotEmpty
        @Column(length = 1000)
        var description: String? = null,

        @field:NotNull
        var numberOfAttendees: Int? = null,

        @ManyToOne
        @JoinColumn(name = "fk_category")
        var category: CategoryEntity? = null,

        @ManyToOne
        @JoinColumn(name = "fk_location")
        var location: LocationEntity? = null,

        @ManyToOne
        @JoinColumn(name = "fk_age_range")
        var ageRange: AgeRangeEntity? = null,

        @ManyToOne
        @JoinColumn(name = "fk_time_range")
        var timeRange: TimeRangeEntity? = null,

        @OneToMany(mappedBy = "event",cascade = [CascadeType.ALL])
        var userEvents: MutableList<UserEventEntity>? = null
): BaseEntity()

