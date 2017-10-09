package com.cashup.service;

import com.cashup.model.Order;

import java.util.List;

public interface OrderService {
    Order findById(Long id);

    void save(Order order);

    void update(Order order);

    void deleteById(Long id);

    List<Order> findAll();
}
