package com.reading.is.good.controller;

import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.model.Order;
import com.reading.is.good.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ChallengeConstant.BASE_URL)
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/orders")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<List<Order>> createOrder(@Valid @RequestBody List<Order> orders) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return new ResponseEntity<>(orderService.createOrders(userName, orders), HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/order/{orderId}")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<Order> fetchOrderById(@PathVariable String orderId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(orderService.getOrderByOrderId(userName, orderId));
    }

    @GetMapping(value = "/orders")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<List<Order>> getOrdersBetweenDates(@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
                                                             @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(orderService.getOrderBetweenDate(userName, startDate, endDate));
    }

}
