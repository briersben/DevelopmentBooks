package com.bnpp.katas.developmentbooks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bnpp.katas.developmentbooks.dto.BookDto;
import com.bnpp.katas.developmentbooks.service.CalculatePriceService;
import com.bnpp.katas.developmentbooks.service.DevelopmentBooksService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = DevelopmentBooksController.class)
class DevelopmentBooksControllerTest {

	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.getbooks}")
	private String GETBOOKS_ENDPOINT;

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.pricesummary}")
	private String FETCH_PRICE_SUMMARY_ENDPOINT;

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.getDiscountDetails}")
	private String GET_DISCOUNT_DETAILS_ENDPOINT;

	@Autowired
	private DevelopmentBooksController developmentBooksController;

	@MockBean
	private DevelopmentBooksService developmentBooksService;

	@MockBean
	private CalculatePriceService calculatePriceService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("DevelopmentBooks controller bean should not be null")
	void developmentBooksController_shouldNotBeNull() {
		assertThat(developmentBooksController).isNotNull();
	}

	@Test
	@DisplayName("API getBooks should return status OK")
	void getBooks_Api_shouldReturn_StatusOK() throws Exception {
		mockMvc.perform(get(GETBOOKS_ENDPOINT)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("API fetchPriceSummary should return status OK")
	void fetchPriceSummary_Api_shouldReturn_StatusOK() throws Exception {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, TWO);
		BookDto thirdBook = new BookDto(THREE, THREE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);

		mockMvc.perform(post(FETCH_PRICE_SUMMARY_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(listOfBooks))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("API getDiscountDetails should return status OK")
	void getDiscountDetails_Api_shouldReturn_StatusOK() throws Exception {
		mockMvc.perform(get(GET_DISCOUNT_DETAILS_ENDPOINT)).andExpect(status().isOk());
	}

}
