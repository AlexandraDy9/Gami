package com.degree.gami.service.user

import com.degree.gami.model.CategoryDao
import com.degree.gami.model.EventDao
import com.degree.gami.model.UserDao
import com.degree.gami.persistence.category.CategoryEntity
import com.degree.gami.persistence.event.EventEntity
import com.degree.gami.persistence.user.UserEntity
import org.springframework.stereotype.Service


@Service
class UserConverter {

    fun convertCategoryToDao(category: CategoryEntity?) : CategoryDao =
            CategoryDao(category!!.name, category.isIndoor, category.image)

    fun convertEventToDao(event: EventEntity?) : EventDao {
        return EventDao(event?.name.toString(),
                event?.description.toString(),
                event?.numberOfAttendees!!.toInt(),
                event.category?.name.toString(),
                event.location?.longitude!!.toDouble(),
                event.location?.latitude!!.toDouble(),
                event.ageRange?.ageMin!!,
                event.ageRange?.ageMax!!,
                event.timeRange?.startTime!!,
                event.timeRange?.endTime!!)
    }

    fun convertToDao(user: UserEntity?): UserDao? = UserDao(user?.user.toString(), user?.pass.toString(), user?.firstname.toString(),
            user?.lastname.toString(),user?.eMail, user?.description.toString(), user?.birthDate)

    fun convertFromDao(user: UserDao) : UserEntity = UserEntity(user.user,user.pass, user.firstname,
            user.lastname, user.eMail, user.description, user.birthdate)
}