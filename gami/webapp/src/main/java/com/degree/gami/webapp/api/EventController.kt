package com.degree.gami.webapp.api

import com.degree.gami.model.*
import com.degree.gami.service.event.EventService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping(value = ["/api/event"])
class EventController(private val eventService: EventService) {

    @PostMapping
    fun add(@Valid @RequestBody event: EventDao){
        eventService.add(event)
    }

    @GetMapping(value = ["/byId/{id}"])
    fun getById(@Valid @PathVariable id: Long): EventDao{
        return eventService.getById(id)
    }

    @GetMapping(value = ["/byName/{name}"])
    fun getByName(@Valid @PathVariable name: String): EventDao{
        return eventService.getByName(name)
    }

    @GetMapping
    fun getAll() : List<EventDao> {
        return eventService.getAll()
    }

    @GetMapping(value = ["/category/{name}"])
    fun getEventsOfCategory(@Valid @PathVariable name: String) : List<EventDao> {
        return eventService.getEventsOfCategory(name)
    }

    @PutMapping(value = ["{name}"])
    fun update(@Valid @PathVariable name: String, @Valid @RequestBody eventDao: EventDao) {
        eventService.update(name, eventDao)
    }

    @DeleteMapping(value = ["{name}"])
    fun delete(@Valid @PathVariable name: String) {
        eventService.delete(name)
    }

    @PatchMapping(value = ["{name}/review"])
    fun updateReview(@Valid @PathVariable name: String, @Valid @RequestBody review: ReviewDto) {
        eventService.updateReview(name, review)
    }

    @GetMapping(value = ["{name}/average"])
    fun getAverageRating(@Valid @PathVariable name: String) : Double {
        return eventService.getAverageRating(name)
    }

    @GetMapping(value = ["{name}/host"])
    fun getHostDetails(@Valid @PathVariable name: String) : HostDao {
        return eventService.getHostDetails(name)
    }

    @GetMapping(value = ["{name}/joinedUsers"])
    fun getNumberOfJoinedUsers(@Valid @PathVariable name: String) : Int {
        return eventService.getNumberOfJoinedUsers(name)
    }

    @GetMapping(value = ["{name}/reviews"])
    fun getReviews(@Valid @PathVariable name: String): List<ReviewDao> {
        return eventService.getReviews(name)
    }

    @GetMapping(value = ["/ageRanges"])
    fun getAgeRanges(): List<AgeRangeDao> {
        return eventService.getAgeRanges()
    }
}