package com.cashup.service;

import com.cashup.model.Order;
import com.cashup.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Order findById(Long id) {
        return orderRepository.findOne(id);
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void update(Order order) {
        save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.delete(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
