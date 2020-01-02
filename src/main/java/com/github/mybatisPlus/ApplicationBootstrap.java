package com.github.mybatisPlus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.mybatisPlus.annotation.EnableDynamicRouteDataSource;


@SpringBootApplication
@EnableDynamicRouteDataSource
public class ApplicationBootstrap {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationBootstrap.class, args);
	}
}
