package com.ticketrush;

import org.springframework.boot.SpringApplication;

public class TestTicketRushApplication {

	public static void main(String[] args) {
		SpringApplication.from(TicketRushApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
