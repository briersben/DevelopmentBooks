package com.bnpp.katas.developmentbooks.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bnpp.katas.developmentbooks.store.DevelopmentBooksEnum;

@SpringBootTest
class BookTest {

	@Autowired
	private ModelMapper modelMapper;

	@Test
	@DisplayName("DevelopmentBooksEnum to book should map all fields correctly")
	void developmentBooksEnumToBook_shouldMapAllFieldsCorrectly() {
		DevelopmentBooksEnum cleanCodebookEnum = DevelopmentBooksEnum.CLEAN_CODE;

		Book cleanCodeBook = modelMapper.map(cleanCodebookEnum, Book.class);

		assertEquals(cleanCodebookEnum.getId(), cleanCodeBook.getId(), "Book Id mapping successful");
		assertEquals(cleanCodebookEnum.getTitle(), cleanCodeBook.getTitle(), "Book title mapping successful");
		assertEquals(cleanCodebookEnum.getAuthor(), cleanCodeBook.getAuthor(), "Book author mapping successful");
		assertEquals(cleanCodebookEnum.getYear(), cleanCodeBook.getYear(), "Book year mapping successful");
		assertEquals(cleanCodebookEnum.getPrice(), cleanCodeBook.getPrice(), "Book price mapping successful");
		assertEquals(cleanCodebookEnum.getImageUrl(), cleanCodeBook.getImageUrl(), "Book image url mapping successful");
	}
}
