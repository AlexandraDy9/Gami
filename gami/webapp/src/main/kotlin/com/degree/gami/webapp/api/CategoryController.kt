package com.degree.gami.webapp.api

import com.degree.gami.model.CategoryDao
import com.degree.gami.service.category.CategoryService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid


@RestController
@RequestMapping(value = ["/api/category"])
class CategoryController(private val categoryService: CategoryService) {

    @PostMapping
    fun add(@Valid @RequestBody category: CategoryDao) {
        categoryService.add(category)
    }

    @GetMapping
    fun getAll(): List<CategoryDao> {
        return categoryService.getAll()
    }
//
//    @GetMapping(value = ["{isIndoor}"])
//    fun getAllByIsIndoor(@Valid @PathVariable isIndoor: Boolean): List<CategoryDao> {
//        return categoryService.getAllByIsIndoor(isIndoor)
//    }

    @DeleteMapping(value = ["{name}"])
    fun delete(@Valid @PathVariable name: String) {
        categoryService.delete(name)
    }

    @PutMapping(value = ["{name}"])
    fun update(@Valid @PathVariable name: String, @Valid @RequestBody newCategory: CategoryDao) {
        categoryService.update(name, newCategory)
    }

    @PostMapping(value = ["/upload"])
    fun uploadImages(@RequestParam file: MultipartFile) {
        categoryService.uploadImages(file)
    }
}