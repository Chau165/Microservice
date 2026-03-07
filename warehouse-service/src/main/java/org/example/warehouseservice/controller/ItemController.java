package org.example.warehouseservice.controller;

import jakarta.validation.Valid;
import org.example.warehouseservice.dto.ApiResponse;
import org.example.warehouseservice.dto.requestDTO.ItemRequestDto;
import org.example.warehouseservice.dto.responseDTO.ItemResponseDto;
import org.example.warehouseservice.entity.Item;
import org.example.warehouseservice.service.CategoryService;
import org.example.warehouseservice.service.ItemImageService;
import org.example.warehouseservice.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse-service/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemImageService itemImageService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ItemResponseDto>> addItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
//        Map<String, List<String>> errors = ItemValidation.validateItem(itemRequestDto);
//        if (!errors.isEmpty()) {
//            ApiResponse<ItemResponseDto> response = new ApiResponse<>();
//            response.setErrors(errors);
//            response.setStatus("error");
//            return ResponseEntity.badRequest().body(response);
//        }
        Long warehouseId = categoryService.getCategoryById(itemRequestDto.categoryId()).warehouseId();
        if (itemService.isExistedByName(itemRequestDto.name(),warehouseId) != null) {
            ApiResponse<ItemResponseDto> response = new ApiResponse<>();
            response.setStatus("error");
            Map<String, List<String>> errors = Map.of("name", List.of("Item name already exists"));
            response.setErrors(errors);
            return ResponseEntity.badRequest().body(response);
        }
        ItemResponseDto itemResponseDto = itemService.save(itemRequestDto);
        ApiResponse<ItemResponseDto> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(@RequestParam Long id,@Valid @RequestBody ItemRequestDto itemRequestDto) {
//        Map<String, List<String>> errors = ItemValidation.validateItem(itemRequestDto);
//        if (!errors.isEmpty()) {
//            ApiResponse<ItemResponseDto> response = new ApiResponse<>();
//            response.setErrors(errors);
//            response.setStatus("error");
//            return ResponseEntity.badRequest().body(response);
//        }
        Long warehouseId = categoryService.getCategoryById(itemRequestDto.categoryId()).warehouseId();
        Item existingItem = itemService.isExistedByName(itemRequestDto.name(),warehouseId);
        if (existingItem != null && !existingItem.getItemId().equals(id)) {
            ApiResponse<ItemResponseDto> response = new ApiResponse<>();
            response.setStatus("error");
            Map<String, List<String>> errors = Map.of("name", List.of("Item name already exists"));
            response.setErrors(errors);
            return ResponseEntity.badRequest().body(response);
        }
        ItemResponseDto itemResponseDto = itemService.update(id,itemRequestDto);
        ApiResponse<ItemResponseDto> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-name/{name}")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> searchByName(@PathVariable String name,@RequestParam Long warehouseId) {
        List<ItemResponseDto> itemResponseDto = itemService.getAllByName(name,warehouseId);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-category/{category}")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> searchByCategory(@PathVariable String category,@RequestParam Long warehouseId) {
        List<ItemResponseDto> itemResponseDto = itemService.getAllByCategory(category,warehouseId);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-supplierName/{supplierName}")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> searchBySupplierName(@PathVariable String supplierName,@RequestParam Long warehouseId) {
        List<ItemResponseDto> itemResponseDto = itemService.getAllBySupplierName(supplierName,warehouseId);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-location/{location}")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> searchByLocation(@PathVariable String location) {
        List<ItemResponseDto> itemResponseDto = itemService.getAllByLocation(location);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-price")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> searchByPrice(@RequestParam double minPrice, @RequestParam double maxPrice,@RequestParam Long warehouseId) {
        List<ItemResponseDto> itemResponseDto = itemService.getAllByPrice(minPrice, maxPrice,warehouseId);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getAllItems(@RequestParam Long warehouseId) {
        List<ItemResponseDto> itemResponseDto = itemService.getAll(warehouseId);
        ApiResponse<List<ItemResponseDto>> response = new ApiResponse<>();
        response.setData(itemResponseDto);
        response.setStatus("success");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/images")
    public ApiResponse<List<String>> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {

        List<String> urls = itemImageService.uploadItemImages(id, files);
        return ApiResponse.<List<String>> builder()
                .status("Images uploaded successfully")
                .data(urls)
                .build();
    }

    @DeleteMapping("/images/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable Long imageId) {
        itemImageService.deleteItemImage(imageId);
        return ApiResponse.<Void> builder()
                .status("Image deleted successfully")
                .data(null)
                .build();
    }

    @DeleteMapping("/{itemId}")
    public ApiResponse<ItemResponseDto> deleteItem(@PathVariable Long itemId) {
        return ApiResponse.<ItemResponseDto> builder()
                .status("Item deleted successfully")
                .data(itemService.deleteItem(itemId))
                .build();
    }
}
