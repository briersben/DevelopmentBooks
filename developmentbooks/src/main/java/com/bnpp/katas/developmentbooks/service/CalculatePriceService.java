package com.bnpp.katas.developmentbooks.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import com.bnpp.katas.developmentbooks.dto.BookDto;
import com.bnpp.katas.developmentbooks.dto.BookGroup;
import com.bnpp.katas.developmentbooks.dto.PriceSummaryDto;
import com.bnpp.katas.developmentbooks.exceptions.BookNotFoundException;
import com.bnpp.katas.developmentbooks.store.DevelopmentBooksEnum;
import com.bnpp.katas.developmentbooks.store.DiscountProviderEnum;

@Service
public class CalculatePriceService {

	private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final int ZERO_PERCENT = BigDecimal.ZERO.intValue();
	private static final int ONE_QUANTITY = BigDecimal.ONE.intValue();

	public PriceSummaryDto getPriceSummary(List<BookDto> listOfBooks) {
		validateBooks(listOfBooks);
		// merge quantities when the same book id appears in multiple entries
		Map<Integer, Integer> bookIdQuantityMap = listOfBooks.stream()
				.collect(Collectors.toMap(BookDto::getId, BookDto::getQuantity, Integer::sum));
		List<Integer> listOfApplicableDiscounts = getApplicableDiscounts(bookIdQuantityMap.size());
		PriceSummaryDto priceSummaryDto = new PriceSummaryDto();
		if (CollectionUtils.isNotEmpty(listOfApplicableDiscounts)) {
			updatePriceSummaryWithDiscount(bookIdQuantityMap, listOfApplicableDiscounts, priceSummaryDto);
		} else {
			updatePriceSummaryWithoutDiscount(bookIdQuantityMap, priceSummaryDto);
		}
		return priceSummaryDto;
	}

	private void updatePriceSummaryWithoutDiscount(Map<Integer, Integer> bookIdQuantityMap,
			PriceSummaryDto priceSummaryDto) {
		BookGroup booksWithoutDiscount = getBookGroupWithoutDiscount(bookIdQuantityMap);
		List<BookGroup> listOfBookGroup = new ArrayList<>();
		listOfBookGroup.add(booksWithoutDiscount);
		updateBestDiscount(priceSummaryDto, listOfBookGroup);
	}

	private void updatePriceSummaryWithDiscount(Map<Integer, Integer> bookIdQuantityMap,
			List<Integer> listOfApplicableDiscounts, PriceSummaryDto priceSummaryDto) {
		listOfApplicableDiscounts.stream().forEach(numberOfBooksToGroup -> {
			Map<Integer, Integer> bookIdQuantityMapCopy = cloneMap(bookIdQuantityMap);
			List<BookGroup> listOfBookGroup = getBookGroupswithDiscount(bookIdQuantityMapCopy, new ArrayList<>(),
					numberOfBooksToGroup);
			if (CollectionUtils.isNotEmpty(bookIdQuantityMapCopy.keySet())) {
				BookGroup booksWithoutDiscount = getBookGroupWithoutDiscount(bookIdQuantityMapCopy);
				listOfBookGroup.add(booksWithoutDiscount);
			}
			updateBestDiscount(priceSummaryDto, listOfBookGroup);
		});
	}

	private Map<Integer, Integer> cloneMap(Map<Integer, Integer> bookIdQuantityMap) {
		return bookIdQuantityMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (firstKey, secondKey) -> secondKey,
						LinkedHashMap::new));
	}

	private void updateBestDiscount(PriceSummaryDto priceSummaryDto, List<BookGroup> listOfBookGroup) {
		BigDecimal discount = listOfBookGroup.stream().map(BookGroup::getDiscount)
				.reduce(ZERO_MONEY, BigDecimal::add);
		if (discount.compareTo(priceSummaryDto.getTotalDiscount()) >= 0) {
			priceSummaryDto.setListOfBookGroups(listOfBookGroup);
			BigDecimal actualPrice = listOfBookGroup.stream().map(BookGroup::getActualPrice)
					.reduce(ZERO_MONEY, BigDecimal::add);
			priceSummaryDto.setActualPrice(actualPrice);
			priceSummaryDto.setTotalDiscount(discount);
			priceSummaryDto.setFinalPrice(actualPrice.subtract(discount));
		}
	}

	private void validateBooks(List<BookDto> listOfBooks) {
		Map<Integer, BigDecimal> bookIdPriceMap = getBookIdPriceMap();
		List<Integer> missingBookIds = listOfBooks.stream()
				.filter(book -> BooleanUtils.isFalse(bookIdPriceMap.containsKey(book.getId()))).map(BookDto::getId)
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(missingBookIds)) {
			throw new BookNotFoundException(missingBookIds);
		}
	}

	private List<BookGroup> getBookGroupswithDiscount(Map<Integer, Integer> bookIdQuantityMap,
			List<BookGroup> bookGroup, Integer numberOfBooksToGroup) {
		numberOfBooksToGroup = getNumberOfBooksToGroup(bookIdQuantityMap, numberOfBooksToGroup);
		Optional<DiscountProviderEnum> discount = getDiscount(numberOfBooksToGroup);
		if (discount.isPresent()) {
			int bookToGroupBasedOnDiscount = discount.get().getNumberOfDistinctItems();
			List<Integer> listOfDistinctBooks = bookIdQuantityMap.keySet().stream().limit(bookToGroupBasedOnDiscount)
					.collect(Collectors.toList());
			BookGroup currentBookGroup = getBookGroup(listOfDistinctBooks);
			bookGroup.add(currentBookGroup);
			cleanupDiscountedItems(bookIdQuantityMap, listOfDistinctBooks);
			getBookGroupswithDiscount(bookIdQuantityMap, bookGroup, numberOfBooksToGroup);
		}
		return bookGroup;
	}

	private int getNumberOfBooksToGroup(Map<Integer, Integer> bookIdQuantityMap, Integer numberOfBooksToGroup) {
		return numberOfBooksToGroup < bookIdQuantityMap.size() ? numberOfBooksToGroup : bookIdQuantityMap.size();
	}

	private BookGroup getBookGroupWithoutDiscount(Map<Integer, Integer> bookIdQuantityMap) {
		Map<Integer, BigDecimal> bookIdPriceMap = getBookIdPriceMap();
		Set<Integer> bookIds = bookIdQuantityMap.keySet();
		BigDecimal actualPrice = bookIds.stream()
				.map(bookId -> bookIdPriceMap.get(bookId).multiply(BigDecimal.valueOf(bookIdQuantityMap.get(bookId))))
				.reduce(ZERO_MONEY, BigDecimal::add);
		int numberOfBooks = bookIdQuantityMap.values().stream().mapToInt(Integer::intValue).sum();
		return new BookGroup(bookIds.stream().collect(Collectors.toList()), ZERO_PERCENT, actualPrice, ZERO_MONEY,
				numberOfBooks);
	}

	private List<Integer> getApplicableDiscounts(int numberOfBooks) {
		return Arrays.stream(DiscountProviderEnum.values()).sorted(Comparator.reverseOrder())
				.filter(discountGroup -> discountGroup.getNumberOfDistinctItems() <= numberOfBooks)
				.map(DiscountProviderEnum::getNumberOfDistinctItems).collect(Collectors.toList());
	}

	private Optional<DiscountProviderEnum> getDiscount(int numberOfBooks) {
		return Arrays.stream(DiscountProviderEnum.values()).sorted(Comparator.reverseOrder())
				.filter(discountGroup -> discountGroup.getNumberOfDistinctItems() <= numberOfBooks).findFirst();
	}

	private BookGroup getBookGroup(List<Integer> listOfBookToGroup) {
		Map<Integer, BigDecimal> bookIdPriceMap = getBookIdPriceMap();
		BigDecimal actualPrice = listOfBookToGroup.stream()
				.map(bookId -> bookIdPriceMap.get(bookId).multiply(BigDecimal.valueOf(ONE_QUANTITY)))
				.reduce(ZERO_MONEY, BigDecimal::add);
		int discountPercentage = getDiscountPercentage(listOfBookToGroup.size());
		BigDecimal discount = actualPrice.multiply(BigDecimal.valueOf(discountPercentage))
				.divide(HUNDRED, 2, RoundingMode.HALF_UP);
		return new BookGroup(listOfBookToGroup, discountPercentage, actualPrice, discount, listOfBookToGroup.size());
	}

	private Map<Integer, BigDecimal> getBookIdPriceMap() {
		return Arrays.stream(DevelopmentBooksEnum.values())
				.collect(Collectors.toMap(DevelopmentBooksEnum::getId, DevelopmentBooksEnum::getPrice));
	}

	private void cleanupDiscountedItems(Map<Integer, Integer> bookIdQuantityMap, List<Integer> discountedBooks) {
		discountedBooks.forEach(bookId -> {
			int bookQuantities = bookIdQuantityMap.get(bookId);
			if (bookQuantities > ONE_QUANTITY) {
				bookIdQuantityMap.put(bookId, bookQuantities - ONE_QUANTITY);
			} else {
				bookIdQuantityMap.remove(bookId);
			}
		});
	}

	private int getDiscountPercentage(int numberOfDistinctBooks) {
		Optional<DiscountProviderEnum> discount = getDiscount(numberOfDistinctBooks);
		return (discount.isPresent()) ? discount.get().getDiscountPercentage() : ZERO_PERCENT;
	}

}