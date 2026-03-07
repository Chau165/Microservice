package org.example.warehouseservice.repository;

import org.example.warehouseservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

    @Query("SELECT i FROM Item i WHERE i.name like %:name% AND i.category.warehouse.warehouseId = :warehouseId")
    List<Item> findItemByName(String name,Long warehouseId);
    List<Item> findItemByName(String name);

    @Query("SELECT i FROM Item i WHERE i.name = :name AND i.category.warehouse.warehouseId = :warehouseId")
    Item isExistedByName(String name,Long warehouseId);

    @Query("SELECT i FROM Item i WHERE i.price >= :minPrice AND i.price <= :maxPrice AND i.category.warehouse.warehouseId = :warehouseId")
    List<Item> findItemByPrice(double minPrice, double maxPrice,Long warehouseId);

    @Query("SELECT i FROM Item i WHERE i.supplierName like %:supplierName% AND i.category.warehouse.warehouseId = :warehouseId")
    List<Item> findItemBySupplierName(String supplierName,Long warehouseId);

    @Query("SELECT i FROM Item i JOIN i.category c WHERE c.name like %:categoryName% AND c.warehouse.warehouseId = :warehouseId")
    List<Item> findItemByCatergory(String categoryName,Long warehouseId);

    @Query("SELECT i FROM Item i WHERE i.category.warehouse.location.name like %:location%")
    List<Item> findItemByLocation(String location);
    boolean existsByCategory_CategoryId(Long categoryId);

    @Query("SELECT i FROM Item i WHERE i.category.warehouse.warehouseId = :warehouseId")
    List<Item> findAll(Long warehouseId);
}
