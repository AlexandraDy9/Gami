package com.degree.gami.persistence.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRepository: JpaRepository<CategoryEntity, Long> {

    fun findByName(name: String) : CategoryEntity
}