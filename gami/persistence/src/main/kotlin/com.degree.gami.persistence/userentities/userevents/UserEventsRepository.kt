package com.degree.gami.persistence.userentities.userevents

import com.degree.gami.persistence.event.EventEntity
import com.degree.gami.persistence.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserEventsRepository: JpaRepository<UserEventEntity, Long>{

    fun deleteByUser(userEntity: UserEntity?)

    fun deleteByEvent(eventEntity: EventEntity?)

    fun findAllByEventName(name: String): List<UserEventEntity>

    fun findByUserIdAndEventId(userId: Long, eventId: Long): UserEventEntity?
}