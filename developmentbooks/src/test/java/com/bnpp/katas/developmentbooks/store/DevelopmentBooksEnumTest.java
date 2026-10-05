package com.bnpp.katas.developmentbooks.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DevelopmentBooksEnumTest {

	private static final int NUMBER_OF_DEVELOPMENT_BOOKS = 5;

	@Test
	@DisplayName("DevelopmentBooksEnum should contain five development books")
	void developmentBooksEnum_shouldContainFiveDevelopmentBooks() {
		assertEquals(NUMBER_OF_DEVELOPMENT_BOOKS, DevelopmentBooksEnum.values().length);
	}

}
