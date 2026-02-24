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
    public OrderDetail create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Principal-Type", required = false) String pt,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String address
    ) {

        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");

        return orderService.createOrder(userId, merchantId, address, idemKey);
    }
}

