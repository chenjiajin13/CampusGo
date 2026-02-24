package com.campusgo.service;


import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.UserDTO;

public interface OrderService {
    OrderDetail getOrder(Long orderId);
    OrderDetail createOrder(Long userId, Long merchantId, String address,String idemKey);
   }

