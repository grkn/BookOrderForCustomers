# BookOrderForCustomers

First of all you need docker environment. Then you need to use docker-compose up -d in projects current directory.


After all the configuration is done by docker then you can use postman scripts to create user, getToken from user, create book, get orders, and create orders, get statistics and so on.

As you can see postman scripts are ready to use under postman folder.

## Docker part

docker-compose up -d starts two things one is mongodo and the other thing is application.

After creating application and mongo you can simply sen request to perform operations.

## Requests 
There are simply important postman scripts where one of them is singUp. It gets userName, password, and name fields where it can create a user in database.

```
POST http://localhost:8080/api/v1/authorize

{
    "userName": "test@test1.com",
    "password": "test",
    "name": "Gurkan"
}

```

Then you can simply get token by given credentials.

POST http://localhost:8080/api/v1/token

```
{
    "userName":"test@test1.com",
    "password": "test"
}
```
After that you can follow postman structure of 

createBook, updateBook, createOrder, getOrderById, getOrderByDateInterval, getStatistics.

PS: When you create book it automatically creates a book and puts it stock so you can assume that each book creation will add quantity 1 to same book. Also if book does not exists in stock, then you create a book with quantity 1. UpdateBook endpoint is a responsible for partial update of book where fields are quantity, name, price. On the other hand there is a createOrder endpoint which takes array. This array is show below.

```
POST http://localhost:8080/api/v1/orders
[
    {
        "orderDetail": {
            "quantity": 3,
            "book": {
                "bookId": "1"
            }
        }
    },
    {
        "orderDetail": {
            "quantity": 10,
            "book": {
                "bookId": "2"
            }
        }
    }
]

Response: 

[
    {
        "orderId": "935c7ab7-28d7-4cbc-9a86-f39a4c7f6516",
        "orderDate": "20/05/2022 11:11:49",
        "status": "SUCCESS",
        "totalPrice": "18.15",
        "orderDetail": {
            "quantity": 3,
            "book": {
                "bookId": "1",
                "name": "test1",
                "price": "6.05"
            }
        }
    },
    {
        "orderId": "111c7ab7-28d7-4cbc-9a86-f39a4c7f2222",
        "orderDate": "20/05/2022 11:11:49",
        "status": "OUT_OF_STOCK",
        "totalPrice": "18.15",
        "description": "Stock does not have any book or enough quantity for given order",
        "orderDetail": {
            "quantity": 10,
            "book": {
                "bookId": "2",
                "name": "test2",
                "price": "60.50"
            }
        }
    }
]
```


