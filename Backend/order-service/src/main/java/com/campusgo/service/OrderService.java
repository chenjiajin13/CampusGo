package com.campusgo.service;


import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.QuickOrderRequest;

public interface OrderService {
    OrderDetail getOrder(Long orderId);
    OrderDetail createOrder(Long userId, Long merchantId, String address,String idemKey);
    CartSummaryDTO addToCart(Long userId, CartItemRequest req);
    CartSummaryDTO getCart(Long userId);
    CartSummaryDTO removeFromCart(Long userId, Long menuItemId);
    void clearCart(Long userId);
    OrderDetail checkoutCart(Long userId, String address, String idemKey, Boolean autoPay);
    OrderDetail quickOrder(Long userId, QuickOrderRequest req, String idemKey);
}
