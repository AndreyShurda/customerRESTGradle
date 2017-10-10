# REST API for working with customers and their orders.

## This service provides an opportunity:
- get all customers
- get customer by id
- create a new customer
- modification of customer properties
- get all orders
- get all customer orders
- create a new customer order
- confirm customer order

## This API use such technologies:
- Java 1.8
- SpringBoot 1.5
- PostgreSQL 9.5
- Gradle 4
- secure access to the API (restricted access by headers, tokens, etc.)
- Unit, Integration tests
- the possibility of obtaining data in different formats (JSON, XML)
- Hibernate 5.0

### Run API
run with gradle: gradle bootRun
```json
Then open any rest client and send json to registers a new user
curl -H "Content-Type: application/json" -X POST -d '{
    "username": "admin",
    "password": "password"
}'http://localhost:8080/users/sign-up

logs into the application (JWT is generated)
curl -i -H "Content-Type: application/json" -X POST -d '{
    "username": "admin",
    "password": "password"
}' http://localhost:8080/login

Remember to replace xxx.yyy.zzz with the JWT retrieved above
curl -H "Content-Type: application/json" \
-H "Authorization: Bearer xxx.yyy.zzz" \
-X POST -d '{
  "firstName": "Ivan",
  "lastName": "Ivanov",
  "birthday": "1988-07-22",
  "number": "22222222222"
}'  http://localhost:8080/api/customer
```
