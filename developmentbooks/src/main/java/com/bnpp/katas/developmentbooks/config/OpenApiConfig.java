package com.bnpp.katas.developmentbooks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI developmentBooksOpenAPI() {
		return new OpenAPI().info(new Info().title("Development Books Kata API")
				.description("Calculates the best price for a basket of Development Books, applying bulk-set discounts.")
				.version("1.0.0"));
	}
}
