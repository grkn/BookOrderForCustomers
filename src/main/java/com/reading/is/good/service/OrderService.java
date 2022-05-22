package com.reading.is.good.service;

import com.reading.is.good.exception.NotFoundException;
import com.reading.is.good.model.*;
import com.reading.is.good.mongorepos.CustomerRepository;
import com.reading.is.good.mongorepos.StockRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String FAILED = "FAILED";
    private static final String SUCCESS = "SUCCESS";
    private static final String OUT_OF_STOCK = "OUT_OF_STOCK";

    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;
    private final StockRepository stockRepository;

    public List<Order> createOrders(String userName, List<Order> orders) {
        LOGGER.debug("Create orders start with orders {} for given user: {}", orders, userName);
        Customer customer = customerRepository.findByUsername(userName)
                .orElseThrow(() -> new NotFoundException(String.format("User can not be found by given username %s", userName)));
        List<Order> newOrders = createNewOrder(orders, customer);

        for (Order order : newOrders) {
            try {
                decreaseQuantityOfBookInStock(order);
                order.setStatus(SUCCESS);
                LOGGER.info("Order is successful: {} for given user: {}", order, userName);
            } catch (NotFoundException ex) {
                LOGGER.debug("Order is failed: {} for given user: {}. Error Message: {}", order, userName, ex.getMessage());
                order.setStatus(OUT_OF_STOCK);
                order.setDescription("Stock does not have any book or enough quantity for given order");
            } catch (Exception ex) {
                LOGGER.info("Order is failed: {} for given user: {}. Error Message: {}", order, userName, ex.getMessage());
                order.setStatus(FAILED);
                order.setDescription("Unknown Exception occurred. Please contact your administrator.");
            } finally {
                customerRepository.save(customer);
            }
        }

        LOGGER.debug("Create orders finish with orders {} for given user: {}", orders, userName);
        return newOrders;
    }

    private void decreaseQuantityOfBookInStock(Order order) {
        Query query = new Query();
        query.addCriteria(Criteria.where("stockDetails.book.bookId").is(order.getOrderDetail().getBook().getBookId()));
        query.addCriteria(Criteria.where("stockDetails.quantity").gte(order.getOrderDetail().getQuantity()));

        Update update = new Update();
        update.inc("stockDetails.quantity", order.getOrderDetail().getQuantity() * -1);
        Stock stock = mongoTemplate.findAndModify(query, update, Stock.class);
        if (null == stock) {
            throw new NotFoundException(String.format("Stock does not have any book related to given order or quantity is not enough %s", order));
        }
    }

    private List<Order> createNewOrder(List<Order> orders, Customer customer) {
        List<Order> newOrders = new ArrayList<>();
        for (Order order : orders) {
            Optional<Stock> stock = stockRepository.findByStockDetailsBookBookId(order.getOrderDetail().getBook().getBookId());
            Book book = order.getOrderDetail().getBook();
            stock.ifPresent(value -> {
                book.setPrice(value.getStockDetails().getBook().getPrice());
                book.setName(value.getStockDetails().getBook().getName());
            });
            Order newOrder = Order.builder()
                    .orderDate(LocalDateTime.now())
                    .status(IN_PROGRESS)
                    .orderId(UUID.randomUUID().toString())
                    .totalPrice(new BigDecimal(order.getOrderDetail().getQuantity()).multiply(order.getOrderDetail().getBook().getPrice()))
                    .orderDetail(
                            Detail.builder()
                                    .quantity(order.getOrderDetail().getQuantity())
                                    .book(book)
                                    .build()
                    )
                    .build();
            newOrders.add(newOrder);
            if (null != customer.getOrders()) {
                // Order exists
                customer.getOrders().add(newOrder);
            } else {
                // Empty order
                List<Order> newOrderList = new ArrayList<>();
                newOrderList.add(newOrder);
                customer.setOrders(newOrderList);
            }
        }

        customerRepository.save(customer);
        return newOrders;
    }

    public Order getOrderByOrderId(String username, String orderId) {
        AggregationOperation match = Aggregation.match(Criteria.where("orders").exists(true));
        AggregationOperation customerName = Aggregation.match(Criteria.where("username").is(username));
        AggregationOperation unwind = Aggregation.unwind("orders");
        AggregationOperation orderIdMatch = Aggregation.match(Criteria.where("orders.orderId").is(orderId));
        AggregationOperation projection = Aggregation.project("orders.orderId", "orders.status", "orders.orderDate",
                "orders.orderDetail", "orders.totalPrice");
        Aggregation aggregation = Aggregation.newAggregation(match, customerName, unwind, orderIdMatch, projection);

        AggregationResults<Order> orders = mongoTemplate
                .aggregate(aggregation, mongoTemplate.getCollectionName(Customer.class), Order.class);
        if (orders.getMappedResults().size() > 0) {
            return orders.getMappedResults().get(0);
        } else {
            throw new NotFoundException(String.format("Order can not be found by given order id: %s", orderId));
        }
    }

    public List<Order> getOrderBetweenDate(String username, LocalDate startDate, LocalDate endDate) {
        AggregationOperation match = Aggregation.match(Criteria.where("orders").exists(true));
        AggregationOperation customerName = Aggregation.match(Criteria.where("username").is(username));
        AggregationOperation unwind = Aggregation.unwind("orders");
        AggregationOperation gtStartDateLtEndDateMatch = Aggregation.match(Criteria.where("orders.orderDate").gte(startDate).lt(endDate));
        AggregationOperation projection = Aggregation.project("orders.orderId", "orders.status", "orders.orderDate",
                "orders.orderDetail", "orders.totalPrice");
        Aggregation aggregation = Aggregation.newAggregation(match, customerName, unwind, gtStartDateLtEndDateMatch, projection);

        AggregationResults<Order> orders = mongoTemplate
                .aggregate(aggregation, mongoTemplate.getCollectionName(Customer.class), Order.class);
        return orders.getMappedResults();
    }
}
