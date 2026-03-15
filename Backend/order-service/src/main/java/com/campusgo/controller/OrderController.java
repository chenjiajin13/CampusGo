package com.campusgo.controller;

import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.QuickOrderRequest;
import com.campusgo.dto.BatchCheckoutResponse;
import com.campusgo.dto.MerchantAnalyticsDTO;
import com.campusgo.exception.UnauthorizedException;
import com.campusgo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private boolean isUser(String principalType) {
        return principalType != null && "USER".equalsIgnoreCase(principalType);
    }

    @GetMapping("/{id}")
    public OrderDetail detail(@PathVariable("id") Long id,
                              @RequestHeader("X-User-Id") Long principalId,
                              @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        OrderDetail detail = orderService.getOrder(id);
        if (pt == null) throw new UnauthorizedException("FORBIDDEN");
        if ("USER".equalsIgnoreCase(pt) && !principalId.equals(detail.getUserId())) throw new UnauthorizedException("FORBIDDEN");
        if ("MERCHANT".equalsIgnoreCase(pt) && !principalId.equals(detail.getMerchantId())) throw new UnauthorizedException("FORBIDDEN");
        if ("RUNNER".equalsIgnoreCase(pt) && (detail.getRunnerId() == null || !principalId.equals(detail.getRunnerId())))
            throw new UnauthorizedException("FORBIDDEN");
        return detail;
    }

    @PostMapping
    public OrderDetail create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Principal-Type", required = false) String pt,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "address", required = false) String address
    ) {

        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");

        return orderService.createOrder(userId, merchantId, address, idemKey);
    }

    @GetMapping("/cart")
    public CartSummaryDTO getCart(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.getCart(userId);
    }

    @PostMapping("/cart/items")
    public CartSummaryDTO addCartItem(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                      @RequestBody CartItemRequest req) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.addToCart(userId, req);
    }

    @DeleteMapping("/cart/items/{menuItemId}")
    public CartSummaryDTO removeCartItem(@RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                         @PathVariable("menuItemId") Long menuItemId) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.removeFromCart(userId, menuItemId);
    }

    @DeleteMapping("/cart")
    public void clearCart(@RequestHeader("X-User-Id") Long userId,
                          @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        orderService.clearCart(userId);
    }

    @PostMapping("/cart/checkout")
    public OrderDetail checkoutCart(@RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                    @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                    @RequestParam(value = "address", required = false) String address,
                                    @RequestParam(value = "autoPay", defaultValue = "false") Boolean autoPay) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.checkoutCart(userId, address, idemKey, autoPay);
    }

    @PostMapping("/cart/checkout-batch")
    public BatchCheckoutResponse checkoutCartBatch(@RequestHeader("X-User-Id") Long userId,
                                                   @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                   @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                                   @RequestParam(value = "address", required = false) String address,
                                                   @RequestParam(value = "autoPay", defaultValue = "false") Boolean autoPay) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.checkoutCartBatch(userId, address, idemKey, autoPay);
    }

    @PostMapping("/quick")
    public OrderDetail quickOrder(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                  @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
                                  @RequestBody QuickOrderRequest req) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.quickOrder(userId, req, idemKey);
    }

    @GetMapping("/my")
    public List<OrderDetail> myOrders(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!isUser(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.listMyOrders(userId);
    }

    @GetMapping("/merchant/me")
    public List<OrderDetail> merchantOrders(@RequestHeader("X-User-Id") Long merchantId,
                                            @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.listMerchantOrders(merchantId);
    }

    @GetMapping("/merchant/me/analytics")
    public MerchantAnalyticsDTO merchantAnalytics(@RequestHeader("X-User-Id") Long merchantId,
                                                  @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                  @RequestParam(value = "weekStart", required = false) String weekStart) {
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.getMerchantAnalytics(merchantId, weekStart);
    }

    @GetMapping("/merchant/me/{orderId}")
    public OrderDetail merchantOrderDetail(@RequestHeader("X-User-Id") Long merchantId,
                                           @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                           @PathVariable("orderId") Long orderId) {
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) throw new UnauthorizedException("FORBIDDEN");
        OrderDetail detail = orderService.getOrder(orderId);
        if (!merchantId.equals(detail.getMerchantId())) throw new UnauthorizedException("FORBIDDEN");
        return detail;
    }

    @GetMapping("/runner/me")
    public List<OrderDetail> runnerOrders(@RequestHeader("X-User-Id") Long runnerId,
                                          @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (pt == null || !"RUNNER".equalsIgnoreCase(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.listRunnerOrders(runnerId);
    }

    @PostMapping("/runner/me/{orderId}/complete")
    public OrderDetail completeByRunner(@RequestHeader("X-User-Id") Long runnerId,
                                        @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                        @PathVariable("orderId") Long orderId) {
        if (pt == null || !"RUNNER".equalsIgnoreCase(pt)) throw new UnauthorizedException("FORBIDDEN");
        return orderService.completeByRunner(runnerId, orderId);
    }
}


