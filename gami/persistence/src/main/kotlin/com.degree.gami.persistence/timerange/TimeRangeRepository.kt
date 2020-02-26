package com.degree.gami.persistence.timerange

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TimeRangeRepository: JpaRepository<TimeRangeEntity, Long>{

    fun findByStartTimeAndEndTime(startTime: LocalDateTime, endTime: LocalDateTime): TimeRangeEntity?
}