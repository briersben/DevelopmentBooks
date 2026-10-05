package com.bnpp.katas.developmentbooks.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bnpp.katas.developmentbooks.dto.BookDto;
import com.bnpp.katas.developmentbooks.dto.BookGroup;
import com.bnpp.katas.developmentbooks.dto.PriceSummaryDto;
import com.bnpp.katas.developmentbooks.exceptions.BookNotFoundException;

@SpringBootTest
class CalculatePriceServiceTest {

	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int FIVE = 5;
	private static final int SIX = 6;
	private static final int SEVEN = 7;
	private static final int TWENTY = 20;
	private static final int FOURTY = 40;
	private static final double BOOK_PRICE = 50.00;
	private static final double PRICE_OF_TWO_DISTINCT_BOOKS = 95.00;
	private static final double PRICE_OF_THREE_DISTINCT_BOOKS = 135.00;
	private static final double PRICE_OF_FOUR_DISTINCT_BOOKS = 160.00;
	private static final double PRICE_OF_FIVE_DISTINCT_BOOKS = 187.50;
	private static final double PRICE_OF_THREE_BOOKS_AFTER_APPLY_DISCOUNT_FOR_TWO = 145.00;
	private static final double PRICE_OF_BOOKS_APPLY_DISCOUNT_TO_DISTINCT_BOOKS = 370.00;
	private static final double ACTUALPRICE_OF_FOUR_BOOKS = 200.00;
	private static final double ACTUALPRICE_OF_EIGHT_BOOKS = 400.00;
	private static final double DISCOUNTPRICE_FOR_EIGHT_BOOKS = 80.00;
	private static final double PRICE_OF_EIGHT_BOOKS_AFTER_DISCOUNT = 320.00;

	@Autowired
	private CalculatePriceService calculatePriceService;

	@Test
	@DisplayName("calculate price for a book should return 50")
	void calculatePriceForABook_shouldReturnFifty() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto bookDto = new BookDto(ONE, ONE);
		listOfBooks.add(bookDto);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(BOOK_PRICE, finalPrice);
	}

	@ParameterizedTest
	@CsvSource({ "1,50", "2,100", "3,150", "4,200", "10,500" })
	@DisplayName("calculate price should return price based on quantities")
	void calculatePrice_shouldReturnPriceBasedOnQuantity(int quantities, double expectedPrice) {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto bookDto = new BookDto(ONE, quantities);
		listOfBooks.add(bookDto);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(expectedPrice, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply 5% discount for two distinct books")
	void calculatePrice_shouldApplyFivePercentDiscountForTwoDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_TWO_DISTINCT_BOOKS, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply 10% for three distinct books")
	void calculatePrice_shouldApplyTenPercentDiscountForThreeDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, ONE);
		BookDto thirdBook = new BookDto(THREE, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_THREE_DISTINCT_BOOKS, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply 20% discount for four distinct books")
	void calculatePrice_shouldApplyTwentyPercentDiscountForFourDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, ONE);
		BookDto thirdBook = new BookDto(THREE, ONE);
		BookDto fourBook = new BookDto(FOUR, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);
		listOfBooks.add(fourBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_FOUR_DISTINCT_BOOKS, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply 25% discount for five distinct books")
	void calculatePrice_shouldApplyTwentyFivePercentDiscountForFiveDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, ONE);
		BookDto thirdBook = new BookDto(THREE, ONE);
		BookDto fourBook = new BookDto(FOUR, ONE);
		BookDto fifthBook = new BookDto(FIVE, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);
		listOfBooks.add(fourBook);
		listOfBooks.add(fifthBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_FIVE_DISTINCT_BOOKS, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply 5% discount to only two distinct books")
	void calculatePrice_shouldApplyFivePercentDiscountOnlyForTwoDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, TWO);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_THREE_BOOKS_AFTER_APPLY_DISCOUNT_FOR_TWO, finalPrice);
	}

	@Test
	@DisplayName("calculate price should apply discount to all distinct books")
	void calculatePrice_shouldApplyDiscountToAllDistinctBooks() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(TWO, TWO);
		BookDto thirdBook = new BookDto(THREE, THREE);
		BookDto fourBook = new BookDto(FOUR, TWO);
		BookDto fifthBook = new BookDto(FIVE, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);
		listOfBooks.add(fourBook);
		listOfBooks.add(fifthBook);

		Double finalPrice = calculatePriceService.getPriceSummary(listOfBooks).getFinalPrice();

		assertEquals(PRICE_OF_BOOKS_APPLY_DISCOUNT_TO_DISTINCT_BOOKS, finalPrice);
	}

	@Test
	@DisplayName("fetch price summary should throw book not found exception on invalid book id")
	void fetchPriceSummary_shouldThrowBookNotFoundExceptionOnInvalidBookId() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, ONE);
		BookDto secondBook = new BookDto(SIX, ONE);
		BookDto thirdBook = new BookDto(SEVEN, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);

		assertThrows(BookNotFoundException.class, () -> calculatePriceService.getPriceSummary(listOfBooks));
	}

	@Test
	@DisplayName("fetch price summary should return price summary with best discount")
	void calculatePrice_shouldReturnPriceSummaryWithBestDiscount() {
		List<BookDto> listOfBooks = new ArrayList<BookDto>();
		BookDto firstBook = new BookDto(ONE, TWO);
		BookDto secondBook = new BookDto(TWO, TWO);
		BookDto thirdBook = new BookDto(THREE, TWO);
		BookDto fourBook = new BookDto(FOUR, ONE);
		BookDto fifthBook = new BookDto(FIVE, ONE);
		listOfBooks.add(firstBook);
		listOfBooks.add(secondBook);
		listOfBooks.add(thirdBook);
		listOfBooks.add(fourBook);
		listOfBooks.add(fifthBook);

		PriceSummaryDto priceSummary = calculatePriceService.getPriceSummary(listOfBooks);

		assertEquals(TWO, priceSummary.getListOfBookGroups().size(),
				"Best discount price is by grouping books into 2 groups");
		for (BookGroup bookGroups : priceSummary.getListOfBookGroups()) {
			assertEquals(FOUR, bookGroups.getNumberOfBooks(), "Group should have 4 books each to get best discount");
			assertEquals(TWENTY, bookGroups.getDiscountPercentage(), "Best discount is with 20% discount");
			assertEquals(FOURTY, bookGroups.getDiscount(), "Discount for 4 books with 20% should be 40");
			assertEquals(ACTUALPRICE_OF_FOUR_BOOKS, bookGroups.getActualPrice(), "Actual price for 4 books is 200");
			assertEquals(FOUR, bookGroups.getListOfbooks().size(), "Each list should have 4 book id");
		}
		assertEquals(ACTUALPRICE_OF_EIGHT_BOOKS, priceSummary.getActualPrice());
		assertEquals(DISCOUNTPRICE_FOR_EIGHT_BOOKS, priceSummary.getTotalDiscount());
		assertEquals(PRICE_OF_EIGHT_BOOKS_AFTER_DISCOUNT, priceSummary.getFinalPrice());
	}
}