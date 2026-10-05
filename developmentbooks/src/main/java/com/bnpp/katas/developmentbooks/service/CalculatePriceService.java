package com.bnpp.katas.developmentbooks.service;

import java.math.BigDecimal;
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

	private static final double NO_DISCOUNT = BigDecimal.ZERO.doubleValue();
	private static final int HUNDRED = 100;
	private static final int ZERO_PERCENT = BigDecimal.ZERO.intValue();
	private static final int ONE_QUANTITY = BigDecimal.ONE.intValue();

	public PriceSummaryDto getPriceSummary(List<BookDto> listOfBooks) {
		validateBooks(listOfBooks);
		Map<Integer, Integer> bookIdQuantityMap = listOfBooks.stream()
				.collect(Collectors.toMap(BookDto::getId, BookDto::getQuantity));
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
		double discount = listOfBookGroup.stream().mapToDouble(BookGroup::getDiscount).sum();
		if (discount >= priceSummaryDto.getTotalDiscount()) {
			priceSummaryDto.setListOfBookGroups(listOfBookGroup);
			double actualPrice = listOfBookGroup.stream().mapToDouble(BookGroup::getActualPrice).sum();
			priceSummaryDto.setActualPrice(actualPrice);
			priceSummaryDto.setTotalDiscount(discount);
			priceSummaryDto.setFinalPrice(actualPrice - discount);
		}
	}

	private void validateBooks(List<BookDto> listOfBooks) {
		Map<Integer, Double> bookIdPriceMap = getBookIdPriceMap();
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
		Map<Integer, Double> bookIdPriceMap = getBookIdPriceMap();
		Set<Integer> bookIds = bookIdQuantityMap.keySet();
		double actualPrice = bookIds.stream()
				.mapToDouble(bookId -> bookIdPriceMap.get(bookId) * bookIdQuantityMap.get(bookId)).sum();
		int numberOfBooks = bookIdQuantityMap.values().stream().mapToInt(Integer::intValue).sum();
		return new BookGroup(bookIds.stream().collect(Collectors.toList()), ZERO_PERCENT, actualPrice, NO_DISCOUNT, numberOfBooks);
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
		Map<Integer, Double> bookIdPriceMap = getBookIdPriceMap();
		double actualPrice = listOfBookToGroup.stream().mapToDouble(bookId -> bookIdPriceMap.get(bookId) * ONE_QUANTITY)
				.sum();
		int discountPercentage = getDiscountPercentage(listOfBookToGroup.size());
		double discount = (actualPrice * discountPercentage) / HUNDRED;
		return new BookGroup(listOfBookToGroup, discountPercentage, actualPrice, discount, listOfBookToGroup.size());

	}

	private Map<Integer, Double> getBookIdPriceMap() {
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