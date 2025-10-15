package com.campusgo.controller;

import com.campusgo.dto.OrderDetail;
import com.campusgo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public OrderDetail detail(@PathVariable("id") Long id) {
        return orderService.getOrder(id);
    }
}

