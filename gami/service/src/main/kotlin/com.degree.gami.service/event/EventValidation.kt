package com.degree.gami.service.event

import com.degree.gami.model.EventDao
import com.degree.gami.persistence.agerange.AgeRangeEntity
import com.degree.gami.persistence.agerange.AgeRangeRepository
import com.degree.gami.persistence.category.CategoryEntity
import com.degree.gami.persistence.category.CategoryRepository
import com.degree.gami.persistence.location.LocationEntity
import com.degree.gami.persistence.location.LocationRepository
import com.degree.gami.persistence.timerange.TimeRangeEntity
import com.degree.gami.persistence.timerange.TimeRangeRepository
import org.springframework.stereotype.Service


@Service
class EventValidation(private val locationRepository: LocationRepository,
                      private val categoryRepository: CategoryRepository,
                      private val ageRangeRepository: AgeRangeRepository,
                      private val timeRangeRepository: TimeRangeRepository) {

    fun checkValidLocation(eventDao: EventDao) : LocationEntity? {
        val location = LocationEntity(eventDao.longitude, eventDao.latitude)

        if (locationRepository.findByLongitudeAndLatitude(eventDao.longitude, eventDao.latitude) == null) {
            locationRepository.save(location)
        }

        return locationRepository
                .findAll()
                .firstOrNull{it.longitude == eventDao.longitude && it.latitude == eventDao.latitude}
    }

    fun checkValidAgeRange(eventDao: EventDao) : AgeRangeEntity? {
        val ageRange = AgeRangeEntity(eventDao.ageMin, eventDao.ageMax)

        if (ageRangeRepository.findByAgeMinAndAgeMax(eventDao.ageMin, eventDao.ageMax) == null){
            ageRangeRepository.save(ageRange)
        }

        return ageRangeRepository
                .findAll()
                .firstOrNull{it.ageMax == eventDao.ageMax && it.ageMin == eventDao.ageMin}
    }

    fun checkValidTimeRange(eventDao: EventDao) : TimeRangeEntity? {
        val timeRange = TimeRangeEntity(eventDao.startTime, eventDao.endTime)

        if (timeRangeRepository.findByStartTimeAndEndTime(eventDao.startTime, eventDao.endTime) == null){
            timeRangeRepository.save(timeRange)
        }

        return timeRangeRepository
                .findAll()
                .firstOrNull{it.startTime == eventDao.startTime && it.endTime == eventDao.endTime}
    }

    fun checkValidCategory(eventDao: EventDao) : CategoryEntity = categoryRepository
            .findByName(eventDao.categoryName)

    fun checkValidRating(rating: Double) : Boolean = rating in 1.0..5.0
}