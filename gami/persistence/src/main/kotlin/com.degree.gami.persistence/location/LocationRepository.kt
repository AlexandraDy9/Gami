package com.degree.gami.persistence.location

import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository: JpaRepository<LocationEntity, Long>{

    fun findByLongitudeAndLatitude(longitude: Double, latitude: Double): LocationEntity?

}