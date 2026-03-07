package org.example.warehouseservice.repository;

import org.example.warehouseservice.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    Optional<ItemImage> findByImageId(Long imageId);

    @Query("SELECT ii FROM ItemImage ii WHERE ii.item.itemId = :itemId")
    List<ItemImage> findItemImageByItemId(Long itemId);

//    @Query(" SELECT ii FROM ItemImage ii WHERE ii.item.itemId = :itemId ORDER BY ii.imageId DESC")
//    List<ItemImage> findItemImageByItemId(Long itemId,int limit);

}
