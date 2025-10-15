package com.campusgo.controller;


import com.campusgo.domain.Payment;
import com.campusgo.dto.*;
import com.campusgo.mapper.PaymentMapper;
import com.campusgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PublicPaymentController {


    private final PaymentService service;


    /** After the front-end orders, initialize the payment (return a mock payUrl/qrcode) */
    @PostMapping
    public PaymentInitResponse create(@RequestBody PaymentCreateRequest req) {
        Payment p = service.create(req.getOrderId(), req.getUserId(), req.getMerchantId(), req.getAmountCents(), req.getCurrency(), req.getMethod());
        return PaymentInitResponse.builder()
                .paymentId(p.getId())
                .payUrl("https://mock.pay/checkout/" + p.getId())
                .qrcodeUrl("https://mock.qr/" + p.getId())
                .build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> get(@PathVariable("id") Long id) {
        return service.findById(id).map(PaymentMapper::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getByOrder(@PathVariable("orderId") Long orderId) {
        return service.findByOrderId(orderId).map(PaymentMapper::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<PaymentDTO> listByUser(@RequestParam("userId") Long userId) {
        return service.listByUser(userId).stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
    }


    /** mock refund */
    @PostMapping("/{id}/refund")
    public PaymentDTO refund(@PathVariable("id") Long id, @RequestBody RefundRequest req) {
        return PaymentMapper.toDTO(service.refund(id, req.getAmountCents(), req.getReason()));
    }


    /** only for testing */
    @PatchMapping("/{id}/status")
    public PaymentDTO updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        return PaymentMapper.toDTO(service.updateStatus(id, req.getStatus()));
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}