package com.degree.gami.service.category

import com.degree.gami.model.CategoryDao
import com.degree.gami.persistence.category.CategoryEntity
import com.degree.gami.persistence.category.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.servlet.http.HttpServletRequest


@Service
class CategoryService(private val categoryRepository: CategoryRepository,
                      private val categoryConverter: CategoryConverter) {

    @Transactional
    fun add(categoryToAdd: CategoryDao) {
        val category: CategoryEntity = categoryConverter.convertFromDao(categoryToAdd)
        categoryRepository.save(category)
    }

    @Transactional
    fun delete(name: String) {
        val category: CategoryEntity = categoryRepository.findByName(name)
        categoryRepository.delete(category)
    }

    @Transactional
    fun update(name: String, newCategory: CategoryDao) {
        val currentCategory: CategoryEntity = categoryRepository.findByName(name)
        currentCategory.image = newCategory.image
        currentCategory.isIndoor = newCategory.isIndoor
        currentCategory.name = newCategory.name
        categoryRepository.save(currentCategory)
    }

    fun getAllByIsIndoor(isIndoor: Boolean): List<CategoryDao> {
        val listCategories: List<CategoryEntity> = if (isIndoor) {
            categoryRepository.findAllByIndoorIsTrue()
        } else {
            categoryRepository.findAllByIndoorIsFalse()
        }
        return categoryConverter.convertListToDaoList(listCategories)
    }

    fun getAll(): List<CategoryDao> {
        val listCategories: List<CategoryEntity> = categoryRepository.findAll()
        return categoryConverter.convertListToDaoList(listCategories)
    }

    @Autowired
    val request: HttpServletRequest? = null

    @Transactional
    fun uploadImages(file: MultipartFile) {
        if (file.isEmpty()) {
            throw RuntimeException("File is empty.")
        } else {
            try {
                val uploadsDir = "/uploads/"
                val realPathtoUploads = request?.session?.servletContext?.getRealPath(uploadsDir)!!
                if (!File(realPathtoUploads).exists()) {
                    File(realPathtoUploads).mkdir()
                }
                val orgName = file.originalFilename
                val filePath = realPathtoUploads + orgName
                val dest = File(filePath)
                file.transferTo(dest)
                if (orgName!!.contains("category")) {
                    if (getAll().size < 8) {
                        insertCategories(orgName, dest)
                    }
                } 
            } catch (e: Exception) {
                val logger = LoggerFactory.getLogger(javaClass)
                logger.error("File not found.", e)
            }
        }
    }

    fun insertCategories(fileName: String, destination: File) {
        val images = mapOf('0' to Pair("cooking", true), '1' to Pair("dancing", true),
                '2' to Pair("music", true), '3' to Pair("painting", true), '4' to Pair("pottery", true),
                '5' to Pair("swimming", true), '6' to Pair("winter sports", false), '7' to Pair("yoga", true))
        val key = fileName[fileName.length - 1]
        val categoryToAdd = CategoryDao(images[key]?.first, images[key]!!.second, destination.toString())
        val category: CategoryEntity = categoryConverter.convertFromDao(categoryToAdd)
        categoryRepository.save(category)
    }

}