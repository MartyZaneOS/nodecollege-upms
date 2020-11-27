package com.nodecollege.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author LC
 * @date 2020/2/25 21:26
 */
@SpringBootApplication(scanBasePackages = {"com.nodecollege.cloud"})
@EnableTransactionManagement
@EnableDiscoveryClient
@ServletComponentScan  //注册过滤器注解
@EnableFeignClients
@EnableScheduling
public class OperateApplication {
    public static void main(String[] args) {
        SpringApplication.run(OperateApplication.class, args);
    }
}
