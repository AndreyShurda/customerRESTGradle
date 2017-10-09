package com.cashup.service;

import com.cashup.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer findById(Long id);

    void save(Customer customer);

    void update(Customer customer);

    void deleteById(Long id);

    List<Customer> findAll();

    boolean isExist(Customer customer);

    Customer findByNumber(String number);
}
