package com.thehecklers.pracai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@SpringBootApplication
public class PracaiApplication {
	@Bean
	@Description("What is the weather in location")
	public Function<WxRequest, WxResponse> weatherFunction() {
		return new WeatherService();
	}


	public static void main(String[] args) {
		SpringApplication.run(PracaiApplication.class, args);
	}

}
