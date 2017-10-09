package com.cashup.controller;

import com.cashup.model.Customer;
import com.cashup.model.Order;
import com.cashup.model.StateOrder;
import com.cashup.service.CustomerService;
import com.cashup.service.OrderService;
import com.cashup.util.CustomErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    OrderService orderService;

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> listAll() {
        List<Customer> customers = customerService.findAll();
        if (customers.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }


    @RequestMapping(value = "/customer", method = RequestMethod.POST)
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        if (customerService.isExist(customer)) {
            return new ResponseEntity(new CustomErrorType("Unable to create. A customer with number " +
                    customer.getNumber() + " already exist."), HttpStatus.CONFLICT);
        }

        setCustomerOrders(customer);

        customerService.save(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/customer/{id}").buildAndExpand(customer.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    private void setCustomerOrders(Customer customer) {
        List<Order> orders = customer.getOrders();
        if (orders != null) {
            for (Order order : orders) {
                order.setCustomer(customer);
            }
        }
    }

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCustomer(@PathVariable("id") long id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return new ResponseEntity(new CustomErrorType("Customer with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @PostMapping(value = "/customer/{id}")
    public ResponseEntity<?> updateCustomer(
            @RequestBody Customer customerUpdate, @PathVariable("id") long id) {

        Customer customer = customerService.findById(id);

        if (customer == null) {
            return new ResponseEntity(new CustomErrorType("Customer with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        customer.setId(id);

        mergeCustomer(customer, customerUpdate);

        customerService.update(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);

    }

    private void mergeCustomer(Customer customer, Customer customerUpdate) {

        String firstName = customerUpdate.getFirstName();
        if (firstName != null) {
            customer.setFirstName(firstName);
        }
        String lastName = customerUpdate.getLastName();
        if (lastName != null) {
            customer.setLastName(lastName);
        }
        LocalDate birthday = customerUpdate.getBirthday();
        if (birthday != null) {
            customer.setBirthday(birthday);
        }
        String number = customerUpdate.getNumber();
        if (number != null) {
            customer.setNumber(number);
        }
    }


}
