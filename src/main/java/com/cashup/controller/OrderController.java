package com.cashup.controller;

import com.cashup.model.Customer;
import com.cashup.model.Order;
import com.cashup.model.StateOrder;
import com.cashup.service.CustomerService;
import com.cashup.service.OrderService;
import com.cashup.util.CustomErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ResponseEntity<List<Order>> getAllOrder() {
        List<Order> orders = orderService.findAll();
        if (orders.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer/{id}/order", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody Order order,
                                         @PathVariable("id") long id,
                                         UriComponentsBuilder ucBuilder) {

        Customer customer = customerService.findById(id);
        if (customer == null) {
            return new ResponseEntity(new CustomErrorType("Order is not create, because a customer with id " +
                    id + " not found."), HttpStatus.NOT_FOUND);
        }
        order.setCustomer(customer);
        orderService.save(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/customer/{id}/order").buildAndExpand(order.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/customer/{id}/orders", method = RequestMethod.GET)
    public ResponseEntity<?> getCustomerOrders(@PathVariable("id") long id) {

        Customer customer = customerService.findById(id);
        if (customer == null) {
            return new ResponseEntity(new CustomErrorType("A customer with id " +
                    id + " not found."), HttpStatus.NOT_FOUND);
        }

        List<Order> orders = customer.getOrders();
        if (orders.isEmpty()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrder(@PathVariable("id") long id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return new ResponseEntity(new CustomErrorType("Order with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @PostMapping("/order/{id}")
    public ResponseEntity<Order> confirmOrder(
            @PathParam("status") String status,
            @PathVariable("id") long id) {

        Order order = orderService.findById(id);

        if (order == null) {
            return new ResponseEntity(new CustomErrorType("Order with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        StateOrder stateOrder = null;
        try {
            String statusUpperCase = status.toUpperCase();
            stateOrder = StateOrder.valueOf(statusUpperCase);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity(new CustomErrorType("Status \"" + status + "\" in oder not found"), HttpStatus.NOT_FOUND);
        }

        order.setStateOrder(stateOrder);

        orderService.update(order);
        return new ResponseEntity<Order>(order, HttpStatus.OK);

    }

}
