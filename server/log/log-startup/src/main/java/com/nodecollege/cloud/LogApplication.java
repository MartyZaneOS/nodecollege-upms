package com.nodecollege.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author LC
 * @date 2020/2/25 21:26
 */
@SpringBootApplication(scanBasePackages = {"com.nodecollege.cloud"})
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients
public class LogApplication {
    private static final Logger logger = LoggerFactory.getLogger(LogApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }
}
