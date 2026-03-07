package org.example.warehouseservice.service;

import org.example.warehouseservice.dto.requestDTO.CategoryRequestDto;
import org.example.warehouseservice.dto.requestDTO.CategoryUpdateRequestDto;
import org.example.warehouseservice.dto.responseDTO.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
   CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
   CategoryResponseDto updateCategory(Long categoryId, CategoryUpdateRequestDto req);
   CategoryResponseDto deleteCategory(Long categoryId);
   List<CategoryResponseDto> getAllCategories();
   CategoryResponseDto getCategoryById(Long categoryId);
   List<CategoryResponseDto> getCategoriesByWarehouse(Long warehouseId);
   CategoryResponseDto findByName(String name);
}
