package com.cs.market.cart.repository;

import com.cs.market.cart.entity.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findWithItemsByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 선택: 동시 추가/증가 시 직렬화
    @Query("""
        select c from Cart c
        left join fetch c.items i
        left join fetch i.product p
        where c.user.id = :userId
    """)
    Optional<Cart> findWithItemsByUserIdForUpdate(@Param("userId") Long userId);

} // end class