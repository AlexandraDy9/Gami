package com.degree.gami.persistence.event

import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<EventEntity, Long> {
    fun findByName(name: String) : EventEntity?
}