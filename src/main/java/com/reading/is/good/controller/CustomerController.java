package com.reading.is.good.controller;

import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.model.Order;
import com.reading.is.good.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ChallengeConstant.BASE_URL)
public class CustomerController {

    private final UserService userService;

    @GetMapping(value = "/user/orders")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<Page<Order>> getOrders(@PageableDefault Pageable pageable) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(userService
                .findOrdersByCustomer(userName, pageable));
    }


}
