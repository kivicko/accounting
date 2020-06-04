# Accounting

Used Spring Boot v2.2.6 / Created project with Spring Initializr

This project is a showcase of not only hibernate optimistic lock mechanism, but also unit & integration test usage

# About Project
This project is basically player withdraw / credit operations by api at its minimum.

You can use sample Player that created on start. ID : 5

It has unit tests & integration for all possible cases. 

# Endpoints

You need to have right authorization for requests.

USER Roled Api Account : USER / USER

ADMIN Roled Api Account : ADMIN / ADMIN

For Withdraw / Credit endpoints, you must have ADMIN role.

You can use log & balance endpoints with USER role.

You can use following postman collection for accepted requests.

https://www.getpostman.com/collections/6ddf2ea98cef2b622d7f

Endpoint details: 

http://localhost:8080/balance/{id} (GET) (Accepts Player ID) : Shows player balance

http://localhost:8080/log/{id} (GET) (Accepts Player ID) : Shows transaction history of player

http://localhost:8080/credit (POST) (Accepts TransactionDTO) : creates credit request

http://localhost:8080/withdraw (POST) : (Accepts TransactionDTO) : creates withdraw request


