package com.example.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@SpringBootApplication
public class ConsumerApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerApplication.class);

	@Bean
	IntegrationFlow integrationFlow() {

		Flux<Date> datePublisher =
				Flux
				.<Date>generate(synchronousSink -> synchronousSink.next(new Date()))
				.delayElements(Duration.ofSeconds(1));

		return IntegrationFlows
				.from(datePublisher.map(date -> MessageBuilder.withPayload(date).build()))
				.handle((GenericHandler<Date>) (date, messageHeaders) -> {
					LOG.info("Date is: {}",date.toInstant());
					messageHeaders.forEach((key,value) -> LOG.info("Key is {}. Value is {}", key,value));
					return date.after(Date.from(Instant.now()));
				})
				.get();

	}


	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}

