package com.cs.market.product.repository;

import com.cs.market.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph
    @Query(
            value = """
            SELECT p
            FROM Product p
            WHERE (:categoryId IS NULL or p.category.id = :categoryId)
                AND (:name IS NULL or p.name = :name)
                AND (:minPrice IS NULL or p.price >= :minPrice)
                AND (:maxPrice IS NULL or p.price <= :maxPrice)
            """
    )
    Page<Product> search(@Param("categoryId") Long categoryId,
                         @Param("name") String name,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);


    @Modifying
    @Query(
            "update Product p set p.stock = p.stock - :quantity "+
            "where p.id = :productId and p.stock >= :quantity"
    )
    int decreaseStockIfEnough(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query(
            "update Product p set p.stock = p.stock + :quantity " +
            "where p.id = :productId"
    )
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

} // end class