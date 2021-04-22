package com.campaign.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SubscriptionApplication {

	public static void main(String[] args) {

		SpringApplication.run(SubscriptionApplication.class, args);
	}

}
