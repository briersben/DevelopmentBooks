package com.bnpp.katas.developmentbooks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bnpp.katas.developmentbooks.dto.Book;
import com.bnpp.katas.developmentbooks.dto.BookDto;
import com.bnpp.katas.developmentbooks.dto.PriceSummaryDto;
import com.bnpp.katas.developmentbooks.service.CalculatePriceService;
import com.bnpp.katas.developmentbooks.service.DevelopmentBooksService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${developmentbooks.controller.path}")
@Tag(name = "Development Books")
public class DevelopmentBooksController {

	@Autowired
	private DevelopmentBooksService developmentBooksService;

	@Autowired
	private CalculatePriceService calculatePriceService;

	@Operation(summary = "List all available development books")
	@GetMapping("${developmentbooks.endpoints.getbooks}")
	public List<Book> getBooks() {
		return developmentBooksService.getBooks();
	}

	@Operation(summary = "Compute the best price for a basket of books, applying bulk-set discounts")
	@PostMapping("${developmentbooks.endpoints.pricesummary}")
	public PriceSummaryDto fetchPriceSummary(@RequestBody List<BookDto> listOfBooks) {
		return calculatePriceService.getPriceSummary(listOfBooks);
	}

	@Operation(summary = "Get the discount percentage applicable per number of different books")
	@GetMapping("${developmentbooks.endpoints.getDiscountDetails}")
	public Map<Integer, Integer> getDiscountDetails() {
		return developmentBooksService.getDiscountDetails();
	}

}
