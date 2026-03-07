package org.example.warehouseservice.controller;

import jakarta.validation.Valid;
import org.example.warehouseservice.dto.ApiResponse;
import org.example.warehouseservice.dto.requestDTO.CategoryRequestDto;
import org.example.warehouseservice.dto.requestDTO.CategoryUpdateRequestDto;
import org.example.warehouseservice.dto.responseDTO.CategoryResponseDto;
import org.example.warehouseservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse-service/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

   @PostMapping
   public ApiResponse<CategoryResponseDto> createCategory(
           @Valid @RequestBody CategoryRequestDto req) {
       return ApiResponse.<CategoryResponseDto>builder()
               .data(categoryService.createCategory(req))
               .status("Category created successfully")
               .build();
   }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponseDto> updateCategory(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryUpdateRequestDto req) {

        return ApiResponse.<CategoryResponseDto>builder()
                .data(categoryService.updateCategory(categoryId, req))
                .status("Category updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CategoryResponseDto> deleteCategory(
            @PathVariable("id") Long categoryId) {
        return ApiResponse.<CategoryResponseDto>builder()
                .data(categoryService.deleteCategory(categoryId))
                .status("Category deleted successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponseDto>> getAllCategories() {
        return ApiResponse.<List<CategoryResponseDto>>builder()
                .data(categoryService.getAllCategories())
                .status("Get all categories successfully")
                .build();
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ApiResponse<List<CategoryResponseDto>> getAllCategories(@PathVariable Long warehouseId) {
        return ApiResponse.<List<CategoryResponseDto>>builder()
                .data(categoryService.getCategoriesByWarehouse(warehouseId))
                .status("Get categories successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponseDto> getCategoryById(
            @PathVariable("id") Long categoryId) {
        return ApiResponse.<CategoryResponseDto>builder()
                .data(categoryService.getCategoryById(categoryId))
                .status("Get category successfully")
                .build();
    }

    @GetMapping("/by-name/{name}")
    public ApiResponse<CategoryResponseDto> getCategoryByName(
            @PathVariable String name) {

        return ApiResponse.<CategoryResponseDto>builder()
                .data(categoryService.findByName(name))
                .status("Get category by name successfully")
                .build();
    }
}
