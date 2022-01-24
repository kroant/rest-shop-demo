# REST Shop Demo

## Requirements
- Java 17+
- Maven 3.8.3+

## How to run
Build the application and run tests:

`mvn clean package`

Run the application:

`mvn spring-boot:run`

Open Swagger UI in browser:

[http://localhost:8080/rest-shop-demo/swagger-ui.html](http://localhost:8080/rest-shop-demo/swagger-ui.html)

### Run in Docker (optional)

Build the image:

`mvn spring-boot:build-image`

Run the application:

`docker run --publish 8080:8080 rest-shop-demo:1.0-SNAPSHOT`

## Task
REST interface to maintain a database of products and orders.

REST order operations

- creation of the order
- cancellation of the order
- payment of the order

REST product operations

- creation of the product
- deleting product
- updating of the product - updating of product quantity in stock, name, ...

Every product must have a name, quantity in stock and price per unit. The order can be for 1 or N
existing products and for any unit quantity (e.g. 5 bread, 2 bottles of milk, ...), but it is always
necessary for all the products to be in adequate quantity in stock during the creation of the order.

If this condition is not met, an answer for calling of the endpoint for creation of the order will be
items, for which there isn’t enough quantity including the missing quantity.

Work with the fact that every order is decreasing the number of available items for following orders,
even in case that the order is not paid yet, for the period of max 30 minutes, then a new order must
be submitted (the current order is invalid). Cancellation of the order releases the reserved goods.