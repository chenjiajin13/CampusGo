package com.campusgo.client;


import com.campusgo.dto.PaymentCreateRequest;
import com.campusgo.dto.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", path = "/internal/payments")
public interface PaymentClient {
    @PostMapping("/initiate")
    PaymentDTO initiate(@RequestBody PaymentCreateRequest req);

    @GetMapping("/order/{orderId}")
    PaymentDTO getByOrder(@PathVariable("orderId") Long orderId);

    @PostMapping("/{id}/simulate/success")
    PaymentDTO simulateSuccess(@PathVariable("id") Long id);

    @PostMapping("/{id}/simulate/fail")
    PaymentDTO simulateFail(@PathVariable("id") Long id);
}
