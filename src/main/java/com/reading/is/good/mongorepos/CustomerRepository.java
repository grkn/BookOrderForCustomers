package com.reading.is.good.mongorepos;

import com.reading.is.good.model.Customer;
import com.reading.is.good.resource.BookStatisticsResource;
import com.reading.is.good.resource.OrderStatisticsResource;
import com.reading.is.good.resource.TotalPriceStatisticsResource;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByUsername(String userName);

    @Aggregation(value = {"{ \"$match\" : { \"orders\" : { \"$exists\" : true}}}",
            "{ \"$match\" : { \"username\" : ?0}}, ",
            "{$unwind : \"$orders\"}, ", "{ \"$match\" :  { \"orders.status\" : \"SUCCESS\"} }",
            "{$project : {\"orderId\" : \"$orders.orderId\", \"month\": {\"$month\" : \"$orders.orderDate\"}}}, ",
            "{ \"$group\" : {_id: \"$month\" , \"totalOrderCount\" : { $sum : 1 }}}"})
    List<OrderStatisticsResource> findOrderCountByMonth(String username);

    @Aggregation(value = {"{ \"$match\" : { \"orders\" : { \"$exists\" : true}}}",
            "{ \"$match\" : { \"username\" : ?0}}, ",
            "{$unwind : \"$orders\"}, ", "{ \"$match\" :  { \"orders.status\" : \"SUCCESS\"} }",
            "{$project : {\"totalPrice\" : \"$orders.totalPrice\", \"month\": {\"$month\" : \"$orders.orderDate\"}}}, ",
            "{ \"$group\" : {_id: \"$month\" , \"totalPrice\" : { $sum : \"$totalPrice\" }}}"})
    List<TotalPriceStatisticsResource> findTotalPriceByMonth(String username);

    @Aggregation(value = {"{ \"$match\" : { \"orders\" : { \"$exists\" : true}}}",
            "{ \"$match\" : { \"username\" : ?0}}, ",
            "{$unwind : \"$orders\"}, ", "{ \"$match\" :  { \"orders.status\" : \"SUCCESS\"} }",
            "{$project : {\"bookId\" : \"$orders.orderDetail.book.bookId\", \"quantity\" : \"$orders.orderDetail.quantity\", \"month\": {\"$month\" : \"$orders.orderDate\"}}}, ",
            "{ \"$group\" : { _id: {\"month\" : \"$month\", \"bookId\" : \"$bookId\" } , \"totalBookCount\" : { $sum : \"$quantity\" }}}"})
    List<BookStatisticsResource> findBookCountByMonth(String username);
}
