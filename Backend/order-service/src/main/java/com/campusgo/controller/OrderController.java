package com.campusgo.controller;

import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.QuickOrderRequest;
import com.campusgo.exception.UnauthorizedException;
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

    @GetMapping("/cart")
    public CartSummaryDTO getCart(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.getCart(userId);
    }

    @PostMapping("/cart/items")
    public CartSummaryDTO addCartItem(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                      @RequestBody CartItemRequest req) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.addToCart(userId, req);
    }

    @DeleteMapping("/cart/items/{menuItemId}")
    public CartSummaryDTO removeCartItem(@RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                         @PathVariable("menuItemId") Long menuItemId) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.removeFromCart(userId, menuItemId);
    }

    @DeleteMapping("/cart")
    public void clearCart(@RequestHeader("X-User-Id") Long userId,
                          @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        orderService.clearCart(userId);
    }

    @PostMapping("/cart/checkout")
    public OrderDetail checkoutCart(@RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                    @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                    @RequestParam(required = false) String address,
                                    @RequestParam(defaultValue = "false") Boolean autoPay) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.checkoutCart(userId, address, idemKey, autoPay);
    }

    @PostMapping("/quick")
    public OrderDetail quickOrder(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                  @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                  @RequestBody QuickOrderRequest req) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.quickOrder(userId, req, idemKey);
    }
}

