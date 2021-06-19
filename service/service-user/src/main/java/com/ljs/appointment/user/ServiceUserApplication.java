package com.ljs.appointment.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component("com.ljs")
@EnableDiscoveryClient
@EnableFeignClients("com.ljs")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
