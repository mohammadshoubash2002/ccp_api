package com.mohammadshoubash.ccp_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync 
public class CcpApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcpApiApplication.class, args);
	}

}
