package com.degree.gami.webapp.api

import com.degree.gami.model.CategoryDao
import com.degree.gami.service.category.CategoryService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid


@RestController
@RequestMapping(value = ["/api/category"])
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAll(): List<CategoryDao> {
        return categoryService.getAll()
    }
}