package com.ljs.appointment.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * author ljs
 * create 2021-3-10
 *
 * 主启动类，且由于其他配置以及工具类不在同一包名下，因此需要设置包扫描路径
 */

@SpringBootApplication
@ComponentScan("com.ljs")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
