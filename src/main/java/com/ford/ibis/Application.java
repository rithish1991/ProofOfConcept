package com.ford.ibis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ford.ibis.component.TrafficLightComponent;

@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Bean
	public CommandLineRunner runOnStartup(final TrafficLightComponent trafficLightComponent) {
		return (args) -> {
			trafficLightComponent.buildMetricTypes();
		};
	}

	
}
