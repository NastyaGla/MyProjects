package com.example.fileparser.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.example.fileparser"})
@PropertySource("classpath:application.properties")
public class ApplicationConfig  {
}
