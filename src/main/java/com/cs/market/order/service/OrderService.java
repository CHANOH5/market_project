package com.cs.market.order.service;

import com.cs.market.cart.dto.CartLine;

import java.util.List;

public interface OrderService {

    public Long createOrder(Long userId, List<CartLine> lines);

}
