package com.degree.gami.service.user

import com.degree.gami.model.*
import com.degree.gami.persistence.category.CategoryRepository
import com.degree.gami.persistence.event.EventRepository
import com.degree.gami.persistence.user.UserRepository
import com.degree.gami.persistence.userentities.usercategory.UserCategoryEntity
import com.degree.gami.persistence.userentities.usercategory.UserCategoryRepository
import com.degree.gami.persistence.userentities.userevents.UserEventEntity
import com.degree.gami.persistence.userentities.userevents.UserEventsRepository
import com.degree.gami.persistence.userentities.userphoto.UserPhotoEntity
import com.degree.gami.persistence.userentities.userphoto.UserPhotoRepository
import com.degree.gami.service.principal.PrincipalService
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserService(private val userRepository: UserRepository,
                  private val userCategoryRepository: UserCategoryRepository,
                  private val userPhotoRepository: UserPhotoRepository,
                  private val userEventsRepository: UserEventsRepository,
                  private val userConverter: UserConverter,
                  private val eventRepository: EventRepository,
                  private val categoryRepository: CategoryRepository,
                  private val passwordEncoder: PasswordEncoder,
                  private val principalService: PrincipalService,
                  private val mailSender: JavaMailSender) {

    @Transactional
    fun register(user: UserDao) {
        val encodedPassword = passwordEncoder.encode(
                user.pass
        )
        user.pass = encodedPassword
        userRepository.save(userConverter.convertFromDao(user))
    }

    fun getEvents(): List<EventDao> {
        val eventsOfUser = mutableListOf<EventDao>()

        val userEvents = principalService.getPrincipal().userEvents
        userEvents?.forEach {
            if (it.isJoin) {
                eventsOfUser.add(userConverter.convertEventToDao(it.event))
            }
        }

        return eventsOfUser
    }

    fun getCategories(): List<CategoryDao> {
        val categoriesForUser = mutableListOf<CategoryDao>()

        val userCategories = principalService.getPrincipal().userCategories
        userCategories?.forEach { categoriesForUser.add(userConverter.convertCategoryToDao(it.category)) }

        return categoriesForUser
    }

    @Transactional
    fun delete() {
        val user = principalService.getPrincipal()

        userRepository.delete(user)
        userCategoryRepository.deleteByUser(user)
        userPhotoRepository.deleteByUser(user)
        userEventsRepository.deleteByUser(user)
    }

    @Transactional
    fun update(newUser: UserDao) : UserDao {
        val oldUser = principalService.getPrincipal()
        oldUser.birthDate = newUser.birthdate
        oldUser.description = newUser.description
        oldUser.firstname = newUser.firstname
        oldUser.lastname = newUser.lastname
        userRepository.save(oldUser)

        return userConverter.convertToDao((oldUser))!!
    }

    fun getEventsByBookmark(): List<EventDao> {
        val eventsForUser = mutableListOf<EventDao>()
        val userEvents = principalService.getPrincipal().userEvents

        userEvents!!.forEach {
            when (it.isBookmark) {
                true -> eventsForUser.add(userConverter.convertEventToDao(it.event))
            }
        }

        return eventsForUser
    }

    @Transactional
    fun bookmarkEvent(eventName: String) {
        var userEvent = userEventsRepository
                .findByUserIdAndEventId(principalService.getPrincipal().id!!, eventRepository.findByName(eventName)?.id!!)
        if (userEvent?.user == null) {
            userEvent = UserEventEntity(rating = 0.0, review = null, isBookmark = true, isHost = false, isJoin = false,
                    event = eventRepository.findByName(eventName),
                    user = principalService.getPrincipal())
        } else {
            userEvent.isBookmark = true
        }
        userEventsRepository.save(userEvent)
    }

    @Transactional
    fun removeBookmarkEvent(eventName: String) {
        val userEvent = userEventsRepository
                .findByUserIdAndEventId(principalService.getPrincipal().id!!, eventRepository.findByName(eventName)?.id!!)
        userEvent?.isBookmark = false
        userEventsRepository.save(userEvent!!)
    }

    @Transactional
    fun joinEvent(eventName: String) : Boolean {
        var isJoin = false

        var userEvent = userEventsRepository
                .findByUserIdAndEventId(principalService.getPrincipal().id!!, eventRepository.findByName(eventName)?.id!!)
        if (userEvent?.user == null) {
            userEvent = UserEventEntity(rating = 0.0, review = null, isBookmark = false, isHost = false, isJoin = true,
                    event = eventRepository.findByName(eventName),
                    user = principalService.getPrincipal())
            isJoin = true
        } else {
            if (!userEvent.isHost && !userEvent.isJoin) {
                userEvent.isJoin = true
                isJoin = true
            }
        }
        userEventsRepository.save(userEvent)

        return isJoin
    }

    @Transactional
    fun leftEvent(eventName: String) {
        val userEvent = userEventsRepository
                .findByUserIdAndEventId(principalService.getPrincipal().id!!, eventRepository.findByName(eventName)?.id!!)
        userEvent?.isJoin = false
        userEventsRepository.save(userEvent!!)
    }

    @Transactional
    fun sendMail(sendMail: SendMailDao) {
        val email = Email(sendMail.email)
        val mailMessage = SimpleMailMessage()
        mailMessage.setSubject(email.subject)
        mailMessage.setFrom(email.from)
        mailMessage.setTo(email.to)
        mailMessage.setText(email.messageText)
        mailSender.send(mailMessage)
    }

    @Transactional
    fun changePassword(changePassword: ChangePasswordDao) {
        val user = userRepository.findByEMail(changePassword.email)
        val encodedPassword = passwordEncoder.encode(changePassword.pass)
        user?.pass = encodedPassword
        userRepository.save(user!!)
    }

    fun setCategoriesForUser(username: String, categories: List<CategoryDao>) {
        categories.forEach {
            userCategoryRepository.save(
                    UserCategoryEntity(categoryRepository.findByName(it.name!!), userRepository.findByUser(username))
            )
        }
    }

    fun setPhotos(username: String, photos: List<PhotoDao>) {
        val user = userRepository.findByUser(username)
        photos.forEach {
            userPhotoRepository.save(
                    UserPhotoEntity(it.image, user)
            )
        }
    }

    fun getPhotos(username: String): List<PhotoDao> {
        val photosForUser = mutableListOf<PhotoDao>()
        val userPhotos = userRepository.findByUser(username).photos
        userPhotos?.forEach { photosForUser.add(PhotoDao(it.image!!)) }

        return photosForUser
    }

    fun getUserByUsername(username: String): UserDao {
        val user = userRepository.findByUser(username)
        return userConverter.convertToDao(user)!!
    }

    fun getHostedEvents(username: String): List<EventDao> {
        val hostedEvents = mutableListOf<EventDao>()
        val userEvents = userEventsRepository.findAll()
        userEvents.forEach {
            if (it.user?.user == username && it.isHost) {
                hostedEvents.add(userConverter.convertEventToDao(it.event))
            }
        }
        return hostedEvents
    }
}