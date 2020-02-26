package com.degree.gami.persistence.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRepository: JpaRepository<CategoryEntity, Long> {

    fun findByName(name: String) : CategoryEntity

    @Query( nativeQuery = true, value = "select * from category where is_indoor = 0")
    fun findAllByIndoorIsFalse() : List<CategoryEntity>

    @Query(nativeQuery = true, value = "select * from category where is_indoor = 1")
    fun findAllByIndoorIsTrue() : List<CategoryEntity>
}