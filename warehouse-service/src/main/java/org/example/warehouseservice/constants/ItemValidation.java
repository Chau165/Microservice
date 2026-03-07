//package org.example.warehouseservice.constants;
//
//import org.example.warehouseservice.dto.requestDTO.ItemRequestDto;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ItemValidation {
//    public static Map<String,List<String>> validateItem(ItemRequestDto itemRequestDto) {
//
//        Map<String, List<String>> errors = new HashMap<>();
//
//        if (itemRequestDto.name() == null || itemRequestDto.name().isEmpty()) {
//            ArrayList<String> nameErrors = new ArrayList<>();
//            nameErrors.add("Name cannot be null or empty");
//            errors.put("name", nameErrors);
//        }
//
//        if (itemRequestDto.quantity() < 0) {
//            ArrayList<String> quantityErrors = new ArrayList<>();
//            quantityErrors.add("Quantity cannot be negative");
//            errors.put("quantity", quantityErrors);
//        }
//
//        if (itemRequestDto.reorderLevel() < 0) {
//            ArrayList<String> reorderLevelErrors = new ArrayList<>();
//            reorderLevelErrors.add("Reorder level cannot be negative");
//            errors.put("reorderLevel",reorderLevelErrors);
//        }
//
//        if (itemRequestDto.price() < 0) {
//            ArrayList<String> priceErrors = new ArrayList<>();
//            priceErrors.add("Price cannot be negative");
//            errors.put("price", priceErrors);
//        }
//
//        if (itemRequestDto.supplierName() == null || itemRequestDto.supplierName().isEmpty()) {
//            ArrayList<String> supplierNameErrors = new ArrayList<>();
//            supplierNameErrors.add("Supplier name cannot be null or empty");
//            errors.put("supplierName", supplierNameErrors);
//        }
//
//        if (itemRequestDto.categoryId() == null) {
//            ArrayList<String> categoryIdErrors = new ArrayList<>();
//            categoryIdErrors.add("Category ID cannot be null");
//            errors.put("categoryId", categoryIdErrors);
//        }
//        return errors;
//    }
//}
