package com.cashup.controller;

import com.cashup.Application;
import com.cashup.model.Customer;
import com.cashup.model.Order;
import com.cashup.model.StateOrder;
import com.cashup.service.CustomerService;
import com.cashup.service.CustomerServiceImpl;
import com.cashup.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.cashup.Utils.createURLWithPort;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@WebMvcTest(value = CustomerController.class, secure = false)

//@RunWith(SpringRunner.class)
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        classes = Application.class)
//@AutoConfigureMockMvc
//@TestPropertySource(
//        locations = "classpath:application-integrationtest.properties")

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class CustomerControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Autowired
    private CustomerService customerService;
    private Customer customer;

    @Before
    public void setUp() throws Exception {
        headers.add("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTUwNzk3ODY0NX0._gLFHo0HSiUlzr1Ibwnpt4zmAzBmWQCt6FONzFRArFYoz6MgSkQgdRrjHjS47eor1d8EnJaw905e-KUqqLXQKg");
//        headers.add("Content-Type", "application/json");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ivan");
        customer.setLastName("Ivanov");
        customer.setBirthday(LocalDate.of(2000, 1, 1));
        customer.setNumber("123456789");

        List<Order> orders = new ArrayList<>();
//        order = new Order();
//        order.setId(1L);
//        order.setDate(LocalDateTime.of(2017, 10, 4, 12, 00, 00));
//        order.setAmount(new BigDecimal(12.55));
//        Locale UAH = new Locale("uk", "UA");
//        order.setCurrency(Currency.getInstance(UAH));
//        order.setStateOrder(StateOrder.NEW);
//        order.setCustomer(customer);
//
//        orders.add(order);
//        customer.setOrders(orders);


//        customerService.save(customer);
    }


    @Test
    public void listAll() throws Exception {
        customerService.save(customer);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customers"),
                HttpMethod.GET, entity, String.class);

        String expected = "[{\"id\":1,\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"birthday\":\"2000-01-01\",\"number\":\"123456789\",\"orders\":[]}]";

        String actual = response.getBody();
        JSONAssert.assertEquals(expected, actual, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void createCustomer() throws Exception {

        headers.add("Content-Type", "application/json");
        headers.add("accept", "application/json");

        String testCunsumer = "{\"id\":\"10\",\"firstName\":\"John\",\"lastName\":\"Karter\",\"birthday\":\"1978-02-11\",\"number\":\"123654789\"}";
        HttpEntity<String> entity = new HttpEntity<String>(testCunsumer, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customer"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String location = response.getHeaders().getLocation().toString();
        assertEquals(createURLWithPort(port, "/api/customer/10"), location);
    }

    @Test
    public void conflictCreateCustomer() throws Exception {
        customerService.save(customer);
        headers.add("Content-Type", "application/json");
        headers.add("accept", "application/json");

        String testCunsumer = "{\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"birthday\":\"2000-01-01\",\"number\":\"123456789\"}";
        HttpEntity<String> entity = new HttpEntity<String>(testCunsumer, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customer"),
                HttpMethod.POST, entity, String.class);

        String actual = response.getBody();
        String expected = "{\"errorMessage\": \"Unable to create. A customer with number 123456789 already exist.\"}";

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        JSONAssert.assertEquals(expected, actual, true);
    }


    @Test
    public void getCustomer() throws Exception {
        customerService.save(customer);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customer/1"),
                HttpMethod.GET, entity, String.class);

        String expected = "{\"id\":1,\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"birthday\":\"2000-01-01\",\"number\":\"123456789\",\"orders\":[]}";

        String actual = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, actual, true);
    }


    @Test
    public void updateCustomer() throws Exception {
        customerService.save(customer);
        headers.add("Content-Type", "application/json");
        headers.add("accept", "application/json");

        String testCunsumer = "{\"lastName\":\"Petrov\",\"birthday\":\"1989-12-01\"}";
        HttpEntity<String> entity = new HttpEntity<>(testCunsumer, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(port, "/api/customer/1"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Customer expectedCustomer = getCustomerFromJson(response.getBody());

        assertEquals(expectedCustomer.getLastName(), "Petrov");
        assertEquals(expectedCustomer.getBirthday(), LocalDate.of(1989, 12, 01));


    }

    private Customer getCustomerFromJson(String jsonString) throws IOException {
        ObjectMapper mapper;
        mapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        return mapper.readValue(jsonString, Customer.class);
    }

}