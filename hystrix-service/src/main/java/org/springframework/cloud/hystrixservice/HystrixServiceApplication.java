package org.springframework.cloud.hystrixservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @EnableCircuitBreaker  这个注解是开启断路器hystrix的
 * @EnableDiscoveryClient 能被eureka发现的注解
 * @Author HyJan
 */
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
public class HystrixServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(HystrixServiceApplication.class, args);
  }
}
