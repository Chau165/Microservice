package org.example.warehouseservice.service.impl;

import org.example.warehouseservice.dto.requestDTO.ItemRequestDto;
import org.example.warehouseservice.dto.responseDTO.ItemResponseDto;
import org.example.warehouseservice.entity.Item;
import org.example.warehouseservice.entity.ItemImage;
import org.example.warehouseservice.exception.AppException;
import org.example.warehouseservice.exception.ErrorCode;
import org.example.warehouseservice.mapper.ItemMapper;
import org.example.warehouseservice.repository.CategoryRepository;
import org.example.warehouseservice.repository.ItemImageRepository;
import org.example.warehouseservice.repository.ItemRepository;
import org.example.warehouseservice.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemImageRepository itemImageRepository;


    @Override
    public ItemResponseDto save(ItemRequestDto itemRequestDto) {
        Item item = ItemMapper.toItem(itemRequestDto);
        item.setCreatedDate(LocalDateTime.now());
        item.setUpdatedDate(LocalDateTime.now());
        item.setCategory(categoryRepository.getReferenceById(itemRequestDto.categoryId()));
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto update(Long id, ItemRequestDto itemRequestDto) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setName(itemRequestDto.name());
            item.setDescription(itemRequestDto.description());
            item.setQuantity(itemRequestDto.quantity());
            item.setReorderLevel(itemRequestDto.reorderLevel());
            item.setPrice(itemRequestDto.price());
            item.setSupplierName(itemRequestDto.supplierName());
            item.setStatus(itemRequestDto.status());
            item.setUpdatedDate(LocalDateTime.now());
            item.setCategory(categoryRepository.getReferenceById(itemRequestDto.categoryId()));
            return ItemMapper.toItemResponseDto(itemRepository.save(item));
        } else {
            throw new RuntimeException("Item not found with id: " + id);
        }
    }

    @Override
    public Item isExistedByName(String name,Long warehouseId) {
        return itemRepository.isExistedByName(name,warehouseId);
    }

    @Override
    public List<ItemResponseDto> getAll(Long warehouseId) {
        return itemRepository.findAll(warehouseId).stream().map(item ->{
            ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);
            List<String> imageUrls = itemImageRepository.findItemImageByItemId(item.getItemId()).stream()
                    .map(ItemImage::getImageUrl)
                    .toList();
            itemResponseDto.imageUrls().addAll(imageUrls);
//            String imageUrls;
//            List<ItemImage> itemImages = itemImageRepository.findItemImageByItemId(item.getItemId());
//            if (itemImages != null && !itemImages.isEmpty()) {
//                imageUrls = itemImages.get(0).getImageUrl();
//            } else {
//                imageUrls = null; // Hoặc giá trị mặc định nếu không có hình ảnh
//            }
//            itemResponseDto.imageUrls().add(imageUrls);
            return itemResponseDto;
        }).toList();
    }

    @Override
    public ItemResponseDto getById(Long id) {
        return ItemMapper.toItemResponseDto(itemRepository.getReferenceById(id));
    }

    @Override
    public List<ItemResponseDto> getAllByName(String name,Long warehouseId) {

        return itemRepository.findItemByName(name,warehouseId).stream().map(ItemMapper::toItemResponseDto).toList();
    }

    @Override
    public List<ItemResponseDto> getAllByPrice(double minPrice, double maxPrice,Long warehouseId) {
        return itemRepository.findItemByPrice(minPrice,maxPrice,warehouseId ).stream().map(ItemMapper::toItemResponseDto).toList();
    }

    @Override
    public List<ItemResponseDto> getAllBySupplierName(String supplierName,Long warehouseId) {
        return itemRepository.findItemBySupplierName(supplierName,warehouseId).stream().map(ItemMapper::toItemResponseDto).toList();
    }

    @Override
    public List<ItemResponseDto> getAllByCategory(String categoryName,Long warehouseId) {
        return itemRepository.findItemByCatergory(categoryName,warehouseId).stream().map(ItemMapper::toItemResponseDto).toList();
    }

    @Override
    public List<ItemResponseDto> getAllByLocation(String location) {
        return itemRepository.findItemByLocation(location).stream().map(ItemMapper::toItemResponseDto).toList();
    }

    @Override
    public ItemResponseDto deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if(item.getStatus() == "INACTIVE") {
            throw new AppException(ErrorCode.ITEM_DELETED);
        }
        if(item.getQuantity() > 0 ){
            throw new AppException(ErrorCode.ITEM_HAS_STOCK);
        }

        item.setStatus("INACTIVE");
        Item i = itemRepository.save(item);
        return ItemMapper.toItemResponseDto(i);
    }
}
