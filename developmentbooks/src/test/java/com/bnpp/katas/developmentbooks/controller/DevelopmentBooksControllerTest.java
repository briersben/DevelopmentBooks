package com.bnpp.katas.developmentbooks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bnpp.katas.developmentbooks.dto.BookDto;
import com.bnpp.katas.developmentbooks.exceptions.BookNotFoundException;
import com.bnpp.katas.developmentbooks.service.CalculatePriceService;
import com.bnpp.katas.developmentbooks.service.DevelopmentBooksService;

import tools.jackson.databind.json.JsonMapper;

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

	@MockitoBean
	private DevelopmentBooksService developmentBooksService;

	@MockitoBean
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
				.content(new JsonMapper().writeValueAsString(listOfBooks))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("API fetchPriceSummary should return 404 with a structured error when a book id is missing")
	void fetchPriceSummary_Api_shouldReturnNotFoundForMissingBookId() throws Exception {
		when(calculatePriceService.getPriceSummary(anyList()))
				.thenThrow(new BookNotFoundException(List.of(THREE)));

		mockMvc.perform(post(FETCH_PRICE_SUMMARY_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(new JsonMapper().writeValueAsString(List.of(new BookDto(THREE, ONE)))))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Book id's not found : [3]"));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1 })
	@DisplayName("API fetchPriceSummary should reject non-positive quantities")
	void fetchPriceSummary_Api_shouldRejectNonPositiveQuantity(int quantity) throws Exception {
		mockMvc.perform(post(FETCH_PRICE_SUMMARY_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(new JsonMapper().writeValueAsString(List.of(new BookDto(ONE, quantity)))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Request validation failed"));
	}

	@Test
	@DisplayName("API fetchPriceSummary should reject a non-positive book id")
	void fetchPriceSummary_Api_shouldRejectNonPositiveBookId() throws Exception {
		mockMvc.perform(post(FETCH_PRICE_SUMMARY_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(new JsonMapper().writeValueAsString(List.of(new BookDto(0, ONE)))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Request validation failed"));
	}

	@Test
	@DisplayName("API getDiscountDetails should return status OK")
	void getDiscountDetails_Api_shouldReturn_StatusOK() throws Exception {
		mockMvc.perform(get(GET_DISCOUNT_DETAILS_ENDPOINT)).andExpect(status().isOk());
	}

}
