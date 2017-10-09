package com.cashup.controller;

import com.cashup.Application;
import com.cashup.model.Customer;
import com.cashup.model.Order;
import com.cashup.model.StateOrder;
import com.cashup.service.CustomerService;
import com.cashup.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.cashup.Utils.createURLWithPort;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class OrderControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;

    private Order order;
    private Customer customer;

    @Before
    public void setUp() throws Exception {
        headers.add("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTUwNzk3ODY0NX0._gLFHo0HSiUlzr1Ibwnpt4zmAzBmWQCt6FONzFRArFYoz6MgSkQgdRrjHjS47eor1d8EnJaw905e-KUqqLXQKg");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ivan");
        customer.setLastName("Ivanov");
        customer.setBirthday(LocalDate.of(2000, 1, 1));
        customer.setNumber("123456789");

        List<Order> orders = new ArrayList<>();
        order = new Order();
        order.setId(1L);
        order.setDate(LocalDateTime.of(2017, 10, 4, 12, 00, 00));
        order.setAmount(new BigDecimal(12.55));
        Locale UAH = new Locale("uk", "UA");
        order.setCurrency(Currency.getInstance(UAH));
        order.setStateOrder(StateOrder.NEW);
        order.setCustomer(customer);

        orders.add(order);
        customer.setOrders(orders);

    }


    @Test
    public void getAllOrder() throws Exception {
        orderService.save(order);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/orders"),
                HttpMethod.GET, entity, String.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createOrder() throws Exception {
        customerService.save(customer);

        headers.add("Content-Type", "application/json");
        headers.add("accept", "application/json");

        String testOrder = "{\"date\":\"2017-10-04 12:00:00\",\"stateOrder\":\"NEW\",\"amount\":44.55,\"currency\":\"EUR\"}";
        HttpEntity<String> entity = new HttpEntity<String>(testOrder, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customer/1/order"),
                HttpMethod.POST,
                entity,
                String.class);


        Customer findCustomer = customerService.findById(1L);
        String location = response.getHeaders().getLocation().toString();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(findCustomer.getOrders().size(), 2);
        assertEquals(createURLWithPort(port, "/api/customer/2/order"), location);
    }

    @Test
    public void getCustomerOrders() throws Exception {
        customer.setId(1L);
        customerService.save(customer);

        ResponseEntity<String> response = getResponseEntity("/api/customer/1/orders");
        String actual = response.getBody();

        String expected = "{\"id\":1,\"date\":\"2017-10-04 12:00:00\",\"stateOrder\":\"NEW\",\"amount\":12.55,\"currency\":\"UAH\"}";
//        JSONAssert.assertEquals(expected, actual, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(actual.contains(expected), true);
    }

    @Test
    public void getCustomerOrdersNotFound() throws Exception {
        ResponseEntity<String> response = getResponseEntity("/api/customer/99/orders");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    private ResponseEntity<String> getResponseEntity(String url) {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        return restTemplate.exchange(
                createURLWithPort(port, url),
                HttpMethod.GET, entity, String.class);
    }

    @Test
    public void getOrder() throws Exception {
//        orderService.save(order);
        customerService.save(customer);
        ResponseEntity<String> response = getResponseEntity("/api/order/1");

        String actual = response.getBody();
        String expected = "{\"id\":1,\"date\":\"2017-10-04 12:00:00\",\"stateOrder\":\"NEW\",\"amount\":12.55,\"currency\":\"UAH\"}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void confirmOrder() throws Exception {
//        orderService.save(order);
        customerService.save(customer);

        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        System.out.println(order);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(port, "/api/order/1?status={status}"),
                HttpMethod.POST,
                entity,
                String.class,
                "finished"
        );

        String actual = response.getBody();
        String expected = "{\"id\":1,\"date\":\"2017-10-04 12:00:00\",\"stateOrder\":\"FINISHED\",\"amount\":12.55,\"currency\":\"UAH\"}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, actual, true);
    }

}