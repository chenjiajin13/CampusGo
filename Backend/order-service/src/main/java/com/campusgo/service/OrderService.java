package com.campusgo.service;


import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.QuickOrderRequest;
import com.campusgo.dto.BatchCheckoutResponse;

import java.util.List;

public interface OrderService {
    OrderDetail getOrder(Long orderId);
    OrderDetail createOrder(Long userId, Long merchantId, String address,String idemKey);
    CartSummaryDTO addToCart(Long userId, CartItemRequest req);
    CartSummaryDTO getCart(Long userId);
    CartSummaryDTO removeFromCart(Long userId, Long menuItemId);
    void clearCart(Long userId);
    OrderDetail checkoutCart(Long userId, String address, String idemKey, Boolean autoPay);
    BatchCheckoutResponse checkoutCartBatch(Long userId, String address, String idemKey, Boolean autoPay);
    OrderDetail quickOrder(Long userId, QuickOrderRequest req, String idemKey);
    List<OrderDetail> listMyOrders(Long userId);
    List<OrderDetail> listMerchantOrders(Long merchantId);
    List<OrderDetail> listRunnerOrders(Long runnerId);
    OrderDetail completeByRunner(Long runnerId, Long orderId);
}
