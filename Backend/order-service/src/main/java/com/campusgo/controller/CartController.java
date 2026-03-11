package com.campusgo.controller;

import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.OrderDetail;
import com.campusgo.exception.UnauthorizedException;
import com.campusgo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final OrderService orderService;

    @GetMapping
    public CartSummaryDTO getCart(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.getCart(userId);
    }

    @PostMapping("/items")
    public CartSummaryDTO addCartItem(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                      @RequestBody CartItemRequest req) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.addToCart(userId, req);
    }

    @DeleteMapping("/items/{menuItemId}")
    public CartSummaryDTO removeCartItem(@RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                         @PathVariable("menuItemId") Long menuItemId) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.removeFromCart(userId, menuItemId);
    }

    @DeleteMapping
    public void clearCart(@RequestHeader("X-User-Id") Long userId,
                          @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        orderService.clearCart(userId);
    }

    @PostMapping("/checkout")
    public OrderDetail checkoutCart(@RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                    @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                    @RequestParam(required = false) String address,
                                    @RequestParam(defaultValue = "false") Boolean autoPay) {
        if (!"USER".equals(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.checkoutCart(userId, address, idemKey, autoPay);
    }
}

