package com.campusgo;

import com.campusgo.client.MerchantClient;
import com.campusgo.client.NotificationClient;
import com.campusgo.client.PaymentClient;
import com.campusgo.client.RunnerClient;
import com.campusgo.client.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {
        UserClient.class,
        RunnerClient.class,
        MerchantClient.class,
        PaymentClient.class,
        NotificationClient.class
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
