package com.cashup.service;

import com.cashup.model.Customer;
import com.cashup.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer findById(Long id) {
        return customerRepository.findOne(id);
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public void update(Customer customer) {
        save(customer);
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.delete(id);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public boolean isExist(Customer customer) {
        return findByNumber(customer.getNumber()) != null;
    }

    @Override
    public Customer findByNumber(String number) {
        return customerRepository.findByNumber(number);
    }

}
