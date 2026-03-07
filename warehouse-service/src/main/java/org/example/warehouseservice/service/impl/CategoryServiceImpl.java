package org.example.warehouseservice.service.impl;

import org.example.warehouseservice.dto.requestDTO.CategoryRequestDto;
import org.example.warehouseservice.dto.requestDTO.CategoryUpdateRequestDto;
import org.example.warehouseservice.dto.responseDTO.CategoryResponseDto;
import org.example.warehouseservice.entity.Category;
import org.example.warehouseservice.entity.Warehouse;
import org.example.warehouseservice.enums.CategoryStatus;
import org.example.warehouseservice.exception.AppException;
import org.example.warehouseservice.exception.ErrorCode;
import org.example.warehouseservice.mapper.CategoryMapper;
import org.example.warehouseservice.repository.CategoryRepository;
import org.example.warehouseservice.repository.ItemRepository;
import org.example.warehouseservice.repository.WarehouseRepository;
import org.example.warehouseservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_ALREADY);
        }
        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElse(null);
        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .displayOrder(dto.displayOrder())
                .status(CategoryStatus.ACTIVE)
                .warehouse(warehouse)
                .build();
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponseDto(saved);
    }

    @Override
    public CategoryResponseDto updateCategory(Long categoryId, CategoryUpdateRequestDto req){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (categoryRepository.existsByName(req.name()) && category.getCategoryId() != categoryId) {
            throw new AppException(ErrorCode.CATEGORY_NAME_ALREADY);
        }
        category.setName(req.name());
        category.setDescription(req.description());
        category.setDisplayOrder(req.displayOrder());
        if(req.status() == CategoryStatus.ACTIVE ||  req.status() == CategoryStatus.INACTIVE) {
            category.setStatus(req.status());
        }
        Category updated = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponseDto(updated);
    }

    @Override
    public CategoryResponseDto deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (itemRepository.existsByCategory_CategoryId(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_HAS_ITEMS);
        }
        category.setStatus(CategoryStatus.INACTIVE);
        Category updated = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponseDto(updated);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories =
                categoryRepository.findAllByStatusOrderByDisplayOrderAsc(CategoryStatus.ACTIVE);
        return categories.stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (category.getStatus() == CategoryStatus.INACTIVE) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByWarehouse(Long warehouseId) {
        List<Category> categories =
                categoryRepository.findByWarehouse_WarehouseIdOrWarehouseIsNull(warehouseId);
        return categories.stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto findByName(String name) {

        Category category = categoryRepository.findByName(name)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getStatus() == CategoryStatus.INACTIVE) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return CategoryMapper.toCategoryResponseDto(category);
    }

}
