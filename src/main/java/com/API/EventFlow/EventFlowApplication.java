package com.API.EventFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.API.EventFlow")
public class EventFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventFlowApplication.class, args);
	}
}
