package com.bank.topup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TopUpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TopUpApplication.class, args);
    }
}