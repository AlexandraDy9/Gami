package com.degree.gami.service.event

import com.degree.gami.model.*
import com.degree.gami.persistence.agerange.AgeRangeRepository
import com.degree.gami.persistence.event.EventRepository
import com.degree.gami.persistence.userentities.userevents.UserEventEntity
import com.degree.gami.persistence.userentities.userevents.UserEventsRepository
import com.degree.gami.service.principal.PrincipalService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.Valid

@Service
class EventService(private val eventRepository: EventRepository,
                   private val userEventsRepository: UserEventsRepository,
                   private val eventConverter: EventConverter,
                   private val eventValidation: EventValidation,
                   private val principalService: PrincipalService,
                   private val ageRangeRepository: AgeRangeRepository) {

    @Transactional
    fun add(eventToAdd: EventDao){
        val event = eventConverter.convertFromDao(eventToAdd)
        eventRepository.save(event)
        val userEvent = UserEventEntity(
                rating = 0.0,
                review = null,
                isBookmark = false,
                isHost = true,
                isJoin = false,
                event = eventRepository.findByName(eventToAdd.name),
                user = principalService.getPrincipal()
        )
        userEventsRepository.save(userEvent)
    }

    fun getById(id: Long): EventDao{
        if(!eventRepository.findById(id).isPresent) {
            throw RuntimeException("Event with id $id not found")
        }

        val event = eventRepository.findById(id).get()
        return eventConverter.convertToDao(event)
    }

    fun getByName(name: String): EventDao{
        if(eventRepository.findByName(name) == null) {
            throw RuntimeException("Event with id $name not found")
        }

        val event = eventRepository.findByName(name)
        return eventConverter.convertToDao(event!!)
    }

    fun getEventsOfCategory(name: String) : List<EventDao> {
        val eventList = eventRepository.findAll()
        val eventsOfCategory = mutableListOf<EventDao>()
        eventList.forEach{
            if(it.category?.name == name){
                eventsOfCategory.add(eventConverter.convertToDao(it))
            }
        }
        return eventsOfCategory
    }

    fun getAll() : List<EventDao>{
        val events = eventRepository.findAll()
        val listEvents = mutableListOf<EventDao>()

        events.forEach {listEvents.add(eventConverter.convertToDao(it)) }

        return listEvents
    }

    @Transactional
    fun update(name: String, newObj: EventDao) {
        if(eventRepository.findByName(name) == null) {
            throw RuntimeException("Event with id $name not found")
        }

        val currentEvent = eventRepository.findByName(name)

        val newCategory = eventValidation.checkValidCategory(newObj)
        val newLocation = eventValidation.checkValidLocation(newObj)
        val newAgeRange = eventValidation.checkValidAgeRange(newObj)
        val newTimeRange = eventValidation.checkValidTimeRange(newObj)

        currentEvent?.name = newObj.name
        currentEvent?.description = newObj.description
        currentEvent?.numberOfAttendees = newObj.numberOfAttendees
        currentEvent?.category = newCategory
        currentEvent?.location = newLocation
        currentEvent?.ageRange = newAgeRange
        currentEvent?.timeRange = newTimeRange
        eventRepository.save(currentEvent!!)
    }

    @Transactional
    fun delete(name: String) {
        if(eventRepository.findByName(name) == null) {
            throw RuntimeException("Event with id $name not found")
        }

        val event = eventRepository.findByName(name)

        eventRepository.delete(event!!)
        userEventsRepository.deleteByEvent(event)
    }

    @Transactional
    fun updateReview(eventName: String, review: ReviewDto) {
        var userEvent = userEventsRepository
                .findAll()
                .firstOrNull {it.user?.id == principalService.getPrincipal().id && it.event?.name == eventName}

        if (userEvent == null) {
            userEvent = UserEventEntity(
                    rating = review.rating?.toDouble(),
                    review = review.review,
                    isBookmark = false,
                    isHost = false,
                    isJoin = false,
                    event = eventRepository.findByName(eventName),
                    user = principalService.getPrincipal()
            )
        } else {
            userEvent.review = review.review
            userEvent.rating = review.rating?.toDouble()
        }

        userEventsRepository.save(userEvent)
    }

    fun getAverageRating(name: String): Double {
        val userEvents = userEventsRepository.findAllByEventName(name)
        var sum = 0.0
        userEvents.forEach {
            sum += it.rating!!
        }
        return sum / userEvents.size
    }


    fun getHostDetails(name: String) : HostDao {
        var host: HostDao? = null
        val userEvents = userEventsRepository.findAll()
        userEvents.forEach {
            if(it.event?.name == name && it.isHost) {
                host = HostDao(it.user?.username!!, it.user?.photos?.firstOrNull()?.image)
            }
        }

        return host!!
    }

    fun getNumberOfJoinedUsers(eventName: String): Int {
        var counter = 0

        val userEvents = userEventsRepository.findAll()
        userEvents.forEach {
            if(it.event?.name == eventName && it.isJoin) {
                counter ++
            }
        }

        return counter
    }

    fun getReviews(@Valid name: String): List<ReviewDao> {
        val userEvents = userEventsRepository.findAllByEventName(name)
        val listReviews = mutableListOf<ReviewDao>()
        userEvents.forEach {
            if (!it.isHost && (it.rating != 0.0 || it.review != null))
                listReviews.add(ReviewDao(
                        it.user?.username!!,
                        it.user?.photos?.firstOrNull()?.image,
                        it.rating,
                        it.review))
            }
        return listReviews
    }

    fun getAgeRanges(): List<AgeRangeDao> {
        val ageRanges = ageRangeRepository.findAll()
        val listAgeRanges = mutableListOf<AgeRangeDao>()

        ageRanges.forEach { it -> listAgeRanges.add(eventConverter.convertToAgeRangeDao(it)) }
        return listAgeRanges
    }
}