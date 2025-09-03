package com.cs.market.order.repository;

import com.cs.market.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


} // end class
