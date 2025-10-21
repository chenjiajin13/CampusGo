package com.campusgo.controller;

import com.campusgo.dto.OrderDetail;
import com.campusgo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDetail detail(@PathVariable("id") Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    public OrderDetail create(@RequestParam Long userId,
                              @RequestParam Long merchantId,
                              @RequestParam(required = false) String address) {
        return orderService.createOrder(userId, merchantId, address);
    }
}

