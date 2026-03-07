package org.example.warehouseservice.repository;

import org.example.warehouseservice.entity.Category;
import org.example.warehouseservice.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    List<Category> findAllByStatusOrderByDisplayOrderAsc(CategoryStatus status);
    List<Category> findByWarehouse_WarehouseIdOrWarehouseIsNull(Long warehouseId);
    Optional<Category> findByName(String name);
}
