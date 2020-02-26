package com.degree.gami.service.event

import com.degree.gami.model.AgeRangeDao
import com.degree.gami.model.EventDao
import com.degree.gami.persistence.agerange.AgeRangeEntity
import com.degree.gami.persistence.event.EventEntity
import org.springframework.stereotype.Service


@Service
class EventConverter(val validation: EventValidation) {

    fun convertFromDao(eventDao: EventDao) : EventEntity {
        val category = validation.checkValidCategory(eventDao)
        val location = validation.checkValidLocation(eventDao)
        val ageRange = validation.checkValidAgeRange(eventDao)
        val timeRange = validation.checkValidTimeRange(eventDao)

        return EventEntity(eventDao.name, eventDao.description, eventDao.numberOfAttendees, category, location, ageRange, timeRange)
    }

    fun convertToDao(event: EventEntity) : EventDao {
        return EventDao(event.name.toString(),
                event.description.toString(),
                event.numberOfAttendees!!.toInt(),
                event.category?.name.toString(),
                event.location?.longitude!!.toDouble(),
                event.location?.latitude!!.toDouble(),
                event.ageRange?.ageMin!!,
                event.ageRange?.ageMax!!,
                event.timeRange?.startTime!!,
                event.timeRange?.endTime!!
        )
    }

    fun convertToAgeRangeDao(ageRangeEntity: AgeRangeEntity): AgeRangeDao =
            AgeRangeDao(ageRangeEntity.ageMin!!, ageRangeEntity.ageMax!!)
}