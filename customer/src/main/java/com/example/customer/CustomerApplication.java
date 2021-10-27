package com.example.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootApplication
public class CustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	private static String [] names= "Jay, Surendra, Baljeet, Amar, Tarun".split(", ");

	private final AtomicInteger counter = new AtomicInteger();

	private  final Flux<Customer> customers = Flux.fromStream(
			Stream.generate(() -> {
				var id = counter.incrementAndGet();
				return new Customer(id, names[id % names.length]);
			})
	).delayElements(Duration.ofSeconds(3));

	@Bean
	Flux<Customer> customer (){
		return this.customers.publish().autoConnect();
	}

}
@Configuration
@RequiredArgsConstructor
class CustomerWebSocketConfiguration{

	private  final ObjectMapper objectMapper;

	@SneakyThrows
	private  String form(Customer customer){
		return this.objectMapper.writeValueAsString(customer);
	}

	@Bean
	WebSocketHandler webSocketHandler(Flux<Customer> customerFlux){
		return webSocketSession -> {
			var map = customerFlux
					.map(this::form)
					.map(webSocketSession::textMessage);
			return webSocketSession.send(map);
		};
	}

	@Bean
	SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler customerWsh){
		return new SimpleUrlHandlerMapping(Map.of("/ws/customers",customerWsh), 10);
	}
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {
	private final Flux<Customer> customerFlux;
	// Safari browsers are causing issue in websocket stream
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE,
	value = "/customers")
	Flux<Customer> get(){
		return this.customerFlux;
	}
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
	private Integer id;
	private  String name;
}
