package com.allra.allra.order.repository;

import com.allra.allra.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


} // end class
