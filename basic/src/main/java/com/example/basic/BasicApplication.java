package com.example.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicApplication.class, args);
	}

	@Bean
	RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder
				.routes()
				.route(routeSpec -> routeSpec
						.path("/customers")
						.uri("lb://customers/")
				).build();
	}


	/* How to use route
	@Bean
	RouteLocator gateway (RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder
				.routes()
				.route(routSpec -> routSpec
						.path("/hello")
//						.filters(gatewayFilterSpec ->
//								gatewayFilterSpec
//										.setPath("/guides")
//						)
						.uri("https://spring.io/")
				)
				.route("/twitter", routeSpec ->
						routeSpec
							.path("/twitter/**")
							.filters(fs -> fs.rewritePath(
									"/twitter/(?<handle>.*)",
									"/${handle}"
							))
						.uri("http://twitter.com/@")

				)
				.build();

	}


 */
}
