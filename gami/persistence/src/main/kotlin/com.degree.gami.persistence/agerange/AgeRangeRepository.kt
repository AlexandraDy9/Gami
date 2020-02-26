package com.degree.gami.persistence.agerange

import org.springframework.data.jpa.repository.JpaRepository

interface AgeRangeRepository :JpaRepository<AgeRangeEntity, Long> {
    fun findByAgeMinAndAgeMax(ageMin: Int, ageMax: Int) : AgeRangeEntity?
}