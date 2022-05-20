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
