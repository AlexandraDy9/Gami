package com.degree.gami.service.category

import com.degree.gami.model.CategoryDao
import com.degree.gami.persistence.category.CategoryEntity
import org.springframework.stereotype.Service

@Service
class CategoryConverter {

    fun convertFromDao(categoryDao: CategoryDao) : CategoryEntity =
            CategoryEntity(categoryDao.name,categoryDao.image)

    fun convertToDao(categoryEntity: CategoryEntity): CategoryDao =
            CategoryDao(categoryEntity.name, categoryEntity.image)

    fun convertListToDaoList(categoryEntities: List<CategoryEntity>): List<CategoryDao>{
        val categoryDao: MutableList<CategoryDao> = mutableListOf()
        categoryEntities.forEach {
            categoryDao.add(convertToDao(it))
        }
        return categoryDao
    }
}