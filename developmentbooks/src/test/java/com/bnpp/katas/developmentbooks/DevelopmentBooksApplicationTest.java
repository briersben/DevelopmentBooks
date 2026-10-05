package com.bnpp.katas.developmentbooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DevelopmentBooksApplicationTest {

	@Autowired
	private ModelMapper modelMapper;
	
	@Test
	@DisplayName("Development books application context should not be null")
	void developmentbooks_applicationcontext_shouldnotbenull(ApplicationContext context) {
		assertNotNull(context, "Development books application context loaded successfully");
	}

	@Test
	@DisplayName("modelMapper bean should not be null")
	void modelMapper_shouldNotBeNull() {
		assertThat(modelMapper).isNotNull();
	}
}