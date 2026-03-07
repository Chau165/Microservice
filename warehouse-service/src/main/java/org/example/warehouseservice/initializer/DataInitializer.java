package org.example.warehouseservice.initializer;

import org.example.warehouseservice.entity.Category;
import org.example.warehouseservice.entity.Item;
import org.example.warehouseservice.entity.ItemImage;
import org.example.warehouseservice.entity.Location;
import org.example.warehouseservice.entity.Warehouse;
import org.example.warehouseservice.enums.CategoryStatus;
import org.example.warehouseservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.example.warehouseservice.entity.Request;
import org.example.warehouseservice.entity.RequestHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemImageRepository itemImageRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestHistoryRepository historyRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ khởi tạo dữ liệu khi database trống
        if (locationRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // Tạo 1 location
        Location location = new Location();
        location.setName("Ho Chi Minh City");
        location.setStatus("ACTIVE");
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        location = locationRepository.save(location);

        // Tạo 2 warehouses
        Warehouse commonWarehouse = new Warehouse();
        commonWarehouse.setName("common warehouse");
        commonWarehouse.setAddress("common warehouse");
        commonWarehouse.setStatus("ACTIVE");
        commonWarehouse.setCreatedAt(LocalDate.now());
        commonWarehouse.setUpdatedAt(LocalDate.now());
        commonWarehouse.setLocation(location);
        commonWarehouse = warehouseRepository.save(commonWarehouse);


        Warehouse warehouse1 = new Warehouse();
        warehouse1.setName("Central Warehouse");
        warehouse1.setAddress("123 Nguyen Van Linh, District 7");
        warehouse1.setStatus("ACTIVE");
        warehouse1.setCreatedAt(LocalDate.now());
        warehouse1.setUpdatedAt(LocalDate.now());
        warehouse1.setLocation(location);
        warehouse1 = warehouseRepository.save(warehouse1);

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setName("North Warehouse");
        warehouse2.setAddress("456 Le Van Viet, District 9");
        warehouse2.setStatus("ACTIVE");
        warehouse2.setCreatedAt(LocalDate.now());
        warehouse2.setUpdatedAt(LocalDate.now());
        warehouse2.setLocation(location);
        warehouse2 = warehouseRepository.save(warehouse2);

        // Tạo 4 categories (2 cho mỗi warehouse)
        Category category1 = new Category();
        category1.setName("Electronics");
        category1.setDescription("Electronic devices and components");
        category1.setDisplayOrder(1);
        category1.setStatus(CategoryStatus.ACTIVE);
        category1.setWarehouse(commonWarehouse);
        category1 = categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Furniture");
        category2.setDescription("Home and office furniture");
        category2.setDisplayOrder(2);
        category2.setWarehouse(commonWarehouse);
        category2.setStatus(CategoryStatus.ACTIVE);
        category2 = categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setName("Clothing");
        category3.setDescription("Fashion and apparel");
        category3.setDisplayOrder(1);
        category3.setStatus(CategoryStatus.ACTIVE);
        category3.setWarehouse(commonWarehouse);
        category3 = categoryRepository.save(category3);

        Category category4 = new Category();
        category4.setName("Sports Equipment");
        category4.setDescription("Sports and fitness equipment");
        category4.setDisplayOrder(2);
        category4.setStatus(CategoryStatus.ACTIVE);
        category4.setWarehouse(commonWarehouse);
        category4 = categoryRepository.save(category4);

        // Tạo 8 items (2 cho mỗi category)
        // Electronics category items
        Item item1 = new Item();
        item1.setName("Laptop Dell XPS 13");
        item1.setDescription("High-performance ultrabook");
        item1.setQuantity(10);
        item1.setReorderLevel(5);
        item1.setSupplierName("Dell Technologies");
        item1.setStatus("AVAILABLE");
        item1.setPrice(25000000.0); // 25 triệu VND
        item1.setCreatedDate(LocalDateTime.now());
        item1.setUpdatedDate(LocalDateTime.now());
        item1.setCategory(category1);
        item1 = itemRepository.save(item1);

        // Thêm ảnh cho item1
        ItemImage image1_1 = new ItemImage();
        image1_1.setImageUrl("https://example.com/images/dell-xps-13-front.jpg");
        image1_1.setItem(item1);
        itemImageRepository.save(image1_1);

        ItemImage image1_2 = new ItemImage();
        image1_2.setImageUrl("https://example.com/images/dell-xps-13-side.jpg");
        image1_2.setItem(item1);
        itemImageRepository.save(image1_2);

        Item item2 = new Item();
        item2.setName("iPhone 15 Pro");
        item2.setDescription("Latest Apple smartphone");
        item2.setQuantity(15);
        item2.setReorderLevel(8);
        item2.setSupplierName("Apple Inc");
        item2.setStatus("AVAILABLE");
        item2.setPrice(30000000.0); // 30 triệu VND
        item2.setCreatedDate(LocalDateTime.now());
        item2.setUpdatedDate(LocalDateTime.now());
        item2.setCategory(category1);
        item2 = itemRepository.save(item2);

        // Thêm ảnh cho item2
        ItemImage image2_1 = new ItemImage();
        image2_1.setImageUrl("https://example.com/images/iphone-15-pro-front.jpg");
        image2_1.setItem(item2);
        itemImageRepository.save(image2_1);

        ItemImage image2_2 = new ItemImage();
        image2_2.setImageUrl("https://example.com/images/iphone-15-pro-back.jpg");
        image2_2.setItem(item2);
        itemImageRepository.save(image2_2);

        ItemImage image2_3 = new ItemImage();
        image2_3.setImageUrl("https://example.com/images/iphone-15-pro-accessories.jpg");
        image2_3.setItem(item2);
        itemImageRepository.save(image2_3);

        // Furniture category items
        Item item3 = new Item();
        item3.setName("Office Chair");
        item3.setDescription("Ergonomic office chair with lumbar support");
        item3.setQuantity(25);
        item3.setReorderLevel(10);
        item3.setSupplierName("Herman Miller");
        item3.setStatus("AVAILABLE");
        item3.setPrice(5000000.0); // 5 triệu VND
        item3.setCreatedDate(LocalDateTime.now());
        item3.setUpdatedDate(LocalDateTime.now());
        item3.setCategory(category2);
        item3 = itemRepository.save(item3);

        // Thêm ảnh cho item3
        ItemImage image3_1 = new ItemImage();
        image3_1.setImageUrl("https://example.com/images/office-chair-front.jpg");
        image3_1.setItem(item3);
        itemImageRepository.save(image3_1);

        ItemImage image3_2 = new ItemImage();
        image3_2.setImageUrl("https://example.com/images/office-chair-side.jpg");
        image3_2.setItem(item3);
        itemImageRepository.save(image3_2);

        Item item4 = new Item();
        item4.setName("Executive Desk");
        item4.setDescription("Large wooden executive desk");
        item4.setQuantity(5);
        item4.setReorderLevel(2);
        item4.setSupplierName("IKEA");
        item4.setStatus("AVAILABLE");
        item4.setPrice(8000000.0); // 8 triệu VND
        item4.setCreatedDate(LocalDateTime.now());
        item4.setUpdatedDate(LocalDateTime.now());
        item4.setCategory(category2);
        item4 = itemRepository.save(item4);

        // Thêm ảnh cho item4
        ItemImage image4_1 = new ItemImage();
        image4_1.setImageUrl("https://example.com/images/executive-desk-main.jpg");
        image4_1.setItem(item4);
        itemImageRepository.save(image4_1);

        ItemImage image4_2 = new ItemImage();
        image4_2.setImageUrl("https://example.com/images/executive-desk-drawers.jpg");
        image4_2.setItem(item4);
        itemImageRepository.save(image4_2);

        ItemImage image4_3 = new ItemImage();
        image4_3.setImageUrl("https://example.com/images/executive-desk-detail.jpg");
        image4_3.setItem(item4);
        itemImageRepository.save(image4_3);

        // Clothing category items
        Item item5 = new Item();
        item5.setName("Men's Business Suit");
        item5.setDescription("Premium wool business suit");
        item5.setQuantity(20);
        item5.setReorderLevel(8);
        item5.setSupplierName("Hugo Boss");
        item5.setStatus("AVAILABLE");
        item5.setPrice(12000000.0); // 12 triệu VND
        item5.setCreatedDate(LocalDateTime.now());
        item5.setUpdatedDate(LocalDateTime.now());
        item5.setCategory(category3);
        item5 = itemRepository.save(item5);

        // Thêm ảnh cho item5
        ItemImage image5_1 = new ItemImage();
        image5_1.setImageUrl("https://example.com/images/business-suit-front.jpg");
        image5_1.setItem(item5);
        itemImageRepository.save(image5_1);

        ItemImage image5_2 = new ItemImage();
        image5_2.setImageUrl("https://example.com/images/business-suit-back.jpg");
        image5_2.setItem(item5);
        itemImageRepository.save(image5_2);

        Item item6 = new Item();
        item6.setName("Women's Evening Dress");
        item6.setDescription("Elegant evening dress for special occasions");
        item6.setQuantity(30);
        item6.setReorderLevel(12);
        item6.setSupplierName("Zara");
        item6.setStatus("AVAILABLE");
        item6.setPrice(3500000.0); // 3.5 triệu VND
        item6.setCreatedDate(LocalDateTime.now());
        item6.setUpdatedDate(LocalDateTime.now());
        item6.setCategory(category3);
        item6 = itemRepository.save(item6);

        // Thêm ảnh cho item6
        ItemImage image6_1 = new ItemImage();
        image6_1.setImageUrl("https://example.com/images/evening-dress-front.jpg");
        image6_1.setItem(item6);
        itemImageRepository.save(image6_1);

        ItemImage image6_2 = new ItemImage();
        image6_2.setImageUrl("https://example.com/images/evening-dress-back.jpg");
        image6_2.setItem(item6);
        itemImageRepository.save(image6_2);

        ItemImage image6_3 = new ItemImage();
        image6_3.setImageUrl("https://example.com/images/evening-dress-detail.jpg");
        image6_3.setItem(item6);
        itemImageRepository.save(image6_3);

        // Sports Equipment category items
        Item item7 = new Item();
        item7.setName("Professional Treadmill");
        item7.setDescription("Commercial grade treadmill for fitness");
        item7.setQuantity(8);
        item7.setReorderLevel(3);
        item7.setSupplierName("NordicTrack");
        item7.setStatus("AVAILABLE");
        item7.setPrice(45000000.0); // 45 triệu VND
        item7.setCreatedDate(LocalDateTime.now());
        item7.setUpdatedDate(LocalDateTime.now());
        item7.setCategory(category4);
        item7 = itemRepository.save(item7);

        // Thêm ảnh cho item7
        ItemImage image7_1 = new ItemImage();
        image7_1.setImageUrl("https://example.com/images/treadmill-main.jpg");
        image7_1.setItem(item7);
        itemImageRepository.save(image7_1);

        ItemImage image7_2 = new ItemImage();
        image7_2.setImageUrl("https://example.com/images/treadmill-console.jpg");
        image7_2.setItem(item7);
        itemImageRepository.save(image7_2);

        Item item8 = new Item();
        item8.setName("Tennis Racket Set");
        item8.setDescription("Professional tennis racket with strings");
        item8.setQuantity(50);
        item8.setReorderLevel(20);
        item8.setSupplierName("Wilson");
        item8.setStatus("AVAILABLE");
        item8.setPrice(2500000.0); // 2.5 triệu VND
        item8.setCreatedDate(LocalDateTime.now());
        item8.setUpdatedDate(LocalDateTime.now());
        item8.setCategory(category4);
        item8 = itemRepository.save(item8);

        // Thêm ảnh cho item8
        ItemImage image8_1 = new ItemImage();
        image8_1.setImageUrl("https://example.com/images/tennis-racket-main.jpg");
        image8_1.setItem(item8);
        itemImageRepository.save(image8_1);

        ItemImage image8_2 = new ItemImage();
        image8_2.setImageUrl("https://example.com/images/tennis-racket-detail.jpg");
        image8_2.setItem(item8);
        itemImageRepository.save(image8_2);

        ItemImage image8_3 = new ItemImage();
        image8_3.setImageUrl("https://example.com/images/tennis-racket-strings.jpg");
        image8_3.setItem(item8);
        itemImageRepository.save(image8_3);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Created: 1 Location, 2 Warehouses, 4 Categories, 8 Items with 20 Images");

        // Request 1 - IMPORT
        Request request1 = new Request();
        request1.setRequestType("IMPORT");
        request1.setStatus("PENDING");
        request1.setRejectReason(null);
        request1.setHandledBy(null);
        request1.setCreatedDate(LocalDateTime.now());
        request1.setUpdatedDate(LocalDateTime.now());
        request1.setFranchiseId(1L);
        request1.setSupplierId(1001L);
        request1.setQuantity(20L);
        request1.setItemName("Laptop Dell XPS 13");
        requestRepository.save(request1);

        // Request 2 - EXPORT
        Request request2 = new Request();
        request2.setRequestType("EXPORT");
        request2.setStatus("PENDING");
        request2.setRejectReason(null);
        request2.setHandledBy(2L);
        request2.setCreatedDate(LocalDateTime.now());
        request2.setUpdatedDate(LocalDateTime.now());
        request2.setFranchiseId(2L);
        request2.setSupplierId(null);
        request2.setQuantity(5L);
        request2.setItemName("iPhone 15 Pro");
        requestRepository.save(request2);

        // Request 3 - IMPORT (Rejected)
        Request request3 = new Request();
        request3.setRequestType("IMPORT");
        request3.setStatus("REJECTED");
        request3.setRejectReason("Supplier out of stock");
        request3.setHandledBy(3L);
        request3.setCreatedDate(LocalDateTime.now().minusDays(2));
        request3.setUpdatedDate(LocalDateTime.now().minusDays(1));
        request3.setFranchiseId(1L);
        request3.setSupplierId(2001L);
        request3.setQuantity(50L);
        request3.setItemName("Office Chair");
        requestRepository.save(request3);

        // Request 4 - EXPORT
        Request request4 = new Request();
        request4.setRequestType("EXPORT");
        request4.setStatus("PENDING");
        request4.setRejectReason(null);
        request4.setHandledBy(null);
        request4.setCreatedDate(LocalDateTime.now());
        request4.setUpdatedDate(LocalDateTime.now());
        request4.setFranchiseId(3L);
        request4.setSupplierId(null);
        request4.setQuantity(10L);
        request4.setItemName("Professional Treadmill");
        requestRepository.save(request4);

        System.out.println("Created: 4 Requests");

        // History 1 - IMPORT Laptop (Completed)
        RequestHistory history1 = new RequestHistory();
        history1.setRequest(request1);
        history1.setItem(item1); // Laptop Dell XPS 13
        history1.setCompletedDate(LocalDateTime.now().minusDays(1));
        historyRepository.save(history1);

        // History 2 - EXPORT iPhone (Completed)
        RequestHistory history2 = new RequestHistory();
        history2.setRequest(request2);
        history2.setItem(item2); // iPhone 15 Pro
        history2.setCompletedDate(LocalDateTime.now());
        historyRepository.save(history2);

        // History 3 - IMPORT Office Chair (Rejected but still logged)
        RequestHistory history3 = new RequestHistory();
        history3.setRequest(request3);
        history3.setItem(item3); // Office Chair
        history3.setCompletedDate(LocalDateTime.now().minusDays(2));
        historyRepository.save(history3);

        // History 4 - EXPORT Treadmill (Pending → chưa hoàn thành)
        RequestHistory history4 = new RequestHistory();
        history4.setRequest(request4);
        history4.setItem(item7); // Professional Treadmill
        history4.setCompletedDate(null);
        historyRepository.save(history4);

        // Thêm vài record để test filter theo ngày
        RequestHistory history5 = new RequestHistory();
        history5.setRequest(request2);
        history5.setItem(item2);
        history5.setCompletedDate(LocalDateTime.now().minusDays(5));
        historyRepository.save(history5);

        RequestHistory history6 = new RequestHistory();
        history6.setRequest(request1);
        history6.setItem(item1);
        history6.setCompletedDate(LocalDateTime.now().minusDays(10));
        historyRepository.save(history6);

        System.out.println("Created: 6 RequestHistory records");
    }
}
