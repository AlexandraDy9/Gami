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

    fun getAll(): List<CategoryDao> {
        val listCategories: List<CategoryEntity> = categoryRepository.findAll()
        return categoryConverter.convertListToDaoList(listCategories)
    }
}