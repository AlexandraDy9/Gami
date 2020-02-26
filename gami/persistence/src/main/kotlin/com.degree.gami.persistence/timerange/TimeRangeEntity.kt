package com.degree.gami.persistence.timerange

import com.degree.gami.persistence.base.BaseEntity
import com.degree.gami.persistence.event.EventEntity
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "time_range")
@Inheritance(strategy = InheritanceType.JOINED)
class TimeRangeEntity(
        @field:NotNull
        var startTime: LocalDateTime? = null,

        @field:NotNull
        var endTime: LocalDateTime? = null,

        @OneToMany(mappedBy = "timeRange",cascade = [CascadeType.ALL])
        var events: MutableList<EventEntity>? = null
) : BaseEntity()
